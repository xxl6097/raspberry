//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: PacketModel.java
 * Create: 2016/3/10 11:40
 * Author: uuxia
 */
#ifndef WORK_PACKETMODEL_H
#define WORK_PACKETMODEL_H
#include <stdio.h>
#include <string.h>
#include <stdarg.h>
#include "Utils.h"
#include "ErrorCode.h"

//5A协议的长度
const int HET_5A_DATA_LEN = 35;
//F241协议长度
const int HET_41_DATA_LEN = 18;
//F242协议长度
const int HET_42_DATA_LEN = 39;
//5A协议头
const int HET_5A_PROTOCOL = 0x5A;
//F2协议头
const int HET_F2_PROTOCOL = 0xF2;
//F241协议版本
const int HET_F2_PROTOCOL_41 = 0x41;
//F242协议版本
const int HET_F2_PROTOCOL_42 = 0x42;

struct BasicPacket
{
    //报文起始
    unsigned char packetStart;
    //软件版本，详情见版本表(注1)
    unsigned char protocolVersion;
    //协议类型，该协议值为0x10.
    unsigned char protocolType;
    //相关操作命令字,高字节为数据方向
    unsigned short commandType;
    //上行账号字节组（设备MAC地址)
    unsigned char macAddr[6];
    //Frame body的长度
    unsigned short dataLen;
    //帧数据段字节数，合法值范围：0～1024(注2)
    unsigned char* frameBody;
    //帧数据段内容CRC16校验值(CRC-16/X25 X16+X12+X5+1)
    unsigned char fcs[2];
};

/**
 * --------------------------------------------------------------------------------------------------------
 * | 0x5A | 数据长度 | 框架版本 | 协议类型 | 设备编码 | Mac地址 | 数据帧序列号 | 保留字 | 数据类型 | 数据内容| 检验码FCS  |
 * |-------------------------------------------------------------------------------------------------------
 * | 1byte|  2byte |  1byte |   1byte |  8byte |  6byte  |   4byte   | 8byte |   2byte | nbyte |2byte
 * ------------------------------------------------------------------------------------------
 * 5a 00 22 40 00 00 00 01 99 00 0b 03 01 8c 88 2b 00 00 50 00 00 00 00 00 00 00 00 00 00 00 00 04 00 89 4c
 */
struct PacketModel_5A:BasicPacket
{
    //设备编码 在开发前申请 包括设备品牌【4】设备的类型，高字节大分类【2】，低字节小分类【1】，数据协议版本【1】
    unsigned char deviceId[8];
    //数据帧序号
    unsigned int frameSN;
    //保留位
    unsigned char reserved[8];
};

/**
 * v42版本协议格式
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * | 0xF2 | Protocol Version | Protocol Type | Command Type | Mac Addr | Device Type | Frame Control & WIFI status | Frame SN | Reserved | Length | Frame Body | Frame Body FCS | FCS  |
 * |-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
 * | 1byte|       1byte      |     1byte     |      2byte   |   6byte  |     8byte   |               2byte         |   4byte  |   8byte  |  2byte |   Nbyte    |       2byte    | 2byte|
 * -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * <p/>
 * 0xF2: 帧开始标志;
 * Protocol Version:协议版本，详情见版本表(注 1);
 * Protocol Type:协议类型 升级协议为0x10,业务数据 0x00,绑定协议为 0x02;
 * Command Type:相关操作命令字,高字节为数据方向; Bit7~Bit7  数据源：10-服务器 01-手机 00-终端
 * Mac Addr:客户端 WIFI 模组的 MAC 地址 ACCF233BA86A;
 * Device Type:包括设备品牌【4】设备的类型，高字节大分类【1】，低字节小分类【1】，数据协议版本【1】，保留【1】;
 * Frame Control & WIFI status:高字节数据状态，低字 WIFI 信号强度;(注 2)
 * Frame SN:数据帧序号;
 * Reserved:保留位;
 * Length:Frame body 的长度，汉枫模块不超过200 字节;
 * Frame Body:帧数据段字节数，合法值范围：0～1024(注)，汉枫模块不超过 200 字节;
 * Frame Body FCS:Frame Body 部分未加密前的 FCS,不加密的数据填 0x0000;
 * FCS:帧数据段内容 CRC16 校验值(CRC-16/X25 X16+X12+X5+1),包含所有帧数据除外不包含0xF2;
 * <p/>
 * <p/>
 * 注1：协议版本号这里包括主协议版本和子协议版本。详情如下表 4-2。
 * 表 4-2 协议版本说明
 * --------------------------------------------------------------
 * |      类型        |      数据值       |         类型描述        |
 * |--------------------------------------------------------------|
 * |                 |       00         |       老版本协议         |
 * |                 |--------------------------------------------|
 * | 主版本号(b7b6)   |       11         |       新版本协议          |
 * |                 |--------------------------------------------|
 * |                 |     10~11        |          预留            |
 * |--------------------------------------------------------------|
 * |    子协议版本号   |     000~111      |      根据具体子协议而定    |
 * | （b5b4b3.b2b1b0) |                  |                        |
 * --------------------------------------------------------------
 * <p/>
 * 注2：数据状态（如下表 4-3）和WiFi状态(如下表 4-4)；
 * <p/>
 * 表 4-3 数据状态
 * ------------------------------------------------------------------------------------------------------------------------------------------------
 * |         数据及数据状态          |      Bit7    |    Bit6    |    Bit5      |                 Bit4                               |   Bit3~Bit0    |
 * |------------------------------------------------------------------------------------------------------------------------------------------------|
 * |   Bit4~Bit7标示当前数据帧的性质 |   1:发送数据 |  1:请求数据 |   1:应答数据 |   0：数据需要应答；1：数据不用应答【只结合发送数据使用】   |    保留        |
 * ------------------------------------------------------------------------------------------------------------------------------------------------
 * <p/>
 * 表 4-4 WIFI状态
 * ------------------------------------------------------------------------------------------------------------------------
 * |         WIFI状态      |      Bit7     |      Bit6     |       Bit5     |        Bit4      |          Bit3~Bit0          |
 * ------------------------------------------------------------------------------------------------------------------------
 * |   Bit0~Bit3信号强度   |     保留      |      保留     |       保留     |       保留       |  WIFI信号强度0~10对应0%~100%  |
 * ------------------------------------------------------------------------------------------------------------------------
 */
