package com.duan.musicoco.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.duan.musicoco.aidl.Song;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by DuanJiaNing on 2017/7/1.
 */

public class DBMusicocoController {

    private final Context context;
    private final SQLiteDatabase database;

    private static final String TAG = "DBMusicocoController";

    public static final String DATABASE = "musicoco.db";

    public static final String TABLE_SONG = "song";
    public static final String SONG_ID = "_id"; //主键
    public static final String SONG_PATH = "path"; //路径
    public static final String SONG_LASTPLAYTIME = "last_play"; //最后播放时间
    public static final String SONG_PLAYTIMES = "play_times"; //播放次数
    public static final String SONG_REMARK = "remarks"; //备注
    public static final String SONG_SHEETS = "sheets"; //所属歌单 歌单编号，空格隔开
    public static final String SONG_CREATE = "create_time"; //创建时间

    public static final String TABLE_SHEET = "sheet";
    public static final String SHEET_ID = "_id"; // 主键
    public static final String SHEET_NAME = "name"; //歌单名称
    public static final String SHEET_REMARK = "remarks"; //歌单备注
    public static final String SHEET_CREATE = "create_time"; //创建时间

    static void createSongTable(SQLiteDatabase db) {
        String sql = "create table " + DBMusicocoController.TABLE_SONG + "(" +
                DBMusicocoController.SONG_ID + " integer primary key autoincrement," +
                DBMusicocoController.SONG_PATH + " text unique," +
                DBMusicocoController.SONG_LASTPLAYTIME + " char(20)," +
                DBMusicocoController.SONG_PLAYTIMES + " integer," +
                DBMusicocoController.SONG_REMARK + " text," +
                DBMusicocoController.SONG_SHEETS + " text," +
                DBMusicocoController.SONG_CREATE + " text)";
        db.execSQL(sql);
    }

    static void createSheetTable(SQLiteDatabase db) {

        String sql = "create table " + DBMusicocoController.TABLE_SHEET + "(" +
                DBMusicocoController.SHEET_ID + " integer primary key autoincrement," +
                DBMusicocoController.SHEET_NAME + " text unique," +
                DBMusicocoController.SHEET_REMARK + " text," +
                DBMusicocoController.SHEET_CREATE + " text)";
        db.execSQL(sql);
    }

    public static class SongInfo {
        public int id;
        public String path;
        public long lastPlayTime;
        public int playTimes;
        public String remark;
        public long create;
        public int[] sheets;

        public SongInfo() {
        }

        public SongInfo(String path, long lastPlayTime, int playTimes, String remark, long create, int[] sheets) {
            this.path = path;
            this.lastPlayTime = lastPlayTime;
            this.playTimes = playTimes;
            this.remark = remark;
            this.create = create;
            this.sheets = sheets;
        }

        @Override
        public String toString() {
            return "SongInfo{" +
                    "id=" + id +
                    ", path='" + path + '\'' +
                    ", lastPlayTime=" + lastPlayTime +
                    ", playTimes=" + playTimes +
                    ", remark='" + remark + '\'' +
                    ", create=" + create +
                    ", sheets=" + Arrays.toString(sheets) +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SongInfo songInfo = (SongInfo) o;

            return path.equals(songInfo.path);

        }

        @Override
        public int hashCode() {
            return path.hashCode();
        }
    }

    public static class Sheet {
        public int id;
        public String name;
        public String remark;
        public long create;

        public Sheet() {
        }

        public Sheet(String name, String remark, long create) {
            this.name = name;
            this.remark = remark;
            this.create = create;
        }

