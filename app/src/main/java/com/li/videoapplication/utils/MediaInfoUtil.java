package com.li.videoapplication.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.li.videoapplication.entity.VideoManagerEntity;

import java.io.File;

/**
 * Created by feimoyuangong on 2015/7/22.
 */
public class MediaInfoUtil {


        /**
         * context
         */
        private Context mContext = null;

        /**
         * data path
         */
        private static final String dataPath = "/mnt";

        /**
         * query column
         */
        private static final String[] mCursorCols = new String[] {
                MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.MIME_TYPE,MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATA };

        /**
         * MediaInfoProvider
         * @param context
         */
        public MediaInfoUtil(Context context) {
            this.mContext = context;
        }

        /**
         * get the media file info by path
         * @param filePath
         * @return
         */
        public VideoManagerEntity getMediaInfo(String filePath) {

                /* check a exit file */
            File file = new File(filePath);
            if (file.exists()) {
                Toast.makeText(mContext, "sorry, the file is not exit!",
                        Toast.LENGTH_SHORT);
            }

                /* create the query URI, where, selectionArgs */
            Uri Media_URI = null;
            String where = null;
            String selectionArgs[] = null;

            if (filePath.startsWith("content://media/")) {
                        /* content type path */
                Media_URI = Uri.parse(filePath);
                where = null;
                selectionArgs = null;
            } else {
                        /* external file path */
                if(filePath.indexOf(dataPath) < 0) {
                    filePath = dataPath + filePath;
                }
                Media_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                where = MediaStore.MediaColumns.DATA + "=?";
                selectionArgs = new String[] { filePath };
            }

                /* query */
            Cursor cursor = mContext.getContentResolver().query(Media_URI,
                    mCursorCols, where, selectionArgs, null);
            if (cursor == null || cursor.getCount() == 0) {
                return null;
            } else {
                cursor.moveToFirst();
                VideoManagerEntity info = getInfoFromCursor(cursor);
                return info;
            }
        }

        /**
         * get the media info beans from cursor
         * @param cursor
         * @return
         */
        private VideoManagerEntity getInfoFromCursor(Cursor cursor) {
            VideoManagerEntity info = new VideoManagerEntity();

                /* file name */
            if(cursor.getString(1) != null) {
                info.setTitle(cursor.getString(1));
            }
                /* media duration */
            if(cursor.getString(2) != null) {
                info.setTimeLength(cursor.getString(2));
            }

                /* size */
            if(cursor.getString(4) != null) {
                float temp = cursor.getInt(8) / 1024f / 1024f;
                String sizeStr = (temp + "").substring(0, 4);
                info.setFileSize(sizeStr + "M");
            } else {
                info.setFileSize("undefine");
            }

                /* media file path */
            if (cursor.getString(5) != null) {
                info.setFilePath(cursor.getString(9));
            }

            return info;
        }

}
