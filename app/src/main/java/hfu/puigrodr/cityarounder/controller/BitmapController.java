package hfu.puigrodr.cityarounder.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 * some static functions for handling images
 */
public class BitmapController {

    public static Bitmap getBitmapByPath(String path, ImageView imageView){

        BitmapFactory.Options options = new BitmapFactory.Options();

        //First, get image sizes
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // calculate inSampleSize -> how much to downscale
        int maxWidth = imageView.getWidth();
        if(options.outWidth > maxWidth){
            int maxHeight = (int) ( maxWidth * ((float) options.outHeight / options.outWidth) );
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        }

        //Second, decode Image
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        bitmap = Bitmap.createBitmap(bitmap, 0, (height / 4), width, (height - (height / 2)) );

        return bitmap;
    }

    public static String getMimeType(String path){

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        return options.outMimeType;
    }

    /**
     * downscale Image on Factor x (thats what is returned)
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
