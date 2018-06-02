/*
 * -----------------------------------------------------------------
 * Copyright ?2014 clife - 和而泰家居在线网络科技有限公司
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: DeviceProtocolDao.java
 * Create: 2015/9/16 11:02
 */
package com.het.udp.wifi.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by IntelliJ IDEA.
 * User: UUXIA
 * Date: 2015/9/16
 * Time: 11:02
 * Description: 设备协议数据库操作
 */
public class DeviceProtocolDao {
    private SQLiteDatabase mDb;

    public DeviceProtocolDao(Context ctx) {
        mDb = DatabaseHelper.getDatabase(ctx);
    }


    public DeviceProtocolModel get(int productId) {
        String strSelection = DeviceProtocolModel.PRODUCTID + " = ?";
        String[] strSelectionArgs = {String.valueOf(productId)};
        Cursor cursor = mDb.query(DeviceProtocolModel.TABLE_NAME,
                DeviceProtocolModel.COLUMNS, strSelection, strSelectionArgs,
                null, null, null);
        DeviceProtocolModel model = null;
        if (cursor.moveToFirst()) {
            model = DeviceProtocolModel.parse(cursor);
        }
        cursor.close();
        return model;
    }


    public boolean save(DeviceProtocolModel model) {
        DeviceProtocolModel existModel = get(model.getProductId());
        boolean bIsSuccess = false;
        long lRowId = -1;
        if (existModel == null) {
            lRowId = mDb.insert(DeviceProtocolModel.TABLE_NAME, null,
                    model.toContentValues());
        } else {
            lRowId = update(model);
        }
        if (lRowId != -1) {
            bIsSuccess = true;
        }
        return bIsSuccess;
    }

    public int update(DeviceProtocolModel model) {
        int iRowId = -1;
        try {
            mDb.beginTransaction();
            int strUserId = model.getProductId();
            String strWhereClause = DeviceProtocolModel.PRODUCTID + " = ?";
            String[] strWhereArgs = new String[]{String.valueOf(strUserId)};
            iRowId = mDb.update(DeviceProtocolModel.TABLE_NAME,
                    model.toContentValues(), strWhereClause, strWhereArgs);
            if (iRowId != -1) {
                mDb.setTransactionSuccessful();
            }
        } finally {
            mDb.endTransaction();
        }
        return iRowId;
    }

    public boolean insert(final DeviceProtocolModel deviceProtocolModel) {
        boolean bIsSuccess = true;
        try {
            mDb.beginTransaction();
            bIsSuccess = save(deviceProtocolModel);
            if (bIsSuccess) {
                mDb.setTransactionSuccessful();
            }
        } finally {
            mDb.endTransaction();
        }
        return bIsSuccess;
    }
}
