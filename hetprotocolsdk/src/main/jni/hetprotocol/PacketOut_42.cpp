//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016�?clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: PacketOut_42.c
 * Create: 2016/3/14 16:39
 * Author: uuxia
 */
#include "PacketOut_42.h"
PacketOut_42::PacketOut_42(PacketModel* nPacketModel)
{
    Logc("call PacketOut_42::PacketOut_42\n");
    this->m_packet = nPacketModel;
    int dataLength = HET_42_DATA_LEN + this->m_packet->bodyLen;
    this->m_packet->data = (unsigned char*)new char[dataLength];
    memset(this->m_packet->data,0,dataLength);
    this->m_packet->packetmodel_42.packetStart = 0xF2;
    this->m_packet->packetmodel_42.protocolVersion = 0x42;
    this->m_packet->packetmodel_42.protocolType = 0x02;
}

PacketOut_42::~PacketOut_42()
{
    Logc("call PacketOut_42::~PacketOut_42\n");
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

void PacketOut_42::putHead(unsigned char* buf)
{
    Logc("call PacketOut_42::putHead\n");
    buf[0] = m_packet->packetmodel_42.packetStart;//填充协议首字�?
    buf[1] = m_packet->packetmodel_42.protocolVersion;//填充协议版本�?
    buf[2] = m_packet->packetmodel_42.protocolType;//填充协议类型
    unsigned short cmd = m_packet->packetmodel_42.commandType;
    convertToLittleEndianFor16Bit(&cmd);//先做大小端转�?
    memcpy(&buf[3], &cmd, 2);//。然后填充命令字
    memcpy(&buf[5], &m_packet->packetmodel_42.macAddr, 6);//填充设备Mac地址
    memcpy(&buf[11], &m_packet->packetmodel_42.deviceType, 8);//填充设备大小分类
    buf[19] = m_packet->packetmodel_42.dataStatus;//填充数据状�?
    buf[20] = m_packet->packetmodel_42.wifiStatus;//填充WiFi状�?
    unsigned int num = m_packet->packetmodel_42.frameSN;
    convertToLittleEndianFor32Bit(&num);
    memcpy(&buf[21], &num, 4);//填充帧序�?
    memcpy(&buf[25], &m_packet->packetmodel_42.reserved, 8);//填充保留�?
    unsigned short nBodyLen = m_packet->bodyLen;
    convertToLittleEndianFor16Bit(&nBodyLen);//先做大小端转换�?
    memcpy(&buf[33], &nBodyLen, 2);//填充数据包体长度
}

void PacketOut_42::putBody(unsigned char* buf)
{
    Logc("call PacketOut_42::putBody\n");
    if (m_packet->bodyLen > 0 && m_packet->packetmodel_42.frameBody) {
        memcpy(&buf[35], m_packet->packetmodel_42.frameBody, m_packet->bodyLen);
    }
}

void PacketOut_42::putCRC(unsigned char* buf)
{
    Logc("PacketOut_42::putCRC\n");
    if (m_packet->bodyLen > 0 && m_packet->packetmodel_42.frameBody) {
        unsigned char bodyCrc[2];
		CRC16x25Calc(bodyCrc,m_packet->packetmodel_42.frameBody, m_packet->bodyLen);
        buf[35 + m_packet->bodyLen] = bodyCrc[0];
        buf[35 + m_packet->bodyLen + 1] = bodyCrc[1];
    }



    int len = HET_42_DATA_LEN + m_packet->bodyLen - 3;
    unsigned char fcs[2];
	CRC16x25Calc(fcs,buf + 1, len);
    buf[37 + m_packet->bodyLen] = fcs[0];
    buf[37 + m_packet->bodyLen + 1] = fcs[1];



}
void PacketOut_42::fill(unsigned char* buf)
{
    Logc("PacketOut_42::fill\n");
    putHead(buf);
    putBody(buf);
    putCRC(buf);
}

char* PacketOut_42::packetOut() throw(Exception)
{
    Logc("call PacketOut_42::packetOut\n");
    if (m_packet) {
        //封装数据
        fill(m_packet->data);
        //封装协议包，填充完毕的时候，标记完整数据包长�?
        m_packet->dataLen = m_packet->bodyLen + HET_42_DATA_LEN;
        return (char*)m_packet->data;
    }
    throw CreateException(ERROR_INVALIDATE_HEADER, "packet42Out is error");
}