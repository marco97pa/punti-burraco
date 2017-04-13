package com.marco97pa.puntiburraco;

        import android.content.Context;
        import android.content.Intent;
        import android.database.Cursor;
        import android.net.Uri;
        import android.provider.MediaStore;

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
}