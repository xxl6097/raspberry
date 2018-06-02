//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: PacketIn_5A.java
 * Create: 2016/3/10 12:15
 * Author: uuxia
 */
#include "PacketIn_5A.h"

PacketIn_5A::PacketIn_5A(PacketModel* nPacketModel)
{
    this->m_packet = nPacketModel;
	this->m_packet->packetstart = 0x5A;
    this->m_packet->packetmodel_5a.packetStart = 0x5A;
	this->m_packet->protocolversion = 0x40;
	this->m_packet->packetmodel_5a.protocolVersion = 0x40;
	this->m_packet->packetmodel_5a.protocolType = 0x01;
    Logc("call PacketIn_5A::PacketIn_5A\n");
}


PacketIn_5A::~PacketIn_5A(void)
{
	Logc("call PacketIn_5A::~PacketIn_5A");
	if (m_packet)
	{
		if (m_packet->packetmodel_5a.frameBody)
		{
			delete m_packet->packetmodel_5a.frameBody;
			m_packet->packetmodel_5a.frameBody = NULL;
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

bool PacketIn_5A::validateHeader(unsigned char* buf)
{
    Logc("call PacketIn_5A::validateHeader\n");
	if (buf && this->m_packet->packetstart == buf[0] && m_packet->dataLen >= HET_5A_DATA_LEN) {
        return true;
    } else {
        return false;
    }
}

unsigned char* PacketIn_5A::calcBody(unsigned char* buf, int length)
{
    Logc("call PacketIn_5A::calcBody\n");
    int bodyLen = length - HET_5A_DATA_LEN;
    if (bodyLen == this->m_packet->packetmodel_5a.dataLen)
    {
        this->m_packet->packetmodel_5a.frameBody = new unsigned char[bodyLen];
        if (bodyLen > 0)
        {
            memset(this->m_packet->packetmodel_5a.frameBody,0,bodyLen);
            memcpy(this->m_packet->packetmodel_5a.frameBody,buf + 33,bodyLen);
        }
    }
    m_packet->bodyLen = bodyLen;
    return m_packet->packetmodel_5a.frameBody;
}

void PacketIn_5A::parseHeader(unsigned char* buf)
{
    Logc("call PacketIn_5A::parseHeader\n");
    if (buf)
    {
        memcpy(&m_packet->packetmodel_5a.dataLen,buf + 1,2);
        convertToLittleEndianFor16Bit(&m_packet->packetmodel_5a.dataLen);
        //Logc("dataLen = %x \n",m_packet->packetmodel_5a.dataLen);
        m_packet->packetmodel_5a.dataLen = (m_packet->packetmodel_5a.dataLen - HET_5A_DATA_LEN + 1);
        memcpy(&m_packet->packetmodel_5a.protocolVersion,buf + 3,1);
        memcpy(&m_packet->packetmodel_5a.protocolType,buf + 4,1);
        memset(this->m_packet->packetmodel_5a.deviceId,0,sizeof(this->m_packet->packetmodel_5a.deviceId));
        memcpy(m_packet->packetmodel_5a.deviceId,buf + 5,8);
        //Logc("before dataLen = %x\n",m_packet->packetmodel_5a.deviceId);
        memset(this->m_packet->packetmodel_5a.macAddr,0,sizeof(this->m_packet->packetmodel_5a.macAddr));
        memcpy(m_packet->packetmodel_5a.macAddr,buf + 13,6);
        memcpy(&m_packet->packetmodel_5a.frameSN,buf + 19,4);
        memset(this->m_packet->packetmodel_5a.reserved,0,sizeof(this->m_packet->packetmodel_5a.reserved));
        memcpy(m_packet->packetmodel_5a.reserved,buf + 23,8);
        memcpy(&m_packet->packetmodel_5a.commandType,buf + 31,2);
    }
}

bool PacketIn_5A::parseTail(unsigned char* buf)
{
    Logc("call PacketIn_5A::parseTail\n");
    int crclen = sizeof(this->m_packet->packetmodel_5a.fcs)/sizeof(this->m_packet->packetmodel_5a.fcs[0]);
    memset(this->m_packet->packetmodel_5a.fcs,0,crclen);
    memcpy(m_packet->packetmodel_5a.fcs,buf + 33 + m_packet->packetmodel_5a.dataLen,crclen);

    int len = HET_5A_DATA_LEN + m_packet->bodyLen - 3;
    unsigned char fcs[2];
    CRC16x25Calc(fcs,buf + 1, len);
    if (fcs[0] != m_packet->packetmodel_5a.fcs[0] || fcs[1] != m_packet->packetmodel_5a.fcs[1]) {
        return false;
    }
    return true;
}

PacketModel* PacketIn_5A::toPacketModel()
{
    Logc("call PacketIn_5A::toPacketModel\n");
    return m_packet;
}

PacketModel* PacketIn_5A::packetIn() throw(Exception)
{
    Logc("call PacketIn_5A::packetIn\n");
    if (m_packet && m_packet->data) {
		//获取数据包总长度
        int length = m_packet->dataLen;
        if (length < HET_5A_DATA_LEN) {
            throw CreateException(ERROR_PACKET_INVALLIDATE, "packet5A'size less than packet5A's size,actual:%d",length);
        }
        Logc("get data.length:%d!\n",length);
		//校验数据包头合法性
        bool isvalidate = validateHeader(m_packet->data);
        if (!isvalidate) {
            throw CreateException(ERROR_INVALIDATE_HEADER, "invalidate header5A error ,header is:%X", m_packet->data[0]);
        }
        //解析数据包头
        parseHeader(m_packet->data);
        if (m_packet->dataLen < (m_packet->packetmodel_5a.dataLen + HET_5A_DATA_LEN)) {
            throw CreateException(ERROR_PACKET_INVALLIDATE, "packet5A's size(%d) less than packet's body size(%d).",m_packet->dataLen,(m_packet->packetmodel_5a.dataLen + HET_5A_DATA_LEN));
        }

        /**得到包体*/
        if (m_packet->packetmodel_5a.dataLen > 0) {
            m_packet->packetmodel_5a.frameBody = calcBody(m_packet->data, length);
        }
        bool iscrcvalidate = parseTail(m_packet->data);
        if (!iscrcvalidate) {
            throw CreateException(ERROR_CRC_ERROR, "packet5A's crc is error");

        }
        return toPacketModel();
    } else {
        Logc("协议包头错误!\n");
        throw CreateException(ERROR_INVALIDATE_HEADER, "packet5Ain is error");
    }
}