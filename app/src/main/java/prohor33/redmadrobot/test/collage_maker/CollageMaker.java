package prohor33.redmadrobot.test.collage_maker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import prohor33.redmadrobot.test.R;
import prohor33.redmadrobot.test.instagram_api.InstagramAPI;
import prohor33.redmadrobot.test.instagram_api.Storage;
import prohor33.redmadrobot.test.utility.ImageLoadAsyncTask;
import prohor33.redmadrobot.test.utility.SimpleAsyncListener;
import prohor33.redmadrobot.test.utility.SimpleAsyncTask;

/**
 * Created by prohor on 25/02/15.
 */
public class CollageMaker {

    /**
     * Interface for catching result of operations.
     */
    public interface Listener {
        public abstract void onSuccess();
        public abstract void onFail(String error);
    }

    private static final String TAG = "CollageMaker";
    private static final int INSTAGRAM_STANDARD_RESOLUTION = 640;
    private static final int INSTAGRAM_LOW_RESOLUTION = 306;
    private static final int INSTAGRAM_THUMBNAIL_RES = 150;

    private static final int COLLAGE_PXL_SIZE_STANDARD = 1280;
    private static final int COLLAGE_PXL_SIZE_LOW = 306 * 3;
    private static final int COLLAGE_PXL_SIZE_THUMBNAIL = 150 * 3;

    private static CollageMaker instance;
    private static Context mainActivity;
    private Listener listener;
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private final int defaultImageInCollageCount = 9;
    private int imageInCollageCount;
    private ImageSize targetCollageImageSize;
    private ArrayList<ImageLoadAsyncTask> currentLoaderTasks = new ArrayList<>();
    private Bitmap collageBitmap;
    private boolean showingCollage = false;

    public enum ImageSize {
        standard_resolution,
        low_resolution,
        thumbnail
    };

    public static synchronized CollageMaker getInstance() {
        if (instance == null) {
            instance = new CollageMaker();
        }
        return instance;
    }

    public static CollageMaker with(Listener listener) {
        getInstance().attachListener(listener);
        return instance;
    }

    public static CollageMaker with() {
        return with(null);
    }

    public static void putContext(Context a) {
        mainActivity = a;
    }

    public static void generateCollagePreview() {
        getInstance().targetCollageImageSize = ImageSize.thumbnail;
        getInstance().generateCollageImpl(ImageSize.thumbnail);
    }

    public static void generateCollage() {
        getInstance().targetCollageImageSize = ImageSize.standard_resolution;
        getInstance().generateCollageImpl(ImageSize.standard_resolution);
    }
    private void generateCollageImpl(ImageSize imageSize) {
        // 1) start ImageLoadAsyncTask
        // 2) addLoadedBitmap()
        // 3) buildCollage()
        // 4) listener -> onSuccess()

        ArrayList<Storage.ImageInfo> images = InstagramAPI.getImages();

        Collections.sort(images, new Comparator<Storage.ImageInfo>() {
            @Override
            public int compare(Storage.ImageInfo imageInfo, Storage.ImageInfo imageInfo2) {
                return imageInfo2.likes_count - imageInfo.likes_count;
            }
        });

        cancelAllTasks();
        imageInCollageCount = Math.min(defaultImageInCollageCount, images.size());
        if (imageInCollageCount == 0)
            listener.onFail(mainActivity.getString(R.string.user_have_no_public_media));

        for (int i = 0; i < imageInCollageCount; i++) {
            ImageLoadAsyncTask task =
                    ImageLoader.loadImage(getImageResolution(images.get(i), imageSize).url);
            currentLoaderTasks.add(task);
        }
    }

    // calling from main thread
    public static void addLoadedBitmap(Bitmap bmp) {
        getInstance().addLoadedBitmapImpl(bmp);
    }
    private void addLoadedBitmapImpl(Bitmap bmp) {
        Log.d(TAG, "Image loaded");
        bitmaps.add(bmp);
        if (bitmaps.size() >= imageInCollageCount)
            tryBuildCollage();
    }

    public static void onFailedToLoadImage() {
        getInstance().onFailedToLoadImageImpl();
    }
    private void onFailedToLoadImageImpl() {
        Log.e(TAG, "Failed to load images => trying to reload smaller");
        // TODO: check problem, not just reload (network, etc)
        reloadCollage();
    }

    public static Bitmap getCollageBitmap() {
        return getInstance().collageBitmap;
    }

    public static void showCollage(boolean show) {
        getInstance().showCollageImpl(show);
    }
    private void showCollageImpl(boolean show) {
        getInstance().showingCollage = show;
        if (!show)
            collageBitmap = null;
    }
    public static boolean isShowingCollage() {
        return getInstance().showingCollage;
    }

    public static float getCollageImageAspectRatio() {
        return getInstance().getCollageImageAspectRatioImpl();
    }
    private float getCollageImageAspectRatioImpl() {
        if (collageBitmap == null)
            return 1.0f;
        return ((float) collageBitmap.getHeight()) / collageBitmap.getWidth();
    }

