//
//  Packet_41.cpp
//  hetprotocol
//
//  Created by uuxia on 16/3/10.
//  Copyright © 2016年 uuxia. All rights reserved.
//

#include "Packet_41.h"
Packet_41::Packet_41(PacketModel* nPacketModel)
{
    Logc("call Packet_41::Packet_41\n");
    this->m_packet = nPacketModel;
}

Packet_41::~Packet_41()
{
    Logc("call ~Packet_41()\n");
    if (m_packet)
    {
        if (m_packet->data)
        {
            delete m_packet->data;
            m_packet->data = NULL;
        }
        if (m_packet->packetmodel_41.frameBody)
        {
            delete m_packet->packetmodel_41.frameBody;
            m_packet->packetmodel_41.frameBody = NULL;
        }
        delete m_packet;
        m_packet = NULL;
    }
}

AbstractPacketIn* Packet_41::createIn()
{
    Logc("call Packet_41::createIn\n");
    return new PacketIn_41(this->m_packet);
}

AbstractPacketOut* Packet_41::createOut()
{
    Logc("call Packet_41::createOut\n");
    return new PacketOut_41(this->m_packet);
}