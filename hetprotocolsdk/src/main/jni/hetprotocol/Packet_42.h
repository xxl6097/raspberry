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
#ifndef WORK_PACKET_42_H
#define WORK_PACKET_42_H

#include "AbstractPacketFactory.h"
#include "PacketIn_42.h"
#include "PacketOut_42.h"
class Packet_42:public AbstractPacketFactory{
public:
	PacketModel* m_packet;
	AbstractPacketIn* createIn();
	AbstractPacketOut* createOut();
public:
	Packet_42(PacketModel* nPacketModel);
	virtual ~Packet_42(void);
};


#endif //WORK_PACKET_42_H
