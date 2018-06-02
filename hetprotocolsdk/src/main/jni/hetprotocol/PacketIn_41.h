//
//  PacketIn_41.h
//  hetprotocol
//
//  Created by uuxia on 16/3/10.
//  Copyright © 2016年 uuxia. All rights reserved.
//

#ifndef PacketIn_41_h
#define PacketIn_41_h

#include "PacketIn.h"


class PacketIn_41:public PacketIn
{
public:
    PacketModel* m_packet;
public:
    PacketIn_41(PacketModel* nPacketModel);
    ~PacketIn_41(void);
public:
    bool validateHeader(unsigned char* buf);
    unsigned char* calcBody(unsigned char* buf, int length);
    void parseHeader(unsigned char* buf);
    bool parseTail(unsigned char* buf);
    PacketModel* toPacketModel();

    PacketModel* packetIn() throw(Exception);
};

#endif /* PacketIn_41_h */
