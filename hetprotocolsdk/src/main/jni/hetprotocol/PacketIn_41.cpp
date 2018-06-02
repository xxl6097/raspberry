//
//  PacketIn_41.cpp
//  hetprotocol
//
//  Created by uuxia on 16/3/10.
//  Copyright © 2016年 uuxia. All rights reserved.
//

#include "PacketIn_41.h"
PacketIn_41::PacketIn_41(PacketModel* nPacketModel)
{
	Logc("call PacketIn_41::PacketIn_41\n");
    this->m_packet = nPacketModel;
	this->m_packet->packetmodel_41.packetStart = 0xF2;
    this->m_packet->packetmodel_41.protocolVersion = 0x41;
	this->m_packet->packetmodel_41.protocolType = 0x02;
}

PacketIn_41::~PacketIn_41()
{
	Logc("call PacketIn_41::~PacketIn_41\n");
	if (m_packet)
	{
		if (m_packet->packetmodel_41.frameBody)
		{
			delete m_packet->packetmodel_41.frameBody;
			m_packet->packetmodel_41.frameBody = NULL;
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

bool PacketIn_41::validateHeader(unsigned char *buf)
{
	Logc("call PacketIn_41::validateHeader\n");
	if (buf && this->m_packet->packetstart == buf[0] && m_packet->dataLen >= HET_41_DATA_LEN) {
		return true;
	} else {
		Logc("parse packetIn_41 error. buf is:%X  size:%d\n",buf[0],m_packet->packetmodel_41.dataLen);
		return false;
	}
}

unsigned char* PacketIn_41::calcBody(unsigned char* buf, int length)
{
	Logc("call PacketIn_41::calcBody\n");
	int bodyLen = length - HET_41_DATA_LEN;
	if (bodyLen == this->m_packet->packetmodel_41.dataLen)
	{
		this->m_packet->packetmodel_41.frameBody = new unsigned char[bodyLen];
		if (bodyLen > 0)
		{
			memset(this->m_packet->packetmodel_41.frameBody,0,bodyLen);
			memcpy(this->m_packet->packetmodel_41.frameBody,buf + 16,bodyLen);
		}
	}
	m_packet->bodyLen = bodyLen;
	return m_packet->packetmodel_41.frameBody;
}

void PacketIn_41::parseHeader(unsigned char* buf)
{
	Logc("call PacketIn_41::parseHeader\n");
	if (buf)
	{
		//获取协议版本
		memcpy(&m_packet->packetmodel_41.protocolVersion,buf + 1,1);
		//获取协议类型
		memcpy(&m_packet->packetmodel_41.protocolType,buf + 2,1);
		//获取命令字
        memcpy(&m_packet->packetmodel_41.commandType,buf + 3,2);
		convertToLittleEndianFor16Bit(&m_packet->packetmodel_41.commandType);
		//获取Mac地址
		memcpy(m_packet->packetmodel_41.macAddr,buf + 5,6);
		//获取设备大小分类
		memcpy(m_packet->packetmodel_41.deviceType,buf + 11,2);
		//获取保留字
		memcpy(&m_packet->packetmodel_41.reserved,buf + 13,1);
		//获取数据包体长度
		memcpy(&m_packet->packetmodel_41.dataLen,buf + 14,2);
		convertToLittleEndianFor16Bit(&m_packet->packetmodel_41.dataLen);
        m_packet->bodyLen = m_packet->packetmodel_41.dataLen;
	}
}

bool PacketIn_41::parseTail(unsigned char* buf)
{
	Logc("call PacketIn_41::parseTail\n");
    int crclen = sizeof(this->m_packet->packetmodel_41.fcs)/sizeof(this->m_packet->packetmodel_41.fcs[0]);
    memset(this->m_packet->packetmodel_41.fcs,0,crclen);
    memcpy(m_packet->packetmodel_41.fcs,buf + 16 + m_packet->packetmodel_41.dataLen,crclen);

    int len = HET_41_DATA_LEN + m_packet->bodyLen - 3;
    unsigned char fcs[2];
    CRC16x25Calc(fcs,buf + 1, len);
    if (fcs[0] != m_packet->packetmodel_41.fcs[0] || fcs[1] != m_packet->packetmodel_41.fcs[1]) {
        return false;
    }
    return true;
}

PacketModel* PacketIn_41::toPacketModel()
{
	Logc("call PacketIn_5A::toPacketModel\n");
	return m_packet;
}

PacketModel* PacketIn_41::packetIn() throw(Exception)
{
	Logc("call PacketIn_41::packetIn\n");
	if (m_packet && m_packet->data) {
		//获取数据包总长度
		int length = m_packet->dataLen;
        if (length < HET_41_DATA_LEN) {
            throw CreateException(ERROR_PACKET_INVALLIDATE, "packet'size less than packet41's size,actual:%d",length);
        }
		Logc("get data.length:%d!\n",length);
		//校验数据包头合法性
        bool isvalidate = validateHeader(m_packet->data);
        if (!isvalidate) {
            throw CreateException(ERROR_INVALIDATE_HEADER, "invalidate header41 error ,header is:%X", m_packet->data[0]);
        }
		//解析数据包头
		parseHeader(m_packet->data);
        if (m_packet->dataLen < (m_packet->packetmodel_41.dataLen + HET_41_DATA_LEN)) {
            throw CreateException(ERROR_PACKET_INVALLIDATE, "packet41's size(%d) less than packet's body size(%d)",m_packet->dataLen,(m_packet->packetmodel_41.dataLen + HET_41_DATA_LEN));
        }
		/**得到包体*/
		if (m_packet->packetmodel_41.dataLen > 0) {
			m_packet->packetmodel_41.frameBody = calcBody(m_packet->data, length);
		}
        bool iscrcvalidate = parseTail(m_packet->data);
        if (!iscrcvalidate) {
            throw CreateException(ERROR_CRC_ERROR, "packet41's crc is error");

        }
		return toPacketModel();
	} else {
		Logc("协议包头错误!\n");
		throw CreateException(ERROR_INVALIDATE_HEADER, "packet41in is error");
	}
}