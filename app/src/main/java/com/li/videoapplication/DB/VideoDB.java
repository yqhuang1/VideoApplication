package com.li.videoapplication.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.fmscreenrecord.utils.FileUtils;
import com.fmscreenrecord.video.VideoInfo;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoDB extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "VIDEO.db"; // 库名
    private final static int DATABASE_VERSION = 3; // 版本
    private final static String TABLE_NAME = "video_table"; // 表名
    public final static String VIDEO_ID = "video_id"; // 视频id
    public final static String VIDEO_NAME = "video_name"; // 名字
    public final static String VIDEO_PATH = "video_path"; // 路径
    public final static String VIDEO_DURATION = "video_duration"; // 时长
    public final static String VIDEO_INSERT_TIME = "video_insert_time"; // 文件记录时间
    /**
     * 文件源，录制的视频（rec）/外部导入的视频(ext)
     */
    public final static String VIDEO_SOURCE = "video_source";
    /**
     * 视频状态，在本地（local）/上传中(uploading)/在云端（service）/上传成功(success)/暂停上传(
     * pauseupvideo)/云端隐藏(hideinserver)
     */
    public final static String VIDEO_STATION = "video_station";
    public final static String VIDEO_URL = "video_url"; // 储存在服务器的视频链接
    public final static String IMAGE_URL = "image_url"; // 储存在服务器的图片链接
    public final static String GAME_NAME = "ganme_name"; // 上传时游戏名称
    public final static String UPVIDEO_PRECENT = "upvideo_precent"; // 上传百分比
    public final static String UPVIDEO_TITLE = "upvideo_title"; // 上传时的标题
    public final static String UPVIDEO_TOKEN = "upvideo_token"; // 上传时保存的视频token（有效期24小时)
    public final static String UPVIDEO_TOKEN_GETTIME = "upvideo_token_gettime"; // 上传时的视频token获得时间
    public final static String UPVIDEO_QNKEY = "upvideo_qnkey"; // 请求后台获取videokey(不带A)
    public final static String VIDEO_DESCRIBE = "video_describe"; // 视频描述

    // TODO
    public VideoDB videoDB;

    public VideoDB(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 创建table
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME + " (" + VIDEO_ID
                + " INTEGER primary key autoincrement, " + VIDEO_NAME
                + " text, " + VIDEO_PATH + " text, " + VIDEO_DURATION
                + " text, " + VIDEO_SOURCE + " text, " + VIDEO_STATION
                + " text, " + VIDEO_URL + " text, " + IMAGE_URL + " text, "
                + VIDEO_INSERT_TIME + " text," + GAME_NAME + " text,"
                + UPVIDEO_TITLE + " text," + UPVIDEO_PRECENT + " double,"
                + UPVIDEO_TOKEN + " text," + UPVIDEO_TOKEN_GETTIME + " long,"
                + VIDEO_DESCRIBE + " text);";

        try {
            db.execSQL(sql);

        } catch (SQLiteException e) {
            e.printStackTrace();

        }

    }

    /**
     * 升级回调
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // ChangeDBData();

        // 根据用户当前数据库版本进行升级
        if (oldVersion == 1) {
            String sql = "alter table video_table add column ganme_name text";
            db.execSQL(sql);
            String sql1 = "alter table video_table add column upvideo_title text";
            db.execSQL(sql1);
            String sql2 = "alter table video_table add column upvideo_precent double";
            db.execSQL(sql2);
            String sql3 = "alter table video_table add column upvideo_token text";
            db.execSQL(sql3);
            String sql4 = "alter table video_table add column upvideo_token_gettime long";
            db.execSQL(sql4);
        } else if (oldVersion == 2) {
            String sql1 = "alter table video_table add column upvideo_token text";
            db.execSQL(sql1);
            String sql2 = "alter table video_table add column upvideo_token_gettime long";
            db.execSQL(sql2);
        }

        // db.close();
    }

    public Cursor select() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db
                .query(TABLE_NAME, null, null, null, null, null, null);
        db.close();
        return cursor;
    }

    /**
     * 数据插入操作
     *
     * @param videoName    视频名
     * @param videoPath    视频地址
     * @param videoSource  文件源，录制的视频（rec）/外部导入的视频(ext)
     * @param videoStation 视频状态，在本地（local）/上传中(uploading)/在云端（service）/上传成功(success)/暂停上传
     *                     ( pauseupvideo)/云端隐藏(hideinserver)
     * @return
     */
    // TODO
    public long insert(String videoName, String videoPath, String videoSource,
                       String videoStation) {
        SQLiteDatabase db = this.getWritableDatabase();

		/* ContentValues */
        ContentValues cv = new ContentValues();
        cv.put(VIDEO_NAME, videoName);
        cv.put(VIDEO_PATH, videoPath);
        cv.put(VIDEO_SOURCE, videoSource);
        cv.put(VIDEO_STATION, videoStation);
        long row = db.insert(TABLE_NAME, null, cv);
        db.close();
        return row;
    }

    // 删除操作
    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = VIDEO_ID + " = ?";
        String[] whereValue = {Integer.toString(id)};
        db.delete(TABLE_NAME, where, whereValue);
        db.close();
    }

    /**
     * 修改操作
     *
     * @param id           视频所在数据库ID
     * @param videoName    视频名称
     * @param videoPath    视频路径
     * @param videoSource  视频远
     * @param videoStation 视频是否上传服务器
     * @param videoUrl     优酷所在链接
     */
    public void update(String id, String videoName, String videoPath,
                       String videoSource, String videoStation, String videoUrl,
                       String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = VIDEO_ID + " = ?";
        String[] whereValue = {id};

        ContentValues cv = new ContentValues();
        cv.put(VIDEO_NAME, videoName);
        cv.put(VIDEO_PATH, videoPath);
        cv.put(VIDEO_SOURCE, videoSource);
        cv.put(VIDEO_STATION, videoStation);
        cv.put(VIDEO_URL, videoUrl);
        cv.put(IMAGE_URL, imageUrl);

        if (db != null) {
            db.update(TABLE_NAME, cv, where, whereValue);
            db.close();
        }
    }

    /**
     * ** 更改上传状态
     *
     * @param address      视频路径
     * @param videoStation 视频上传状态
     * @param gamename     游戏名称
     * @param videotile    视频标题
     * @param dbpercent    上传进度
     * @param videourl     视频云端链接地址
     */
    public void updateContect(String address, String videoStation,
                              String gamename, String videotile, double dbpercent, String videourl) {

        SQLiteDatabase db = this.getWritableDatabase();
        String where = VIDEO_PATH + " = ?";
        String[] whereValue = {address};

        ContentValues cv = new ContentValues();

        cv.put(VIDEO_STATION, videoStation);
        cv.put(GAME_NAME, gamename);
        cv.put(UPVIDEO_TITLE, videotile);
        cv.put(UPVIDEO_PRECENT, dbpercent);
        // cv.put(VIDEO_URL, videourl);

        if (db != null) {
            db.update(TABLE_NAME, cv, where, whereValue);
            db.close();
        }

    }

    /**
     * ** 更改视频编辑内容
     *
     * @param address       视频路径
     * @param gamename      游戏名称
     * @param videotile     视频标题
     * @param videodescribe 视频描述
     */
    public void updateEditContect(String address, String gamename, String videotile, String videodescribe) {

        SQLiteDatabase db = this.getWritableDatabase();
        String where = VIDEO_PATH + " = ?";
        String[] whereValue = {address};

        ContentValues cv = new ContentValues();

        cv.put(GAME_NAME, gamename);
        cv.put(UPVIDEO_TITLE, videotile);
        cv.put(VIDEO_DESCRIBE, videodescribe);

        if (db != null) {
            db.update(TABLE_NAME, cv, where, whereValue);
            db.close();
        }

    }

    /**
     * 通过云端视频ID修改本地视频上传状态
     */
    public void setupdateContect(String videourl, String videoStation) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = VIDEO_URL + " = ?";
        String[] whereValue = {videourl};

        ContentValues cv = new ContentValues();

        cv.put(VIDEO_STATION, videoStation);

        if (db != null) {
            db.update(TABLE_NAME, cv, where, whereValue);
            db.close();
        }

    }

    /**
     * 根据文件地址存储token
     *
     * @param address   视频地址
     * @param token     服务器token
     * @param tokenTime token获取时间
     * @param videoUrl  服务器视频播放链接
     */
    public void saveVideoToken(String address, String token, long tokenTime,
                               String videoUrl) {

        SQLiteDatabase db = this.getWritableDatabase();
        String where = VIDEO_PATH + " = ?";
        String[] whereValue = {address};

        ContentValues cv = new ContentValues();

        cv.put(UPVIDEO_TOKEN, token);
        cv.put(UPVIDEO_TOKEN_GETTIME, tokenTime);
        cv.put(VIDEO_URL, videoUrl);

        if (db != null) {
            db.update(TABLE_NAME, cv, where, whereValue);
            db.close();
        }

    }

    /**
     * 通过文件地址查询token
     *
     * @param address
     * @return
     */
    public VideoInfo getVideoToken(String address) {
        VideoInfo vi = new VideoInfo();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("select * from video_table where video_path=?",
                new String[]{address});
        try {
            if (c.moveToFirst()) {
                // 获取视频上传的token
                String token = c.getString(c.getColumnIndex(UPVIDEO_TOKEN));
                // 获取视频获得token时的日期
                long tokentime = c.getLong(c
                        .getColumnIndex(UPVIDEO_TOKEN_GETTIME));
                // 获取视频播放链接
                String videourl = c.getString(c.getColumnIndex(VIDEO_URL));
                vi.setToken(token);
                vi.setTokenTime(tokentime);
                vi.setVideoURL(videourl);

            }
        } catch (Exception e) {
            return null;
        } finally {
            c.close();
            db.close();
        }
        return vi;

    }

    /**
     * 通过文件地址查询上传状态*
     */
    public VideoInfo getupdateContect(String address) {
        VideoInfo vi = new VideoInfo();
        SQLiteDatabase db = this.getWritableDatabase();
        String videostation = null;
        Cursor c = db.rawQuery("select * from video_table where video_path=?",
                new String[]{address});
        try {
            if (c.moveToFirst()) {

                videostation = c.getString(c.getColumnIndex(VIDEO_STATION));
                String name = c.getString(c.getColumnIndex(VIDEO_NAME));
                String videoid = c.getString(c.getColumnIndex(VIDEO_ID));
                String gamename = c.getString(c.getColumnIndex(GAME_NAME));
                String videotitle = c
                        .getString(c.getColumnIndex(UPVIDEO_TITLE));
                String videodescribe = c.getString(c.getColumnIndex(VIDEO_DESCRIBE));
                String videourl = c.getString(c.getColumnIndex(VIDEO_URL));
                double precent = c.getDouble(c.getColumnIndex(UPVIDEO_PRECENT));

                vi.setDisplayName(name);
                vi.setVideoStation(videostation);
                vi.setGamename(gamename);
                vi.setVideoId(videoid);
                vi.setUpvideotitle(videotitle);
                vi.setVideodescribe(videodescribe);
                vi.setPrecent(precent);

                vi.setVideoURL(videourl);

            }
        } catch (Exception e) {
            return null;
        } finally {
            c.close();
            db.close();
        }
        return vi;

    }

    /**
     * 通过videoName查询文件
     *
     * @param videoName
     * @return
     */
    public VideoInfo GetVideoFile(String videoName) {
        VideoInfo vi = new VideoInfo();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from video_table where video_name=?",
                new String[]{videoName});
        try {
            if (c.moveToFirst()) {
                String name = c.getString(c.getColumnIndex(VIDEO_NAME));
                String videostation = c.getColumnName(DATABASE_VERSION);
                String videoid = c.getString(c.getColumnIndex(VIDEO_ID));
                String gamename = c.getString(c.getColumnIndex(GAME_NAME));

                vi.setDisplayName(name);
                vi.setVideoStation(videostation);
                vi.setGamename(gamename);
                vi.setVideoId(videoid);

            }
        } catch (Exception e) {
            return null;
        } finally {
            c.close();
            db.close();
        }
        return vi;
    }

    /**
     * 通过文件名查询文件ID
     */

    public String GetVideoIdForPath(String videoPath) {

        SQLiteDatabase db = this.getWritableDatabase();
        String videoid = null;
        Cursor c = db.rawQuery("select * from video_table where video_path=?",
                new String[]{videoPath});
        try {
            if (c.moveToFirst()) {

                videoid = c.getString(c.getColumnIndex(VIDEO_ID));

            }
        } catch (Exception e) {
            return null;
        } finally {
            c.close();
            db.close();
        }
        return videoid;
    }

    /**
     * 插入剪辑的视频
     */
    // TODO
    public int insertForCutVideo(String videoName, String videoPath,
                                 String videoSource, String videoStation) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery(
                "select video_name from video_table where video_name=?",
                new String[]{videoName});
        try {
            if (c.moveToFirst()) {// 如果存在重名，返回告知用户

                return 0;
            } else {// 插入数据

                ContentValues cv = new ContentValues();
                cv.put(VIDEO_NAME, videoName);
                cv.put(VIDEO_PATH, videoPath);
                cv.put(VIDEO_SOURCE, videoSource);
                cv.put(VIDEO_STATION, videoStation);
                db.insert(TABLE_NAME, null, cv);

                return 1;
            }
        } catch (Exception e) {
            return -1;
        } finally {
            c.close();
            db.close();
        }

    }

    /**
     * 修改视频文件名和文件路径方法
     *
     * @param videoName
     * @return
     */
    public int renameForVideoName(String videoName, String videoid,
                                  String newfilePaht) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery(
                "select video_name from video_table where video_name=?",
                new String[]{videoName});
        try {
            if (c.moveToFirst()) {// 如果存在重名，返回告知用户

                return 0;
            } else {// 修改数据库中的视频名称

                db.execSQL(
                        "update video_table set video_name=?,video_path =? where video_id =?",
                        new Object[]{videoName, newfilePaht, videoid});

                return 1;
            }
        } catch (Exception e) {
            return -1;
        } finally {
            c.close();
            db.close();
        }

    }

    /**
     * 根据文件路径修改文件名称
     *
     * @param videoNewName
     * @param newfilePaht
     * @param filePaht
     * @return
     */
    public int setFileNameForPath(String videoNewName, String newfilePaht,
                                  String filePaht) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery(
                "select video_name from video_table where video_name=?",
                new String[]{videoNewName});
        try {
            if (c.moveToFirst()) {// 如果存在重名，返回告知用户

                return 0;
            } else {// 修改数据库中的视频名称

                db.execSQL(
                        "update video_table set video_name=?,video_path =? where video_path =?",
                        new Object[]{videoNewName, newfilePaht, filePaht});
                copyDBToSDcrad();
                return 1;
            }
        } catch (Exception e) {
            return -1;
        } finally {
            c.close();
            db.close();
        }

    }

    /**
     * 根据视频ID修改文件路径方法
     */

    public void setFilePathForId(String videoid, String newfilePaht) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("update video_table set video_path =? where video_id =?",
                new Object[]{newfilePaht, videoid});
        db.close();

    }

    // 将数据库拷贝到SD卡
    public static void copyDBToSDcrad() {

        String DATABASE_NAME = "VIDEO.db";

        String oldPath = "data/data/com.li.videoapplication/databases/"
                + DATABASE_NAME;
        String newPath = Environment.getExternalStorageDirectory()
                + File.separator + DATABASE_NAME;

        copyFile(oldPath, newPath);
    }

    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newfile = new File(newPath);
            if (!newfile.exists()) {
                newfile.createNewFile();
            }
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            // "复制单个文件操作出错");
            e.printStackTrace();

        }
    }

    // 根据文件路径删除数据库记录
    public void DelectForFilePath(String filepath) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("video_table", "video_path=?", new String[]{filepath});

        db.close();
    }

    // 通过videoid查询文件
    public VideoInfo GetVideoFileForId(String videoId) {
        VideoInfo vi = new VideoInfo();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from video_table where video_id=?",
                new String[]{videoId});
        try {
            if (c.moveToFirst()) {
                String id = c.getString(c.getColumnIndex(VIDEO_ID));
                String url = c.getString(c.getColumnIndex(VIDEO_URL));
                String imageurl = c.getString(c.getColumnIndex(IMAGE_URL));
                String videotitle = c.getString(c.getColumnIndex(VIDEO_NAME));
                vi.setVideoId(id);

                vi.setVideoURL(url);
                vi.setImageUrl(imageurl);

            }
        } catch (Exception e) {
            return null;
        } finally {
            c.close();
            db.close();
        }
        return vi;
    }

    // 获取视频列表
    public List<VideoInfo> GetVideoList() {
        List<VideoInfo> list = new ArrayList<VideoInfo>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("video_table", null, null, null, null, null,
                null);
        VideoInfo vi;
        try {
            while (cursor.moveToNext()) {
                vi = new VideoInfo();
                vi.setVideoId(cursor.getString(cursor.getColumnIndex(VIDEO_ID)));
                vi.setDisplayName(cursor.getString(cursor
                        .getColumnIndex(VIDEO_NAME)));
                vi.setPath(cursor.getString(cursor.getColumnIndex(VIDEO_PATH)));
                vi.setVideoSource(cursor.getString(cursor
                        .getColumnIndex(VIDEO_SOURCE)));
                vi.setVideoStation(cursor.getString(cursor
                        .getColumnIndex(VIDEO_STATION)));

                vi.setVideoURL(cursor.getString(cursor
                        .getColumnIndex(VIDEO_URL)));
                vi.setImageUrl(cursor.getString(cursor
                        .getColumnIndex(IMAGE_URL)));
                vi.setVideoUploadProgress(0);
                vi.setVideoSize(String.valueOf(FileUtils.getFileSize(new File(
                        vi.getPath()))));
                list.add(vi);
            }
        } catch (Exception e) {
            return null;
        } finally {
            cursor.close();
            db.close();
        }
        // 视频列表倒序
        Collections.reverse(list);

        return list;
    }

    public void clearTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

    // 初次启动时，初始化数据库数据
    public void InitDBData(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                clearTable();
                // 获得视频文件
                file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        String name = file.getName();
                        float fileSize = FileUtils.getFileSize(new File(file
                                .getAbsolutePath()));
                        if (fileSize > 512) {
                            int i = name.indexOf('.');
                            if (i != -1) {
                                name = name.substring(i);
                                if (name.equalsIgnoreCase(".mp4")) {
                                    insert(file.getName(),
                                            file.getAbsolutePath(), "rec",
                                            "local");
                                    return true;

                                }
                            } else if (file.isDirectory()) {
                                InitDBData(file);
                            }
                        }
                        return false;
                    }
                });
            }
        }).start();
    }

    public void InitDBData() {
        List<VideoInfo> list = new ArrayList<VideoInfo>();
        list = GetVideoList();
        int i = 0;
        String[] dir;
        String dirF;
        String dirB;
        for (i = 0; i < list.size(); i++) {
            dir = list.get(i).getPath().split("SupperLulu"); // 分割
            dirF = dir[0];
            dirB = dir[1];
            list.get(i).setPath(dirF + "LuPingDaShi" + dirB);
            ChangeDir(String.valueOf(i), list.get(i).getPath()); // 更新数据库
        }
    }

    public void ChangeDBData() {
        List<VideoInfo> list = new ArrayList<VideoInfo>();
        list = GetVideoList();
        int i = 0;
        String[] dir;
        String dirF;
        String dirB;
        for (i = 0; i < list.size(); i++) {
            dir = list.get(i).getPath().split("SupperLulu"); // 分割
            dirF = dir[0];
            dirB = dir[1];
            list.get(i).setPath(dirF + "LuPingDaShi" + dirB);
            ChangeDir(list.get(i).getVideoId(), list.get(i).getPath()); // 更新数据库
        }
    }

    public void ChangeDir(String id, String videoPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = VIDEO_ID + " = ?";
        String[] whereValue = {id};

        ContentValues cv = new ContentValues();
        cv.put(VIDEO_PATH, videoPath);
        if (db != null) {
            db.update(TABLE_NAME, cv, where, whereValue);
            db.close();
        }
    }

}