    // private members only ==================
    private void reloadCollage() {
        cancelAllTasks();
        int i = targetCollageImageSize.ordinal() + 1;
        if (ImageSize.values().length >= i) {
            listener.onFail("Error: network error?");
            return;
        }
        generateCollageImpl(ImageSize.values()[i]);
    }

    private void tryBuildCollage() {
        Log.d(TAG, "All images loaded. Start building collage...");
        try {
            buildCollage();
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "Out of memory => reload smaller");
            reloadCollage();
            return;
        }
    }
    private void buildCollage() {
        new SimpleAsyncTask(new SimpleAsyncListener() {
            Bitmap bitmap;

            @Override
            public void onSuccess() {
                Log.d(TAG, "Collage successfully generated!");
                collageBitmap = bitmap;
                listener.onSuccess();
            }

            @Override
            public void onError(String error) {
                listener.onFail(error);
            }

            @Override
            public Boolean doInBackground() {
                bitmap = buildCollageImpl();
                return bitmap != null;
            }
        }).execute();
    }
    private Bitmap buildCollageImpl() {
        final int collage_size_x = getCollageSize(targetCollageImageSize);

        int count_y = (int) Math.sqrt(imageInCollageCount);
        int count_x = imageInCollageCount / count_y;
        int padding = collage_size_x / 100;
        int img_size = (collage_size_x - padding * (count_x + 1)) / count_x;

        Log.d(TAG, "count " + count_x + "x" + count_y);
        Log.d(TAG, "imageInCollageCount " + imageInCollageCount);
        Log.d(TAG, "img_size " + img_size + "x" + img_size);

        Bitmap collageImage = Bitmap.createBitmap(img_size * count_x + padding * (count_x + 1),
                img_size * count_y + padding * (count_y + 1), Bitmap.Config.ARGB_8888);
        Canvas collageCanvas = new Canvas(collageImage);
        collageCanvas.drawColor(mainActivity.getResources().getColor(R.color.white));

        for (int y = 0; y < count_y; y++) {
            for (int x = 0; x < count_x; x++) {
                int i = count_x * y + x;
                Bitmap bitmap = bitmaps.get(i);

                Point place_size = new Point(img_size, img_size);
                float place_aspect = (float)place_size.y / place_size.x;
                float image_aspect = (float)bitmap.getHeight() / bitmap.getWidth();

                Point img_t_size = new Point();
                if (place_aspect > image_aspect) {
                    img_t_size.x = (int)(bitmap.getHeight() / place_aspect);
                    img_t_size.y = bitmap.getHeight();
                } else {
                    img_t_size.x = bitmap.getWidth();
                    img_t_size.y = (int)(bitmap.getWidth() * place_aspect);
                }

                // crop square in center
                // but if image originally with white spaces, this doesn't helps, obviously
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, img_t_size.x, img_t_size.y);
                bitmap = Bitmap.createScaledBitmap(bitmap, place_size.x, place_size.y, true);

                collageCanvas.drawBitmap(bitmap,
                        x * img_size + padding * (x + 1),
                        y * img_size + padding * (y + 1),
                        new Paint(Paint.FILTER_BITMAP_FLAG));
            }
        }

        return collageImage;
    }

    private Storage.ImageInfo.ImageResolution getImageResolution(Storage.ImageInfo image, ImageSize imageSize) {
        switch (imageSize) {
            case low_resolution:
                return image.low_resolution;
            case standard_resolution:
                return image.standard_resolution;
            case thumbnail:
                return image.thumbnail;
        }
        throw new IllegalArgumentException();
    }

    private int getImageSize(ImageSize imageSize) {
        switch (imageSize) {
            case low_resolution:
                return INSTAGRAM_LOW_RESOLUTION;
            case standard_resolution:
                return INSTAGRAM_STANDARD_RESOLUTION;
            case thumbnail:
                return INSTAGRAM_THUMBNAIL_RES;
        }
        throw new IllegalArgumentException();
    }

    private int getCollageSize(ImageSize imageSize) {
        switch (imageSize) {
            case low_resolution:
                return COLLAGE_PXL_SIZE_LOW;
            case standard_resolution:
                return COLLAGE_PXL_SIZE_STANDARD;
            case thumbnail:
                return COLLAGE_PXL_SIZE_THUMBNAIL;
        }
        throw new IllegalArgumentException();
    }

    private void cancelAllTasks() {
        for (ImageLoadAsyncTask task : currentLoaderTasks) {
            task.cancel();
        }
        currentLoaderTasks.clear();
        bitmaps.clear();
    }

    // this func must control that listener is not null.
    private void attachListener(Listener listener) {
        if (listener == null) {
            this.listener = mDefaultListener;
        } else {
            this.listener = listener;
        }
    }

    // default Listener
    private Listener mDefaultListener = new Listener() {
        @Override
        public void onSuccess() {}

        @Override
        public void onFail(String error) {}
    };
}
