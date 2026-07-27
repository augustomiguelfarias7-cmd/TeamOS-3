package com.teamos.launcher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class LauncherDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "teamos_launcher.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_WORKSPACE = "workspace_items";
    public static final String COL_ID = "_id";
    public static final String COL_PACKAGE_NAME = "package_name";
    public static final String COL_CLASS_NAME = "class_name";
    public static final String COL_ITEM_TYPE = "item_type";
    public static final String COL_SCREEN = "screen";
    public static final String COL_CELL_X = "cell_x";
    public static final String COL_CELL_Y = "cell_y";
    public static final String COL_SPAN_X = "span_x";
    public static final String COL_SPAN_Y = "span_y";

    public static class DbItem {
        public long id;
        public String packageName;
        public String className;
        public int itemType; // 0 = Shortcut, 1 = Widget
        public int screen;
        public int cellX;
        public int cellY;
        public int spanX;
        public int spanY;

        public DbItem(long id, String packageName, String className, int itemType, int screen, int cellX, int cellY, int spanX, int spanY) {
            this.id = id;
            this.packageName = packageName;
            this.className = className;
            this.itemType = itemType;
            this.screen = screen;
            this.cellX = cellX;
            this.cellY = cellY;
            this.spanX = spanX;
            this.spanY = spanY;
        }
    }

    public LauncherDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_WORKSPACE + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PACKAGE_NAME + " TEXT, " +
                COL_CLASS_NAME + " TEXT, " +
                COL_ITEM_TYPE + " INTEGER, " +
                COL_SCREEN + " INTEGER, " +
                COL_CELL_X + " INTEGER, " +
                COL_CELL_Y + " INTEGER, " +
                COL_SPAN_X + " INTEGER, " +
                COL_SPAN_Y + " INTEGER" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKSPACE);
        onCreate(db);
    }

    public long addItem(DbItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PACKAGE_NAME, item.packageName);
        cv.put(COL_CLASS_NAME, item.className);
        cv.put(COL_ITEM_TYPE, item.itemType);
        cv.put(COL_SCREEN, item.screen);
        cv.put(COL_CELL_X, item.cellX);
        cv.put(COL_CELL_Y, item.cellY);
        cv.put(COL_SPAN_X, item.spanX);
        cv.put(COL_SPAN_Y, item.spanY);
        long id = db.insert(TABLE_WORKSPACE, null, cv);
        item.id = id;
        return id;
    }

    public void updateItemPosition(long id, int screen, int cellX, int cellY) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_SCREEN, screen);
        cv.put(COL_CELL_X, cellX);
        cv.put(COL_CELL_Y, cellY);
        db.update(TABLE_WORKSPACE, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void removeItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORKSPACE, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public List<DbItem> getAllItems() {
        List<DbItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKSPACE, null, null, null, null, null, null);
        if (cursor != null) {
            int idIdx = cursor.getColumnIndex(COL_ID);
            int pkgIdx = cursor.getColumnIndex(COL_PACKAGE_NAME);
            int clsIdx = cursor.getColumnIndex(COL_CLASS_NAME);
            int typeIdx = cursor.getColumnIndex(COL_ITEM_TYPE);
            int screenIdx = cursor.getColumnIndex(COL_SCREEN);
            int cxIdx = cursor.getColumnIndex(COL_CELL_X);
            int cyIdx = cursor.getColumnIndex(COL_CELL_Y);
            int sxIdx = cursor.getColumnIndex(COL_SPAN_X);
            int syIdx = cursor.getColumnIndex(COL_SPAN_Y);

            while (cursor.moveToNext()) {
                list.add(new DbItem(
                        cursor.getLong(idIdx),
                        cursor.getString(pkgIdx),
                        cursor.getString(clsIdx),
                        cursor.getInt(typeIdx),
                        cursor.getInt(screenIdx),
                        cursor.getInt(cxIdx),
                        cursor.getInt(cyIdx),
                        cursor.getInt(sxIdx),
                        cursor.getInt(syIdx)
                ));
            }
            cursor.close();
        }
        return list;
    }
}
