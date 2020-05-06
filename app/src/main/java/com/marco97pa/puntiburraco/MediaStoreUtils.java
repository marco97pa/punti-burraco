package com.marco97pa.puntiburraco;

        import android.content.Context;
        import android.content.Intent;
        import android.database.Cursor;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.ImageDecoder;
        import android.net.Uri;
        import android.os.Build;
        import android.provider.MediaStore;

        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.net.URI;

/**
 * MEDIASTOREUTILS
 * Utility class to get images from user
 *
 */

public final class MediaStoreUtils {
    private MediaStoreUtils() {
    }

    public static Intent getPickImageIntent(final Context context) {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        return Intent.createChooser(intent, "Select picture");
    }

    public static Bitmap getBitmap(Context context, Uri imageUri) {
        Bitmap bitmap = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(context.getContentResolver(),imageUri));
            }
            else {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static void convertBitmaptoFile(File destinationFile, Bitmap bitmap) {
        try {
            //create a file to write bitmap data
            destinationFile.createNewFile();
            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            byte[] bitmapData = bos.toByteArray();
            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(destinationFile);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}