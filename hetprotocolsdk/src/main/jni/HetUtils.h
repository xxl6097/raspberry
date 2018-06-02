//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: HetUtils.h
 * Create: 2016/3/2 20:01
 * Author: uuxia
 */
#ifndef WORK_HETUTILS_H
#define WORK_HETUTILS_H
#include <jni.h>
#include <stddef.h>
#include <string.h>
//errno
#include <errno.h>
#include "Android_log_print.h"
#include "hetprotocol/PacketUtils.h"
//MAX log message length
#define MAX_LOG_MESSAGE_LENGTH 1024 * 5

//通过异常类和异常信息抛出异常
static void ThrowException(JNIEnv* env, const char* className, const char* message)
{
    jclass clazz = env->FindClass(className);
    /* if clazz is NULL, an exception has already been thrown */
    if (clazz != NULL) {
        env->ThrowNew(clazz, message);
        /* free the local ref */
        env->DeleteLocalRef(clazz);
    }
}

static void IllegalArgumentException(JNIEnv* env,jthrowable throws, const char* msg)
{
    throws = env->ExceptionOccurred();
    if (throws)  // 如果发生了异常
    {
        jclass newExceptionClazz;
        env->ExceptionDescribe();
        env->ExceptionClear();

        newExceptionClazz = env->FindClass("java/lang/IllegalArgumentException"); //实例化一个参数不合法异常

        if (newExceptionClazz == NULL)
        {
            return;
        }
        env->ThrowNew(newExceptionClazz, msg); //在JNI中抛出异常
    }
}

static void IOException(JNIEnv* env, const char* message)
{
    ThrowException(env,"java/io/IOException",message);
}


//通过异常类和错误号抛出异常
static void ThrowErrnoException(JNIEnv* env, const char* className, int errnum)
{
    char buffer[MAX_LOG_MESSAGE_LENGTH];
    //通过错误号获得错误消息
    if (-1 == strerror_r(errnum, buffer, MAX_LOG_MESSAGE_LENGTH)) {
        strerror_r(errno, buffer, MAX_LOG_MESSAGE_LENGTH);
    }
    ThrowException(env, className, buffer);
}


/**
 * 将jbyteArray转换成char*
 */
static DataModel* jbyteArrayToChar(JNIEnv* env, jbyteArray bytes)
{
    if (bytes == NULL)
        return NULL;
    int len = env-> GetArrayLength(bytes);
    if (len <= 0)
        return NULL;
    DataModel* dataModel = new DataModel();
    dataModel->data = NULL;
    dataModel->size = 0;
    unsigned char* rtn = NULL;
    jbyte* arrayBody = env->GetByteArrayElements(bytes,0);
    if(arrayBody == NULL)
    {
        delete dataModel;
        dataModel = NULL;
        return NULL;
    }
    if(len > 0)
    {
        rtn = new unsigned char[len + 1];
        memcpy(rtn, arrayBody, len);
        rtn[len] = 0;
    }
    dataModel->data = rtn;
    dataModel->size = len;
    env->ReleaseByteArrayElements(bytes, arrayBody, 0);
    return dataModel;
}


static jbyteArray char2jbytearray(JNIEnv* env, char* data,int dataLen)
{
    if(data == NULL)
    {
        return NULL;
    }
    if(dataLen <= 0)
    {
        return NULL;
    }
    jbyte *by = (jbyte*)data;
    Logc("data.size:%d\n",dataLen);
    jbyteArray jData = env->NewByteArray(dataLen);
    env->SetByteArrayRegion(jData,0,dataLen,by);
    return jData;
}

static jbyteArray GetJbyteArray(JNIEnv* env, unsigned char* data,int dataLen)
{
    if(data == NULL || dataLen <= 0)
    {
        return NULL;
    }
    jbyte *by = (jbyte*)data;
    Logc("data.size:%d\n",dataLen);
    jbyteArray jData = env->NewByteArray(dataLen);
    env->SetByteArrayRegion(jData,0,dataLen,by);
    return jData;
}

