//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: PacketOut_5A.java
 * Create: 2016/3/10 12:13
 * Author: uuxia
 */
#include "PacketOut_5A.h"

PacketOut_5A::PacketOut_5A(PacketModel* nPacketModel)
{
    this->m_packet = nPacketModel;
	int dataLength = HET_5A_DATA_LEN + this->m_packet->bodyLen;
    this->m_packet->data = (unsigned char*)new char[dataLength];
	memset(this->m_packet->data,0,dataLength);
    this->m_packet->packetmodel_5a.packetStart = 0x5A;
    this->m_packet->protocolversion = 0x40;
	this->m_packet->packetmodel_5a.protocolVersion = 0x40;
	this->m_packet->packetmodel_5a.protocolType = 0x01;
}

PacketOut_5A::~PacketOut_5A()
{
	Logc("call PacketOut_5A::~PacketOut_5A()");
	if (m_packet)
	{
		if (m_packet->data)
		{
			delete m_packet->data;
			m_packet->data = NULL;
		}
		if (m_packet->packetmodel_5a.frameBody)
		{
			delete m_packet->packetmodel_5a.frameBody;
			m_packet->packetmodel_5a.frameBody = NULL;
		}
		delete m_packet;
		m_packet = NULL;
	}
}

void PacketOut_5A::putHead(unsigned char *buf)
{
    buf[0] = m_packet->packetmodel_5a.packetStart;

    unsigned short bodylen = (unsigned short) (m_packet->bodyLen + HET_5A_DATA_LEN - 1);
    convertToLittleEndianFor16Bit(&bodylen);
    memcpy(&buf[1], &bodylen, 2);

    buf[3] = m_packet->packetmodel_5a.protocolVersion;
    buf[4] = m_packet->packetmodel_5a.protocolType;
    memcpy(&buf[5], &m_packet->packetmodel_5a.deviceId, 8);
    memcpy(&buf[13], &m_packet->packetmodel_5a.macAddr, 6);
	unsigned int frameNo = m_packet->packetmodel_5a.frameSN;
	convertToLittleEndianFor32Bit(&frameNo);
    memcpy(&buf[19], &frameNo, 4);
    memcpy(&buf[23], &m_packet->packetmodel_5a.reserved, 8);

	unsigned short cmd = m_packet->packetmodel_5a.commandType;
    convertToLittleEndianFor16Bit(&cmd);
    memcpy(&buf[31], &cmd, 2);
}

void PacketOut_5A::putBody(unsigned char *buf)
{
    if (m_packet->bodyLen > 0 && m_packet->packetmodel_5a.frameBody) {
        memcpy(&buf[33], m_packet->packetmodel_5a.frameBody, m_packet->bodyLen);
    }
}

void PacketOut_5A::putCRC(unsigned char *buf)
{
    int len = HET_5A_DATA_LEN + m_packet->bodyLen - 3;
	unsigned char fcs[2];
	CRC16x25Calc(fcs,buf + 1, len);
	buf[33 + m_packet->bodyLen] = fcs[0];
	buf[33 + m_packet->bodyLen + 1] = fcs[1];
}

void PacketOut_5A::fill(unsigned char *buf)
{
    putHead(buf);
    putBody(buf);
    putCRC(buf);
}

char* PacketOut_5A::packetOut() throw(Exception)
{
    if (m_packet) {
        fill(m_packet->data);
		m_packet->dataLen = m_packet->bodyLen + HET_5A_DATA_LEN;
        return (char*)m_packet->data;
    }
    throw CreateException(ERROR_INVALIDATE_HEADER, "packet5AOut is error");
}