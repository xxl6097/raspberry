//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: PacketOut.java
 * Create: 2016/3/10 12:12
 * Author: uuxia
 */
#ifndef WORK_PACKETOUT_H
#define WORK_PACKETOUT_H
#include "AbstractPacketOut.h"
class PacketOut:public AbstractPacketOut
{
public:
    virtual void putHead(unsigned char* buf) = 0;
    virtual void putBody(unsigned char* buf) = 0;
    virtual void putCRC(unsigned char* buf) = 0;
    virtual void fill(unsigned char* buf) = 0;

    virtual char* packetOut() throw(Exception) = 0;
};
#endif //WORK_PACKETOUT_H