        @Override
        public String toString() {
            return "Sheet{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", remark='" + remark + '\'' +
                    ", create=" + create +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Sheet sheet = (Sheet) o;

            return name.equals(sheet.name);

        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    /**
     * 在使用结束时应调用{@link #close()}关闭数据库连接
     */
    public DBMusicocoController(Context context, boolean writable) {
        DBHelper helper = DBHelper.getInstance(context, DATABASE);
        if (writable) {
            this.database = helper.getWritableDatabase();
        } else {
            this.database = helper.getReadableDatabase();
        }
        this.context = context;
    }

    public void close() {
        if (database.isOpen())
            database.close();
    }

    @Nullable
    public Sheet getSheet(int sheetId) {
        String sql = "select * from " + TABLE_SHEET + " where " + SHEET_ID + " = " + sheetId;
        Cursor cursor = database.rawQuery(sql, null);

        Sheet sheet = new Sheet();
        while (cursor.moveToNext()) {
            sheet.id = cursor.getInt(cursor.getColumnIndex(SHEET_ID));
            sheet.name = cursor.getString(cursor.getColumnIndex(SHEET_NAME));
            sheet.remark = cursor.getString(cursor.getColumnIndex(SHEET_REMARK));
            String str = cursor.getString(cursor.getColumnIndex(SHEET_CREATE));
            sheet.create = Long.valueOf(str);
        }

        cursor.close();
        return sheet;
    }

    public List<Sheet> getSheets() {
        String sql = "select * from " + TABLE_SHEET;
        Cursor cursor = database.rawQuery(sql, null);

        List<Sheet> sheets = new ArrayList<>();
        while (cursor.moveToNext()) {
            Sheet sheet = new Sheet();
            sheet.id = cursor.getInt(cursor.getColumnIndex(SHEET_ID));
            sheet.name = cursor.getString(cursor.getColumnIndex(SHEET_NAME));
            sheet.remark = cursor.getString(cursor.getColumnIndex(SHEET_REMARK));
            String str = cursor.getString(cursor.getColumnIndex(SHEET_CREATE));
            sheet.create = Long.valueOf(str);
            sheets.add(sheet);
        }

        cursor.close();
        return sheets;
    }

    @Nullable
    public SongInfo getSongInfo(int songId) {
        String sql = "select * from " + TABLE_SONG + " where " + SONG_ID + " = " + songId;
        Cursor cursor = database.rawQuery(sql, null);

        SongInfo info = new SongInfo();
        while (cursor.moveToNext()) {
            info.id = cursor.getInt(cursor.getColumnIndex(SONG_ID));
            info.path = cursor.getString(cursor.getColumnIndex(SONG_PATH));

            String str = cursor.getString(cursor.getColumnIndex(SONG_LASTPLAYTIME));
            info.lastPlayTime = Long.valueOf(str);

            info.playTimes = cursor.getInt(cursor.getColumnIndex(SONG_PLAYTIMES));
            info.remark = cursor.getString(cursor.getColumnIndex(SONG_REMARK));

            String str1 = cursor.getString(cursor.getColumnIndex(SONG_CREATE));
            info.create = Long.valueOf(str1);

            String sh = cursor.getString(cursor.getColumnIndex(SONG_SHEETS));
            String[] strs = sh.split(" ");
            int[] shs = new int[strs.length];
            for (int i = 0; i < shs.length; i++) {
                shs[i] = Integer.parseInt(strs[i]);
            }
            info.sheets = shs;

        }

        cursor.close();
        return info;
    }

    @Nullable
    public SongInfo getSongInfo(@NonNull Song song) {
        String sql = "select * from " + TABLE_SONG + " where " + SONG_PATH + " like '" + song.path + "'";
        Cursor cursor = database.rawQuery(sql, null);

        SongInfo info = new SongInfo();
        while (cursor.moveToNext()) {
            info.id = cursor.getInt(cursor.getColumnIndex(SONG_ID));
            info.path = cursor.getString(cursor.getColumnIndex(SONG_PATH));

            String str = cursor.getString(cursor.getColumnIndex(SONG_LASTPLAYTIME));
            info.lastPlayTime = Long.valueOf(str);

            info.playTimes = cursor.getInt(cursor.getColumnIndex(SONG_PLAYTIMES));
            info.remark = cursor.getString(cursor.getColumnIndex(SONG_REMARK));

            String str1 = cursor.getString(cursor.getColumnIndex(SONG_CREATE));
            info.create = Long.valueOf(str1);

            String sh = cursor.getString(cursor.getColumnIndex(SONG_SHEETS));
            String[] strs = sh.split(" ");
            int[] shs = new int[strs.length];
            for (int i = 0; i < shs.length; i++) {
                shs[i] = Integer.parseInt(strs[i]);
            }
            info.sheets = shs;
        }

        cursor.close();
        return info;
    }

    public List<SongInfo> getSongInfos() {
        String sql = "select * from " + TABLE_SONG;
        Cursor cursor = database.rawQuery(sql, null);

        List<SongInfo> infos = new ArrayList<>();
        while (cursor.moveToNext()) {
            SongInfo info = new SongInfo();
            info.id = cursor.getInt(cursor.getColumnIndex(SONG_ID));
            info.path = cursor.getString(cursor.getColumnIndex(SONG_PATH));

            String str = cursor.getString(cursor.getColumnIndex(SONG_LASTPLAYTIME));
            info.lastPlayTime = Long.valueOf(str);

            info.playTimes = cursor.getInt(cursor.getColumnIndex(SONG_PLAYTIMES));
            info.remark = cursor.getString(cursor.getColumnIndex(SONG_REMARK));

            String str1 = cursor.getString(cursor.getColumnIndex(SONG_CREATE));
            info.create = Long.valueOf(str1);

            String sh = cursor.getString(cursor.getColumnIndex(SONG_SHEETS));
            String[] strs = sh.split(" ");
            int[] shs = new int[strs.length];
            for (int i = 0; i < shs.length; i++) {
                shs[i] = Integer.parseInt(strs[i]);
            }
            info.sheets = shs;

            infos.add(info);
        }

        cursor.close();
        return infos;
    }

    public void addSheet(String name, String remark) {
        String create = System.currentTimeMillis() + "";
        if (remark == null)
            remark = "";

        String sql = String.format(Locale.CHINESE, "insert into %s values(null,'%s','%s','%s')",
                TABLE_SHEET, name, remark, create);
        database.execSQL(sql);
        Log.d(TAG, "addSheet: insert " + name);


    }

    public void addSongInfo(@NonNull Song song, int playTimes, @Nullable String remark, @Nullable int[] sheets) {

        if (remark == null) {
            remark = " ";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("0 "); //任何一首歌都在 全部歌曲 歌单中
        if (sheets != null && sheets.length > 0) {
            for (int i : sheets) {
                builder.append(i).append(" ");
            }
        }

        String path = song.path;
        String lpt = String.valueOf(System.currentTimeMillis()) + "";
        ContentValues values = new ContentValues();
        values.put(SONG_CREATE, lpt);
        values.put(SONG_LASTPLAYTIME, lpt);
        values.put(SONG_PATH, path);
        values.put(SONG_PLAYTIMES, playTimes);
        values.put(SONG_REMARK, remark);
        values.put(SONG_SHEETS, builder.toString());

        database.insert(TABLE_SONG, null, values);
        Log.d(TAG, "addSongInfo: insert " + path);

    }

    public void addSongInfo(List<Song> songs) {
        if (songs != null && songs.size() > 0) {
            for (Song song : songs) {
                addSongInfo(song, 0, null, null);
            }
        }
    }

    /**
     * 更新歌曲最后播放时间
     */
    public void updateSongLastPlayTime(@NonNull Song song, long time) {
        ContentValues values = new ContentValues();
        values.put(SONG_LASTPLAYTIME, time + "");
        String whereClause = SONG_PATH + " like ?";
        String[] whereArgs = {song.path};
        database.update(TABLE_SONG, values, whereClause, whereArgs);
    }

    public void updateSongLastPlayTime(int songID, long time) {
        ContentValues values = new ContentValues();
        values.put(SONG_LASTPLAYTIME, time + "");
        String whereClause = SONG_ID + " = ?";
        String[] whereArgs = {songID + ""};
        database.update(TABLE_SONG, values, whereClause, whereArgs);
    }

    public void updateSongLastPlayTime(int songID) {
        updateSongLastPlayTime(songID, System.currentTimeMillis());
    }

    public void updateSongLastPlayTime(@NonNull Song song) {
        updateSongLastPlayTime(song, System.currentTimeMillis());
    }

    /**
     * 更新歌曲播放次数
     */
    public void updateSongPlayTimes(@NonNull Song song, int times) {
        ContentValues values = new ContentValues();
        values.put(SONG_PLAYTIMES, times);
        String whereClause = SONG_PATH + " like ?";
        String[] whereArgs = {song.path + ""};
        database.update(TABLE_SONG, values, whereClause, whereArgs);
    }

    public void updateSongPlayTimes(int songID, int times) {
        ContentValues values = new ContentValues();
        values.put(SONG_PLAYTIMES, times);
        String whereClause = SONG_ID + " = ?";
        String[] whereArgs = {songID + ""};
        database.update(TABLE_SONG, values, whereClause, whereArgs);
    }

    public void updateSongPlayTimes(int songID) {
        SongInfo info = getSongInfo(songID);
        if (info == null)
            return;

        int times = info.playTimes + 1;
        updateSongPlayTimes(songID, times);
    }

    public void updateSongPlayTimes(@NonNull Song song) {
        SongInfo info = getSongInfo(song);
        if (info == null)
            return;

        int times = info.playTimes + 1;
        updateSongPlayTimes(song, times);
    }

    /**
     * 更新歌曲备注
     */
    public void updateSongRemark(@NonNull Song song, @NonNull String remark) {
        ContentValues values = new ContentValues();
        values.put(SONG_REMARK, remark);
        String whereClause = SONG_PATH + " like ?";
        String[] whereArgs = {song.path};
        database.update(TABLE_SONG, values, whereClause, whereArgs);
    }

    /**
     * 歌曲的播放次数加一
     * 同时修改最后播放时间为当前时间
     */
    public void addTimes(Song song) {
        updateSongPlayTimes(song);
    }

    public void truncate(String table) {
        String sql = "drop table " + table;
        database.execSQL(sql);

        if (table.equals(TABLE_SHEET))
            createSheetTable(database);
        else if (table.equals(TABLE_SONG))
            createSongTable(database);

    }

}