//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: PacketFactory.java
 * Create: 2016/3/10 12:10
 * Author: uuxia
 */
#include "PacketFactory.h"
PacketFactory::PacketFactory(PacketModel* nPacketModel)throw(Exception)
{
    Logc("call PacketFactory::PacketFactory()\n");
    this->m_packet = nPacketModel;
    m_packetVersionManager = new PacketVersionManager();
    m_absPacketFactory = m_packetVersionManager->createVersion(this->m_packet);
    Logc("call PacketFactory::PacketFactory\n");
}

PacketFactory::~PacketFactory()
{
	Logc("call PacketFactory::~PacketFactory()\n");
	if (m_absPacketFactory)
	{
		delete m_absPacketFactory;
		m_absPacketFactory = NULL;
	}

	if (m_packetVersionManager)
	{
		delete m_packetVersionManager;
		m_packetVersionManager = NULL;
	}
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

		//这个地方调用delete崩溃
		//delete m_packet;
		m_packet = NULL;
	}

}

AbstractPacketIn* PacketFactory::createIn()
{
    Logc("call PacketFactory::createIn\n");
    return m_absPacketFactory->createIn();
}

AbstractPacketOut* PacketFactory::createOut()
{
    Logc("call PacketFactory::createOut\n");
    return m_absPacketFactory->createOut();
}