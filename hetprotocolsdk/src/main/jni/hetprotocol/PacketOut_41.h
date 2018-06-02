//
//  PacketOut_41.h
//  hetprotocol
//
//  Created by uuxia on 16/3/10.
//  Copyright © 2016年 uuxia. All rights reserved.
//

#ifndef PacketOut_41_h
#define PacketOut_41_h
#include "PacketOut.h"

class PacketOut_41:public PacketOut
{
public:
    PacketModel* m_packet;
public:
    PacketOut_41(PacketModel* nPacketModel);
    ~PacketOut_41(void);

public:
    void putHead(unsigned char* buf);
    void putBody(unsigned char* buf);
    void putCRC(unsigned char* buf);
    void fill(unsigned char* buf);

    char* packetOut() throw(Exception);
};

#endif /* PacketOut_41_h */
