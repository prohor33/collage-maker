package prohor33.redmadrobot.test.collage_maker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    private Listener listener;
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private final int imageInCollageCount = 9;
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
        return collageBitmap.getHeight() / collageBitmap.getWidth();
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
        int img_size = collage_size_x / count_x;

        Bitmap collageImage = Bitmap.createBitmap(img_size * count_x,
                img_size * count_y, Bitmap.Config.ARGB_8888);
        Canvas collageCanvas = new Canvas(collageImage);

        for (int y = 0; y < count_y; y++) {
            for (int x = 0; x < count_x; x++) {
                int i = count_x * y + x;
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmaps.get(i),
                        img_size, img_size, true);
                collageCanvas.drawBitmap(scaledBitmap, x * img_size, y * img_size,
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