static jbyteArray GetDataTojbyteArray(JNIEnv* env, PacketModel* data)
{
    if(data == NULL || data->data == NULL || data->dataLen <= 0)
    {
        return NULL;
    }
    jbyte *by = (jbyte*)data->data;
    int dataLen = data->dataLen;
    Logc("data.size:%d\n",dataLen);
    jbyteArray jData = env->NewByteArray(dataLen);
    env->SetByteArrayRegion(jData,0,dataLen,by);
    return jData;
}

static BasicPacket* GetWhitchPacketModel(PacketModel* data)
{
    if (data->packetstart == HET_5A_PROTOCOL)
    {
        return &(data->packetmodel_5a);
    }
    else if (data->packetstart == HET_F2_PROTOCOL)
    {
        if (data->protocolversion == HET_F2_PROTOCOL_41)
        {
            return &(data->packetmodel_41);
        }
        else if(data->protocolversion == HET_F2_PROTOCOL_42)
        {
            return &(data->packetmodel_42);
        }
    }
    return NULL;
}

static jbyteArray GetBodyTojbyteArray(JNIEnv* env, PacketModel* data)
{
    int len = data->bodyLen;
    Logc("body.size:%d\n",len);
    if (len <= 0)
    {
        Logc("len <= 0:%d\n",len);
        return NULL;
    }
    jbyte *by = NULL;
    BasicPacket* basic = GetWhitchPacketModel(data);
    if (NULL == basic)
    {
        Logc("BasicPacket is null\n");
        return NULL;
    }
    if (NULL == basic->frameBody)
    {
        Logc("basic->frameBody is null\n");
        return NULL;
    }
    by = (jbyte *) basic->frameBody;
    if (NULL == by)
    {
        Logc("basic->frameBody's jbyte is null\n");
        return NULL;
    }
    jbyteArray jBody = env->NewByteArray(len);
    env->SetByteArrayRegion(jBody, 0, len, by);
    if (NULL == jBody)
    {
        Logc("jBody is null\n");
        return NULL;
    }
    Logc("oh,SetByteArrayRegion for jBody sucess : \n");
    return jBody;
}

