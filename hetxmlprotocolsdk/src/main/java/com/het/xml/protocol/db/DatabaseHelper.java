/*
 * -----------------------------------------------------------------
 * Copyright ?2014 clife - 和而泰家居在线网络科技有限公司
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: DatabaseHelper.java
 * Create: 2015/9/16 10:58
 */
package com.het.xml.protocol.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by IntelliJ IDEA.
 * User: UUXIA
 * Date: 2015/9/16
 * Time: 10:58
 * Description:
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    /**
     * DatabaseHelper.
     */
    private static DatabaseHelper sDbHelper;

    /**
     * SQLiteDatabase.
     */
    private static SQLiteDatabase sDb;

    /**
     * 上下文信息.
     */
    private static Context sContext;

    /**
     * 对象锁.
     */
    private static Object sObjLock = new Object();

    /**
     * @param context 上下文信息
     */
    private DatabaseHelper(final Context context) {
        super(context, "protocol" + DataBaseInfo.EXTENSION, null, DataBaseInfo.VERSION);
        DatabaseHelper.sContext = context;
    }

    /**
     * 获取DatabaseHelper实例.
     *
     * @param context 上下文信息.
     * @return DatabaseHelper实例
     */
    private static DatabaseHelper getInstance(final Context context) {
        if (DatabaseHelper.sDbHelper == null) {
            synchronized (DatabaseHelper.sObjLock) {
                if (DatabaseHelper.sDbHelper == null) {
                    DatabaseHelper.sDbHelper = new DatabaseHelper(context);
                }
            }
        }
        return DatabaseHelper.sDbHelper;
    }

    /**
     * 获取SQLiteDatabase实例.
     *
     * @param context 上下文信息.
     * @return SQLiteDatabase实例
     */
    public static SQLiteDatabase getDatabase(final Context context) {
        if (DatabaseHelper.sDb == null) {
            synchronized (DatabaseHelper.sObjLock) {
                if (DatabaseHelper.sDb == null) {
                    DatabaseHelper.sDb = DatabaseHelper.getInstance(context)
                            .getWritableDatabase();
                }
            }
        } else {
            while (DatabaseHelper.sDb.isDbLockedByCurrentThread()
                    || DatabaseHelper.sDb.isDbLockedByCurrentThread()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
        }
        return DatabaseHelper.sDb;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
     * .SQLiteDatabase)
     */
    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(DeviceProtocolModel.CREATE_TABLE);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
     * .SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
                          final int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS '"
                    + DeviceProtocolModel.TABLE_NAME + "'");
            onCreate(db);
        }
    }
}
