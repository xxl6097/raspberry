//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: PacketOut_5A.java
 * Create: 2016/3/10 12:13
 * Author: uuxia
 */
#ifndef WORK_PACKETOUT_5A_H
#define WORK_PACKETOUT_5A_H
#include "PacketOut.h"
class PacketOut_5A:public PacketOut
{
public:
    PacketModel* m_packet;
public:
    PacketOut_5A(PacketModel* nPacketModel);
    ~PacketOut_5A(void);

public:
    void putHead(unsigned char* buf);
    void putBody(unsigned char* buf);
    void putCRC(unsigned char* buf);
    void fill(unsigned char* buf);

    char* packetOut() throw(Exception);
};


#endif //WORK_PACKETOUT_5A_H