jobject fillPacketModel(JNIEnv* env, jobject obj, jobject packetModel)
{
    Logc("Enter fillPacketModel");
    if(NULL == packetModel)
    {
        Logc("sorry, packetModel is NULL");
        return null;
    }
    //packetModel = NULL;
    /*否则就传入一个jclass对象表示native()方法所在的类*/
    jclass pm_class = env->GetObjectClass(packetModel);
    if (pm_class == NULL)
    {
        Logc("fillPacketModel packetModel jclass is null");
        return null;
    }
    /*得到jfieldID userKey body deviceInfo*/
    jfieldID jfid_data = env->GetFieldID(pm_class,"data","[B");
    jfieldID jfid_deviceInfo = env->GetFieldID(pm_class,"deviceInfo","Lcom/het/udp/wifi/model/UdpDeviceDataBean;");
    /*得到设备UdpDeviceDataBean属性*/
    jobject deviceModel = env->GetObjectField(packetModel, jfid_deviceInfo);
    /*获取设备业务数据*/
    jobject jdata = env->GetObjectField(packetModel, jfid_data);
    jbyteArray jdataarr = NULL;
    if (jdata != NULL)
    {
        jdataarr = (jbyteArray) jdata;
    }
    /*获取设备信息*/
    jclass dm_class = NULL;
    if (deviceModel != NULL)
    {
        dm_class = env->GetObjectClass(deviceModel);
    }
    else
    {
        //更加包命获取Java类
        dm_class = env->FindClass("com/het/udp/wifi/model/UdpDeviceDataBean");
        if (dm_class == NULL)
        {
            Logc("createJaveUdpDeviceDataBean PacketByffer jclass is null");
            return NULL;
        }
        //获取Java类中无参构造函数
        jmethodID dm_init_mid   = env->GetMethodID(dm_class,"<init>","()V");
        if (dm_init_mid == NULL)
        {
            Logc("obj_init_jmid jmethodID is null");
            env->DeleteLocalRef(dm_class);
            return NULL;
        }
        //创建UdpDeviceDataBean Java对象
        deviceModel = env->NewObject(dm_class,dm_init_mid);
        if (deviceModel == NULL)
        {
            Logc("j_obj jobject is null");
            env->DeleteLocalRef(dm_class);
            return deviceModel;
        }
    }

    if (dm_class == NULL)
    {
        return null;
    }





    DataModel* dataModel = jbyteArrayToChar(env,jdataarr);
    if (NULL == dataModel)
    {
        return NULL;
    }
    if (NULL == dataModel->data)
    {
        return NULL;
    }
    PacketModel* data = in(dataModel);
    if (NULL != dataModel)
    {
        delete dataModel;
        dataModel = NULL;
    }



    Logc("create deviceModel Javabean is sucess.");

    jbyteArray jData = GetDataTojbyteArray(env, data);
    jbyteArray jBody = GetBodyTojbyteArray(env, data);

    BasicPacket* basic = GetWhitchPacketModel(data);


    //0.获取类中setData(byte[] data)方法
    jmethodID setPacketModelOpenProtocol = env->GetMethodID(pm_class, "setOpenProtocol", "(Z)V");
    //1.获取类中setData(byte[] data)方法
    jmethodID setdata_jmid = env->GetMethodID(pm_class, "setData", "([B)V");
    //2.获取类中setBody(byte[] data)方法
    jmethodID setbody_jmid = env->GetMethodID(pm_class, "setBody", "([B)V");
    //3.setDeviceInfo(Lcom/het/udp/wifi//model/PacketModel)方法
    jmethodID setDeviceInfo = env->GetMethodID(pm_class, "setDeviceInfo", "(Lcom/het/udp/wifi/model/UdpDeviceDataBean;)V");
    //4.获取类中setPacketStart方法
    jmethodID setPacketStart = env->GetMethodID(dm_class, "setPacketStart", "(B)V");
    //5.获取类中setProtocolVersion方法
    jmethodID setProtocolVersion = env->GetMethodID(dm_class, "setProtocolVersion", "(B)V");
    //6.获取类中setProtocolType方法
    jmethodID setProtocolType = env->GetMethodID(dm_class, "setProtocolType", "(B)V");
    //7.获取类中setProtocolType方法
    jmethodID setCommandType = env->GetMethodID(dm_class, "setCommandType", "(S)V");
    //8.获取类中setDeviceMac方法
    jmethodID setDeviceMac = env->GetMethodID(dm_class, "setDeviceMacArray", "([B)V");
    //9.获取类中setDeviceType方法
    jmethodID setDeviceType = env->GetMethodID(dm_class, "setDeviceType", "(B)V");
    //10.获取类中setDeviceSubType方法
    jmethodID setDeviceSubType = env->GetMethodID(dm_class, "setDeviceSubType", "(B)V");
    //11.获取类中setCustomerId方法
    jmethodID setCustomerId = env->GetMethodID(dm_class, "setCustomerId", "(I)V");
    //12.获取类中setFrameSN方法
    jmethodID setFrameSN = env->GetMethodID(dm_class, "setFrameSN", "(I)V");
    //13.获取类中setNewDeviceTypeForOpen方法
    jmethodID setNewDeviceTypeForOpen = env->GetMethodID(dm_class, "setNewDeviceTypeForOpen", "([B)V");
    //13.获取类中setOpenProtocol方法
    jmethodID setOpenProtocol = env->GetMethodID(dm_class, "setOpenProtocol", "(Z)V");


    if (setdata_jmid == NULL || setbody_jmid == NULL || setDeviceInfo == NULL || setPacketStart == NULL ||
        setProtocolVersion  == NULL|| setProtocolType == NULL || NULL == setCommandType || NULL == setDeviceMac ||
        NULL == setDeviceType)
    {
        env->DeleteLocalRef(dm_class);
        env->DeleteLocalRef(pm_class);
        env->DeleteLocalRef(jData);
        env->DeleteLocalRef(jBody);
        env->DeleteLocalRef(packetModel);
        env->DeleteLocalRef(deviceModel);
        return NULL;
    }


    //1.设置setData
    env->CallVoidMethod(packetModel, setdata_jmid, jData);
    Logc("CallVoidMethod setData method sucess.");
    //2.设置setBody
    env->CallVoidMethod(packetModel, setbody_jmid, jBody);
    Logc("CallVoidMethod setBody method sucess.");

    //3.设置setPacketStart
    jbyte packetstart = (jbyte)data->packetstart;
    Logc("packetstart filed sucess.");
    env->CallVoidMethod(deviceModel, setPacketStart, packetstart);
    Logc("CallVoidMethod setPacketStart method sucess.");
    //4.设置setProtocolVersion
    jbyte protocolversion = (jbyte)data->protocolversion;
    env->CallVoidMethod(deviceModel, setProtocolVersion, protocolversion);
    Logc("CallVoidMethod setProtocolVersion method sucess.");
    //5.设置setProtocolType
    jbyte protocoltype = (jbyte)basic->protocolType;
    env->CallVoidMethod(deviceModel, setProtocolType, protocoltype);
    Logc("CallVoidMethod setProtocolType method sucess.");
    //6.设置setCommandType
    jshort commandtype = (jshort)basic->commandType;
    unsigned short dCmd = commandtype;
    convertToLittleEndianFor16Bit(&dCmd);
    Logc("packetmodel_5a.commandtype:%d\n",dCmd);
    env->CallVoidMethod(deviceModel, setCommandType, dCmd);
    Logc("CallVoidMethod setCommandType method sucess.");
    //7.设置setDeviceMac
    jbyteArray jMacAddr = GetJbyteArray(env,basic->macAddr,6);
    env->CallVoidMethod(deviceModel, setDeviceMac, jMacAddr);
    Logc("CallVoidMethod setDeviceMac method sucess.");
    //8.设置setCommandType
    jbyte deviceType;
    jbyte subtype;
    jint brandid = 0;
    jint frameSn = 0;
    if (data->packetstart == HET_F2_PROTOCOL)
    {
        //处理F241协议
        if (data->protocolversion == HET_F2_PROTOCOL_41)
        {
            deviceType = (jbyte)data->packetmodel_41.deviceType[0];
            subtype = data->packetmodel_41.deviceType[1];
            frameSn = 0;
        }
        //处理F242协议
        else if(data->protocolversion == HET_F2_PROTOCOL_42)
        {
            int ibrandid = (int)((data->packetmodel_42.deviceType[0] << 24)|(data->packetmodel_42.deviceType[1] << 16)|(data->packetmodel_42.deviceType[2] << 8)|(data->packetmodel_42.deviceType[3]));
            brandid = (jint)ibrandid;
            deviceType = data->packetmodel_42.deviceType[5];
            subtype = data->packetmodel_42.deviceType[6];
            frameSn = (jint)data->packetmodel_42.frameSN;
        }
    }
    //处理5A协议
    else if (data->packetstart == HET_5A_PROTOCOL)
    {
        int ibrandid = (int)((data->packetmodel_5a.deviceId[0] << 24)|(data->packetmodel_5a.deviceId[1] << 16)|(data->packetmodel_5a.deviceId[2] << 8)|(data->packetmodel_5a.deviceId[3]));
        brandid = (jint)ibrandid;
        short sdeviceType = (short)(data->packetmodel_5a.deviceId[4] << 8) | data->packetmodel_5a.deviceId[5];
        char bdeviceType = (char)sdeviceType;
        deviceType = (jbyte)bdeviceType;
        subtype = data->packetmodel_5a.deviceId[6];
        frameSn = (jint)data->packetmodel_5a.frameSN;

        //13.设置setNewDeviceTypeForOpen
        jbyteArray jNewDeviceTypeForOpen = GetJbyteArray(env,data->packetmodel_5a.deviceId,8);
        env->CallVoidMethod(deviceModel, setNewDeviceTypeForOpen, jNewDeviceTypeForOpen);

        env->CallVoidMethod(deviceModel, setOpenProtocol, 1);
        env->CallVoidMethod(packetModel, setPacketModelOpenProtocol, 1);

    }
    env->CallVoidMethod(deviceModel, setDeviceType, deviceType);
    env->CallVoidMethod(deviceModel, setDeviceSubType, subtype);
    env->CallVoidMethod(deviceModel, setCustomerId, brandid);
    env->CallVoidMethod(deviceModel, setFrameSN, frameSn);
    Logc("CallVoidMethod setCommandType method sucess.");

    //设置setDeviceInfo
    env->CallVoidMethod(packetModel, setDeviceInfo, deviceModel);
    Logc("CallVoidMethod setDeviceInfo method sucess.");

    //释放对象的jclass空间
    if (pm_class)
    {
        env->DeleteLocalRef(pm_class);
    }
    if (dm_class)
    {
        env->DeleteLocalRef(dm_class);
    }
    if (jData)
    {
        //释放Data数组jData
        env->DeleteLocalRef(jData);
    }
    if (jBody)
    {
        //释放Body数组jBody
        env->DeleteLocalRef(jBody);
    }
    if (NULL != data)
    {
        delete data;
        data = NULL;
    }
    Logc("end\n");
    return packetModel;
}