struct PacketModel_42:BasicPacket
{
    //包括设备品牌【4】设备的类型，高字节大分类【1】，低字节小分类【1】，数据协议版本【1】，保留【1】
    unsigned char deviceType[8];
    //高字节数据状态，低字节WIFI信号强度
    unsigned char dataStatus;
    //WIFI信号强度
    unsigned char wifiStatus;
    //数据帧序号
    unsigned int frameSN;
    //保留位
    unsigned char reserved[8];
    //Frame Body 部分未加密前的FCS
    unsigned char frameBodyCrc[2];
};



/**
 * v41版本协议格式
 * -------------------------------------------------------------------------------------------------------------------------
 * | 0xF2 | Protocol Version | Protocol Type | Command Type | Mac Addr | Device Type | Reserved | Length | Frame Body | FCS  |
 * |-------------------------------------------------------------------------------------------------------------------------|
 * | 1byte|       1byte      |     1byte     |      2byte   |   6byte  |     2byte   |   1byte  |  2byte |   Nbyte    | 2byte|
 * -------------------------------------------------------------------------------------------------------------------------
 * <p/>
 * 0xF2: 帧开始标志;
 * Protocol Version:协议版本，详情见版本表(注 1);
 * Protocol Type:协议类型 升级协议为0x10,业务数据 0x00,绑定协议为 0x02;
 * Command Type:相关操作命令字,高字节为数据方向; Bit7~Bit7  数据源：10-服务器 01-手机 00-终端
 * Mac Addr:客户端 WIFI 模组的 MAC 地址 ACCF233BA86A;
 * Device Type:包括设备品牌【4】设备的类型，高字节大分类【1】，低字节小分类【1】，数据协议版本【1】，保留【1】;
 * Reserved:保留位;
 * WiFi状态信息
 WiFi状态信息	BIT7	BIT6	BIT5	BIT4	BIT3	BIT2	BIT1	BIT0
 绑定状态	服务器	路由器	WIFI信号强度
 1-绑定成功	1-已连接	1-已连接 预留
 0-未绑定	0-未连接	0-未连接
 * Length:Frame body 的长度，汉枫模块不超过200 字节;
 * Frame Body:帧数据段字节数，合法值范围：0～1024(注)，汉枫模块不超过 200 字节;
 * FCS:帧数据段内容 CRC16 校验值(CRC-16/X25 X16+X12+X5+1),包含所有帧数据除外不包含0xF2;
 */
struct PacketModel_41:BasicPacket
{
    //设备的类型0811
    unsigned char deviceType[2];
    //保留位
    unsigned char reserved;
};

//数据包数据模型结构体
struct PacketModel
{
    //完整数据包地址
    unsigned char* data;
    //封包的时候填充该值，解包的时候也填充该值
    unsigned int dataLen;
    //包体长度，封包的时候必须制定，解包的时候填充该值
    unsigned short bodyLen;
    //协议首字节，用于区分协议类型
    unsigned char packetstart;
    //协议版本号
    unsigned char protocolversion;
    //5A协议数据模型
    PacketModel_5A packetmodel_5a;
    //F242数据模型
    PacketModel_42 packetmodel_42;
    //F241数据模型
    PacketModel_41 packetmodel_41;
};

struct DataModel
{
    unsigned char* data;
    unsigned int size;
};

struct Exception
{
    int errorcode;
    char errormsg[100];
};

/*static Exception CreatePacketError(int errCode,char* errmsg)
{
    Exception error;
    memset(&error,0, sizeof(error));
    error.errorcode = errCode;
    error.errormsg = errmsg;
    return error;
}
 */

static Exception CreateException(int code, const char * format, ...)
{
    Exception error;
    int len =sizeof(error.errormsg)/sizeof(error.errormsg[0]);
    memset(&error,0, sizeof(error));
    memset(error.errormsg, 0, len);
    va_list st;
    va_start(st, format);
    vsprintf(error.errormsg, format, st);
    va_end(st);
    error.errorcode = code;
    return error;
}

#endif //WORK_PACKETMODEL_H
