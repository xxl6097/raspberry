//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: PacketIn_5A.java
 * Create: 2016/3/10 12:15
 * Author: uuxia
 */
#ifndef WORK_PACKETIN_5A_H
#define WORK_PACKETIN_5A_H

#include "PacketIn.h"
#include "Packet_5A.h"
class PacketIn_5A:public PacketIn{
public:
    PacketModel* m_packet;
public:
    PacketIn_5A(PacketModel* nPacketModel);
    ~PacketIn_5A(void);
public:
    bool validateHeader(unsigned char* buf);
    unsigned char* calcBody(unsigned char* buf, int length);
    void parseHeader(unsigned char* buf);
    bool parseTail(unsigned char* buf);
    PacketModel* toPacketModel();

    PacketModel* packetIn() throw(Exception);
};


#endif //WORK_PACKETIN_5A_H