static jobject parseData(JNIEnv* env, jobject obj, jobject packetModel)
{
    if (packetModel == NULL)
    {
        return NULL;
    }
    return fillPacketModel(env,obj,packetModel);
}

static jbyteArray takePacketModel(JNIEnv* env,jobject jPacket)
{
    Logc("Enter takePacketModel");
    if(NULL == jPacket)
    {
        Logc("sorry, jPacket is NULL");
        return null;
    }
    /*否则就传入一个jclass对象表示native()方法所在的类*/
    jclass pm_class = env->GetObjectClass(jPacket);
    if (pm_class == NULL)
    {
        Logc("takePacketModel jPacket jclass is null");
        return null;
    }
    /*得到jfieldID userKey body deviceInfo*/
    jfieldID jfid_body = env->GetFieldID(pm_class,"body","[B");
    jfieldID jfid_data = env->GetFieldID(pm_class,"data","[B");
    jfieldID jfid_userKey = env->GetFieldID(pm_class,"userKey","[B");
    jfieldID jfid_deviceInfo = env->GetFieldID(pm_class,"deviceInfo","Lcom/het/udp/wifi/model/UdpDeviceDataBean;");
    /*得到设备UdpDeviceDataBean属性*/
    jobject deviceModel = env->GetObjectField(jPacket, jfid_deviceInfo);
    /*获取设备业务数据*/
    jobject jbody = env->GetObjectField(jPacket, jfid_body);
    jbyteArray body = NULL;
    if (jbody != NULL)
    {
        body = (jbyteArray) jbody;
    }

    /*获取控制秘钥*/
    jobject juserKey = env->GetObjectField(jPacket, jfid_userKey);
    jbyteArray userKey = NULL;
    if (juserKey != NULL)
    {
        userKey = (jbyteArray)juserKey;
    }
    /*获取设备信息*/
    jclass dm_class = NULL;
    if (deviceModel != NULL)
    {
        dm_class = env->GetObjectClass(deviceModel);
    }

    if (dm_class == NULL)
    {
        return null;
    }
    /*报文起始*/
    jfieldID jfid_pstart = env->GetFieldID(dm_class,"packetStart","B");
    /*协议版本号*/
    jfieldID jfid_pversion = env->GetFieldID(dm_class,"protocolVersion","B");
    /*协议类型  0--老协议  1--新协议*/
    jfieldID jfid_ptype = env->GetFieldID(dm_class,"protocolType","B");
    /*控制命令字*/
    jfieldID jfid_pcmd = env->GetFieldID(dm_class,"commandType","S");
    /*设备类型*/
    jfieldID jfid_pdevicetype = env->GetFieldID(dm_class,"deviceType","S");
    /*设备子类型类型*/
    jfieldID jfid_psubtype = env->GetFieldID(dm_class,"deviceSubType","B");
    /*客户ID*/
    jfieldID jfid_pcustomerid = env->GetFieldID(dm_class,"customerId","I");
    /*数据帧序号*/
    jfieldID jfid_pframesn = env->GetFieldID(dm_class,"frameSN","I");
    /*设备mac地址*/
//    jfieldID jfid_pmac = env->GetFieldID(dm_class,"deviceMac","Ljava/lang/String;");
    jmethodID jfid_pmac = env->GetMethodID(dm_class, "getDeviceMacArray", "()[B");
    /*设备productcode*/
    jmethodID jfid_pnewdeviceType = env->GetMethodID(dm_class, "getDeviceTypeForOpen", "()[B");


    jbyte jpacketstart = env->GetByteField(deviceModel,jfid_pstart);
    jbyte jpversion = env->GetByteField(deviceModel,jfid_pversion);
    jbyte ptype = env->GetByteField(deviceModel,jfid_ptype);
    jshort pcmd = env->GetShortField(deviceModel,jfid_pcmd);
    jshort pdevicetype = env->GetShortField(deviceModel,jfid_pdevicetype);
    jbyte psubtype = env->GetByteField(deviceModel,jfid_psubtype);
    jint curstomerid = env->GetIntField(deviceModel,jfid_pcustomerid);
    jint framesn = env->GetIntField(deviceModel,jfid_pframesn);
    jbyteArray macArray = NULL;
    jobject jmac = env->CallObjectMethod(deviceModel,jfid_pmac);//env->GetObjectField(deviceModel,jfid_pmac);
    if (jmac != NULL)
    {
        macArray = (jbyteArray) jmac;
    }

    jbyteArray newDeivceTypeArray = NULL;
    jobject jNewDeivceType = env->CallObjectMethod(deviceModel,jfid_pnewdeviceType);
    if (jNewDeivceType != NULL)
    {
        newDeivceTypeArray = (jbyteArray) jNewDeivceType;
    }

    DataModel* newDeviceIdDataModel = jbyteArrayToChar(env,newDeivceTypeArray);

    DataModel* macDataModel = jbyteArrayToChar(env,macArray);

    unsigned char packetstart = (unsigned char)jpacketstart;
    packetstart &= 0xFF;
    unsigned char pversion = (unsigned char)jpversion;
    pversion &= 0xFF;

    DataModel* bodyDataModel = jbyteArrayToChar(env,body);
    if (NULL == bodyDataModel)
    {
        return null;
    }
    DataModel* userKeyDataModel = jbyteArrayToChar(env,userKey);

    PacketModel* packet = new PacketModel();
    packet->bodyLen = bodyDataModel->size;
    packet->packetstart = (packetstart & 0xFF);
    packet->protocolversion = (pversion & 0xFF);
    if (packetstart == HET_5A_PROTOCOL)
    {
        packet->packetmodel_5a.protocolVersion = pversion;
        packet->packetmodel_5a.protocolType = ptype;
        Logc("packetmodel_5a.ptype:%d\n",packet->packetmodel_5a.protocolType);

        //unsigned short dCmd = pcmd;
        //convertToLittleEndianFor16Bit(&dCmd);
        packet->packetmodel_5a.commandType = pcmd;

        unsigned int brandId = curstomerid;
        convertToLittleEndianFor32Bit(&brandId);
        memcpy(packet->packetmodel_5a.deviceId,&brandId, sizeof(brandId));

        unsigned short dType = pdevicetype;
        convertToLittleEndianFor16Bit(&dType);
        memcpy(&packet->packetmodel_5a.deviceId[4],&dType, sizeof(dType));

        packet->packetmodel_5a.deviceId[6] = psubtype;
        if(newDeviceIdDataModel != NULL)
        {
        packet->packetmodel_5a.deviceId[7] = newDeviceIdDataModel->data[7];
        }

        packet->packetmodel_5a.frameSN = framesn;
        packet->packetmodel_5a.frameBody = bodyDataModel->data;
        if (macDataModel != NULL)
        {
            unsigned char* macData = macDataModel->data;
            int macSize = macDataModel->size;
            if (macData != NULL && macSize > 0)
            {
                memcpy(packet->packetmodel_5a.macAddr,macData,macSize);
            }
            delete macDataModel;
            macDataModel = NULL;
        }
    }else if(packetstart == HET_F2_PROTOCOL)
    {
        if (pversion == HET_F2_PROTOCOL_41)
        {
            packet->packetmodel_41.protocolVersion = pversion;
            packet->packetmodel_41.protocolType = ptype;
            packet->packetmodel_41.commandType = pcmd;
            packet->packetmodel_41.deviceType[0] = pdevicetype;
            packet->packetmodel_41.deviceType[1] = psubtype;
            if (macDataModel != NULL)
            {
                unsigned char* macData = macDataModel->data;
                int macSize = macDataModel->size;
                if (macData != NULL && macSize > 0)
                {
                    memcpy(packet->packetmodel_41.macAddr,macData,macSize);
                }
                delete macDataModel;
                macDataModel = NULL;
            }

        }
        else if(pversion == HET_F2_PROTOCOL_42)
        {
            packet->packetmodel_42.protocolVersion = pversion;
            packet->packetmodel_42.protocolType = ptype;
            packet->packetmodel_42.commandType = pcmd;
            packet->packetmodel_42.deviceType[0] = pdevicetype;
            packet->packetmodel_42.deviceType[1] = psubtype;
            packet->packetmodel_42.frameSN = framesn;

            if (macDataModel != NULL)
            {
                unsigned char* macData = macDataModel->data;
                int macSize = macDataModel->size;
                if (macData != NULL && macSize > 0)
                {
                    memcpy(packet->packetmodel_42.macAddr,macData,macSize);
                }
                delete macDataModel;
                macDataModel = NULL;
            }

            unsigned int brandId = curstomerid;
            convertToLittleEndianFor32Bit(&brandId);
            memcpy(packet->packetmodel_42.deviceType,&brandId, sizeof(brandId));
            packet->packetmodel_42.deviceType[4] = pdevicetype;
            packet->packetmodel_42.deviceType[5] = psubtype;
            packet->packetmodel_42.deviceType[6] = 0;
            packet->packetmodel_42.deviceType[7] = 0;
        }
    }

    DataModel* dataModel = out(packet);
    int len = dataModel->size;
    char data[len];
    memset(data,0,len);
    memcpy(data,dataModel->data,len);
    jbyteArray jdataarray = char2jbytearray(env,data,len);
    env->SetObjectField(jPacket,jfid_data, jdataarray);
    if (packet != NULL)
    {
        delete packet;
        packet = NULL;
    }

    if (bodyDataModel != null)
    {
        delete bodyDataModel;
        bodyDataModel = null;
    }

    if (userKeyDataModel != null)
    {
        delete userKeyDataModel;
        userKeyDataModel = null;
    }

    if (dataModel != null)
    {
        delete dataModel;
        dataModel = null;
    }

    if (pm_class != NULL)
    {
        env->DeleteLocalRef(pm_class);
        pm_class = null;
    }
    if (dm_class != null)
    {
        env->DeleteLocalRef(dm_class);
        dm_class = null;
    }
    return jdataarray;
}


/************************************************************************/
/* 将数据封装Java对象，然后调用Java方法，将对象作为参数传递
/*  public native PacketModel parseData(byte[] data);
/************************************************************************/
static jbyteArray packageData(JNIEnv* env, jobject obj, jobject jData)
{
    if (jData == NULL)
    {
        return NULL;
    }
    return takePacketModel(env,jData);
}
#endif //WORK_HETUTILS_H
