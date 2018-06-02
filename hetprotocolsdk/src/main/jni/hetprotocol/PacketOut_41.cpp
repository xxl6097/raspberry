//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016�?clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: PacketOut_41.c
 * Create: 2016/3/14 16:38
 * Author: uuxia
 */
#include "PacketOut_41.h"
PacketOut_41::PacketOut_41(PacketModel* nPacketModel)
{
    Logc("call PacketOut_41::PacketOut_41\n");
    this->m_packet = nPacketModel;
    int dataLength = HET_41_DATA_LEN + this->m_packet->bodyLen;
    this->m_packet->data = (unsigned char*)new char[dataLength];
    memset(this->m_packet->data,0,dataLength);
    this->m_packet->packetmodel_41.packetStart = 0xF2;
    this->m_packet->packetmodel_41.protocolVersion = 0x41;
    this->m_packet->packetmodel_41.protocolType = 0x02;
}

PacketOut_41::~PacketOut_41()
{
    Logc("call PacketOut_41::~PacketOut_41\n");
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

void PacketOut_41::putHead(unsigned char* buf)
{
    Logc("call PacketOut_41::putHead\n");
    buf[0] = m_packet->packetmodel_41.packetStart;//填充协议首字�?
    buf[1] = m_packet->packetmodel_41.protocolVersion;//填充协议版本�?
    buf[2] = m_packet->packetmodel_41.protocolType;//填充协议类型
    unsigned short cmd = m_packet->packetmodel_41.commandType;
    convertToLittleEndianFor16Bit(&cmd);//先做大小端转�?
    memcpy(&buf[3], &cmd, 2);//。然后填充命令字
    memcpy(&buf[5], &m_packet->packetmodel_41.macAddr, 6);//填充设备Mac地址
    memcpy(&buf[11], &m_packet->packetmodel_41.deviceType, 2);//填充设备大小分类
    buf[13] = m_packet->packetmodel_41.reserved;//填充保留�?
    unsigned short nBodyLen = m_packet->bodyLen;
    convertToLittleEndianFor16Bit(&nBodyLen);//先做大小端转换�?
    memcpy(&buf[14], &nBodyLen, 2);//填充数据包体长度
}

void PacketOut_41::putBody(unsigned char* buf)
{
    Logc("call PacketOut_41::putBody\n");
    if (m_packet->bodyLen > 0 && m_packet->packetmodel_41.frameBody) {
        memcpy(&buf[16], m_packet->packetmodel_41.frameBody, m_packet->bodyLen);
    }
}

void PacketOut_41::putCRC(unsigned char* buf)
{
    int len = HET_41_DATA_LEN + m_packet->bodyLen - 3;
    unsigned char fcs[2];
	CRC16x25Calc(fcs,buf + 1, len);
    buf[16 + m_packet->bodyLen] = fcs[0];
    buf[16 + m_packet->bodyLen + 1] = fcs[1];
}
void PacketOut_41::fill(unsigned char* buf)
{
    Logc("call PacketOut_41::fill\n");
    putHead(buf);
    putBody(buf);
    putCRC(buf);
}

char* PacketOut_41::packetOut() throw(Exception)
{
    Logc("call PacketOut_41::packetOut\n");
    if (m_packet) {
        //封装数据
        fill(m_packet->data);
        //封装协议包，填充完毕的时候，标记完整数据包长�?
        m_packet->dataLen = m_packet->bodyLen + HET_41_DATA_LEN;
        return (char*)m_packet->data;
    }
    throw CreateException(ERROR_INVALIDATE_HEADER, "packet41Out is error");
}