//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: Packet_5A.java
 * Create: 2016/3/10 12:09
 * Author: uuxia
 */
#include "Packet_5A.h"
Packet_5A::Packet_5A(PacketModel* nPacketModel)
{
    Logc("call Packet_5A::Packet_5A\n");
    this->m_packet = nPacketModel;
}

Packet_5A::~Packet_5A()
{
	Logc("call ~Packet_5A()\n");
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

AbstractPacketIn* Packet_5A::createIn()
{
    Logc("call Packet_5A::createIn\n");
    return new PacketIn_5A(this->m_packet);
}

AbstractPacketOut* Packet_5A::createOut()
{
    Logc("call Packet_5A::createOut\n");
    return new PacketOut_5A(this->m_packet);//new PacketOut_5A(this->packet);
}