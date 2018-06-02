/*
 * -----------------------------------------------------------------
 * Copyright ?2014 clife - 和而泰家居在线网络科技有限公司
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 *
 * File: DeviceProtocolModel.java
 * Create: 2015/9/16 11:03
 */
package com.het.udp.wifi.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * Created by IntelliJ IDEA.
 * User: UUXIA
 * Date: 2015/9/16
 * Time: 11:03
 * Description: 协议数据模型
 */
public class DeviceProtocolModel extends AbstractModel<DeviceProtocolModel> {
    /**
     * 表名.
     */
    public static final String TABLE_NAME = "protocolxml";

    public static final String PRODUCTID = "productId";
    public static final String PROTOCOLDATE = "protocolDate";
    public static final String PROTOCOLID = "protocolId";
    public static final String BASE64DATA = "base64data";
    public static final String UPDATE_TIME = "update_time";
    public static final String[] COLUMNS = {PRODUCTID, PROTOCOLDATE, PROTOCOLID, BASE64DATA, UPDATE_TIME};
    /**
     * 创建表的语句.
     */
    public static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME + " (" + BaseColumns._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PRODUCTID + " INTEGER, "
            + PROTOCOLDATE + " TEXT, "
            + PROTOCOLID + " TEXT, "
            + BASE64DATA + " TEXT, "
            + UPDATE_TIME + " TEXT);";

    private int productId;
    private String protocolDate;
    private String protocolId;
    private String updateTime;
    private String base64data;

    /**
     * 将游标中的数据封装成表模
     *
     * @param cursor 游标
     * @return 封装后的表模型，null表示未成??
     */
    public static DeviceProtocolModel parse(final Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        DeviceProtocolModel model = new DeviceProtocolModel();
        int iIndex = 0;
        // CSOFF
        if ((iIndex = cursor.getColumnIndex(PRODUCTID)) != -1) {
            model.setProductId(cursor.getInt(iIndex));
        }
        if ((iIndex = cursor.getColumnIndex(PROTOCOLDATE)) != -1) {
            model.setProtocolDate(cursor.getString(iIndex));
        }

        if ((iIndex = cursor.getColumnIndex(PROTOCOLID)) != -1) {
            model.setProtocolId(cursor.getString(iIndex));
        }
        if ((iIndex = cursor.getColumnIndex(BASE64DATA)) != -1) {
            model.setBase64data(cursor.getString(iIndex));
        }
        if ((iIndex = cursor.getColumnIndex(UPDATE_TIME)) != -1) {
            model.setUpdateTime(cursor.getString(iIndex));
        }
        // CSON
        return model;
    }


    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProtocolDate() {
        return protocolDate;
    }

    public void setProtocolDate(String protocolDate) {
        this.protocolDate = protocolDate;
    }

    public String getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(String protocolId) {
        this.protocolId = protocolId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getBase64data() {
        return base64data;
    }

    public void setBase64data(String base64data) {
        this.base64data = base64data;
    }

    @Override
    public String toString() {
        return "DeviceProtocolModel{" +
                "productId=" + productId +
                ", protocolDate='" + protocolDate + '\'' +
                ", protocolId='" + protocolId + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", base64data='" + base64data + '\'' +
                '}';
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(PRODUCTID, productId);
        values.put(PROTOCOLDATE, protocolDate);
        values.put(PROTOCOLID, protocolId);
        values.put(BASE64DATA, base64data);
        values.put(UPDATE_TIME, updateTime);
        return values;
    }

}
