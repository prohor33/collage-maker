package prohor33.redmadrobot.test.collage_maker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import prohor33.redmadrobot.test.utility.SimpleAsyncListener;
import prohor33.redmadrobot.test.utility.ImageLoadAsyncTask;

/**
 * Created by prohor on 26/02/15.
 */
public class ImageLoader {

    public static ImageLoadAsyncTask loadImage(final String url_str) {

        ImageLoadAsyncTask task = new ImageLoadAsyncTask(new SimpleAsyncListener() {
            Bitmap bitmap;

            @Override
            public void onSuccess() {
                CollageMaker.addLoadedBitmap(bitmap);
            }

            @Override
            public void onError(String error) {
                CollageMaker.onFailedToLoadImage();
            }

            @Override
            public Boolean doInBackground() {

                URL url;
                try {
                    url = new URL(url_str);
                } catch (MalformedURLException e) {
                    return false;
                }

                try {
                    bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    // TODO: catch errors and respond
                    return false;
                } catch (OutOfMemoryError e) {
                    return false;
                }
                return bitmap != null;
            }
        });

        task.execute();
        return task;
    }
}
