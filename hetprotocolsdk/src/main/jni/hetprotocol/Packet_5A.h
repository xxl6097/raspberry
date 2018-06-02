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
#ifndef WORK_PACKET_5A_H
#define WORK_PACKET_5A_H
#include "AbstractPacketFactory.h"
#include "PacketIn_5A.h"
#include "PacketOut_5A.h"
#include "PacketOut.h"

class Packet_5A:public AbstractPacketFactory
{
public:
    PacketModel* m_packet;
private:
    AbstractPacketIn* createIn();
    AbstractPacketOut* createOut();
public:
    Packet_5A(PacketModel* nPacketModel);
    virtual ~Packet_5A(void);
};


#endif //WORK_PACKET_5A_H
