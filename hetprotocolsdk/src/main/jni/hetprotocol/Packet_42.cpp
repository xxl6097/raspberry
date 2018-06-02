//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: Packet_42.java
 * Create: 2016/3/11 11:17
 * Author: uuxia
 */
#include "Packet_42.h"
Packet_42::Packet_42(PacketModel* nPacketModel)
{
	Logc("call Packet_42::Packet_42\n");
	this->m_packet = nPacketModel;
}

Packet_42::~Packet_42()
{
	Logc("call ~Packet_42()\n");
	if (m_packet)
	{
		if (m_packet->data)
		{
			delete m_packet->data;
			m_packet->data = NULL;
		}
		if (m_packet->packetmodel_42.frameBody)
		{
			delete m_packet->packetmodel_42.frameBody;
			m_packet->packetmodel_42.frameBody = NULL;
		}
		delete m_packet;
		m_packet = NULL;
	}
}

AbstractPacketIn* Packet_42::createIn()
{
	Logc("call Packet_42::createIn\n");
	return new PacketIn_42(this->m_packet);
}

AbstractPacketOut* Packet_42::createOut()
{
	Logc("call Packet_42::createOut\n");
	return new PacketOut_42(this->m_packet);
}