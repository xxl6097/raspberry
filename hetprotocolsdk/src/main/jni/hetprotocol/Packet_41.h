//
//  Packet_41.
//  hetprotocol
//
//  Created by uuxia on 16/3/10.
//  Copyright © 2016年 uuxia. All rights reserved.
//

#ifndef Packet_41_h
#define Packet_41_h
#include "AbstractPacketFactory.h"
#include "PacketIn_41.h"
#include "PacketOut_41.h"
class Packet_41:public AbstractPacketFactory
{
public:
    PacketModel* m_packet;
    AbstractPacketIn* createIn();
    AbstractPacketOut* createOut();
public:
    Packet_41(PacketModel* nPacketModel);
    virtual ~Packet_41(void);
};

#endif /* Packet_41_h */
