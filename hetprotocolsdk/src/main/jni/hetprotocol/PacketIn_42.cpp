//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: PacketIn_42.java
 * Create: 2016/3/11 11:22
 * Author: uuxia
 */
#include "PacketIn_42.h"
PacketIn_42::PacketIn_42(PacketModel* nPacketModel)
{
    Logc("call PacketIn_42::PacketIn_42\n");
    this->m_packet = nPacketModel;
    this->m_packet->packetmodel_42.packetStart = 0xF2;
    this->m_packet->packetmodel_42.protocolVersion = 0x42;
    this->m_packet->packetmodel_42.protocolType = 0x02;
}

PacketIn_42::~PacketIn_42()
{
    Logc("call PacketIn_42::~PacketIn_42\n");
    if (m_packet)
    {
        if (m_packet->packetmodel_42.frameBody)
        {
            delete m_packet->packetmodel_42.frameBody;
            m_packet->packetmodel_42.frameBody = NULL;
        }
        if (m_packet->data)
        {
            delete m_packet->data;
            m_packet->data = NULL;
        }
        delete m_packet;
        m_packet = NULL;
    }
}

bool PacketIn_42::validateHeader(unsigned char *buf)
{
    Logc("PacketIn_42::validateHeader\n");
    if (buf && this->m_packet->packetstart == buf[0] && m_packet->dataLen >= HET_42_DATA_LEN) {
        return true;
    } else {
        Logc("parse packetIn_41 error. buf is:%s\n",buf);
        return false;
    }
}

unsigned char* PacketIn_42::calcBody(unsigned char* buf, int length)
{
    Logc("call PacketIn_42::calcBody\n");
    int bodyLen = length - HET_42_DATA_LEN;
    if (bodyLen == this->m_packet->packetmodel_42.dataLen)
    {
        this->m_packet->packetmodel_42.frameBody = new unsigned char[bodyLen];
        if (bodyLen > 0)
        {
            memset(this->m_packet->packetmodel_42.frameBody,0,bodyLen);
            memcpy(this->m_packet->packetmodel_42.frameBody,buf + 35,bodyLen);
        }
    }
    m_packet->bodyLen = bodyLen;
    return m_packet->packetmodel_42.frameBody;
}

void PacketIn_42::parseHeader(unsigned char* buf)
{
    Logc("call PacketIn_42::parseHeader\n");
    if (buf)
    {
        memcpy(&m_packet->packetmodel_42.protocolVersion,buf + 1,1);//获取协议版本
        memcpy(&m_packet->packetmodel_42.protocolType,buf + 2,1);//获取协议类型
        memcpy(&m_packet->packetmodel_42.commandType,buf + 3,2);//获取命令字
        convertToLittleEndianFor16Bit(&m_packet->packetmodel_42.commandType);
        memcpy(m_packet->packetmodel_42.macAddr,buf + 5,6);//获取Mac地址
        memcpy(m_packet->packetmodel_42.deviceType,buf + 11,8);//获取设备大小分类
        memcpy(&m_packet->packetmodel_42.dataStatus,buf + 19,1);//获取数据状态
        memcpy(&m_packet->packetmodel_42.wifiStatus,buf + 20,1);//获取WiFi状态
        memcpy(&m_packet->packetmodel_42.frameSN,buf + 21,4);//获取数据帧序号
        memcpy(m_packet->packetmodel_42.reserved,buf + 25,8);//获取保留字
        memcpy(&m_packet->packetmodel_42.dataLen,buf + 33,2);//获取数据包体长度
        convertToLittleEndianFor16Bit(&m_packet->packetmodel_42.commandType);
    }
}

bool PacketIn_42::parseTail(unsigned char* buf)
{
    Logc("call PacketIn_42::parseTail\n");
    int crclen = sizeof(this->m_packet->packetmodel_42.fcs)/sizeof(this->m_packet->packetmodel_42.fcs[0]);
    memset(this->m_packet->packetmodel_42.fcs,0,crclen);
    int crcbodylen = sizeof(this->m_packet->packetmodel_42.frameBodyCrc)/sizeof(this->m_packet->packetmodel_42.frameBodyCrc[0]);
    memset(this->m_packet->packetmodel_42.frameBodyCrc,0,crcbodylen);

    memcpy(m_packet->packetmodel_42.frameBodyCrc,buf + 35 + m_packet->packetmodel_42.dataLen,crcbodylen);
    memcpy(m_packet->packetmodel_42.fcs,buf + 37 + m_packet->packetmodel_42.dataLen,crclen);

    int len = HET_42_DATA_LEN + m_packet->bodyLen - 3;
    unsigned char fcs[2];
    CRC16x25Calc(fcs,buf + 1, len);
    if (fcs[0] != m_packet->packetmodel_42.fcs[0] || fcs[1] != m_packet->packetmodel_42.fcs[1]) {
        return false;
    }
    return true;
}

PacketModel* PacketIn_42::toPacketModel()
{
    Logc("PacketIn_42::toPacketModel\n");
    return m_packet;
}

PacketModel* PacketIn_42::packetIn() throw(Exception)
{
    Logc("call PacketIn_42::packetIn\n");
    if (m_packet && m_packet->data) {
        //获取数据包总长度
        int length = m_packet->dataLen;
        if (length < HET_42_DATA_LEN) {
            throw CreateException(ERROR_PACKET_INVALLIDATE, "packet'size less than packet42's size,actual:%d",length);
        }
        Logc("get data.length:%d!\n",length);
        //校验数据包头合法性
        bool isvalidate = validateHeader(m_packet->data);
        if (!isvalidate) {
            throw CreateException(ERROR_INVALIDATE_HEADER, "invalidate header42 error ,header is:%X", m_packet->data[0]);
        }
        //解析数据包头
        parseHeader(m_packet->data);
        if (m_packet->dataLen < (m_packet->packetmodel_42.dataLen + HET_42_DATA_LEN)) {
            throw CreateException(ERROR_PACKET_INVALLIDATE, "packet42's size(%d) less than packet's body size(%d)",m_packet->dataLen,(m_packet->packetmodel_42.dataLen + HET_42_DATA_LEN));
        }
        /**得到包体*/
        if (m_packet->packetmodel_42.dataLen > 0) {
            m_packet->packetmodel_42.frameBody = calcBody(m_packet->data, length);
        }
        bool iscrcvalidate = parseTail(m_packet->data);
        if (!iscrcvalidate) {
            throw CreateException(ERROR_CRC_ERROR, "packet42's crc is error");

        }
        return toPacketModel();
    } else {
        Logc("协议包头错误!\n");
        throw CreateException(ERROR_INVALIDATE_HEADER, "packet42in is error");
    }
}