package com.rahnema.gramophone.ext;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

/**
 * @author Payam1991 on 9/18/2017.
 */

public class Utils {

    public static void loadAlbumArt(Context context, ImageView imageView,long albumId)
    {
        Cursor artCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.AlbumColumns.ALBUM_ART},
                MediaStore.Audio.Media._ID+" =?",
                new String[]{String.valueOf(albumId)},
                null);
        String albumArt;
        if(artCursor.moveToNext()) {
            albumArt = "file://"+artCursor.getString(0);
        } else {
            albumArt = null;
        }
        artCursor.close();

        if(albumArt != null) {
            Glide.with(context)
                    .load(albumArt)
                    .centerCrop()
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
    }
    public static String getAlbumArtBitmap(Context context , long albumId)
    {
        Cursor artCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.AlbumColumns.ALBUM_ART},
                MediaStore.Audio.Media._ID+" =?",
                new String[]{String.valueOf(albumId)},
                null);
        String albumArt;
        if(artCursor.moveToNext()) {
            albumArt = "file://"+artCursor.getString(0);
        } else {
            albumArt = null;
        }
        artCursor.close();

        if(albumArt != null) {
            return albumArt;
        }
        return null;
    }
}
