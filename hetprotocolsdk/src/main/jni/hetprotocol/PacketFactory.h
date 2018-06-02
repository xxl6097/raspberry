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
#ifndef WORK_PACKETFACTORY_H
#define WORK_PACKETFACTORY_H
#include "AbstractPacketFactory.h"
#include "PacketVersionManager.h"

class PacketFactory:public AbstractPacketFactory{
public:
    PacketFactory(PacketModel* nPacketModel) throw(Exception);
    virtual ~PacketFactory(void);
public:
    AbstractPacketFactory* m_absPacketFactory;
	PacketVersionManager* m_packetVersionManager;
	PacketModel* m_packet;
public:
    AbstractPacketIn* createIn();
    AbstractPacketOut* createOut();
};


#endif //WORK_PACKETFACTORY_H
