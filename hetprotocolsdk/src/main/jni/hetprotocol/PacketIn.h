//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: PacketIn.java
 * Create: 2016/3/10 12:12
 * Author: uuxia
 */
#ifndef WORK_PACKETIN_H
#define WORK_PACKETIN_H
#include "AbstractPacketIn.h"
class PacketIn:public AbstractPacketIn
{
public:
    virtual bool validateHeader(unsigned char* buf)=0;
    virtual unsigned char* calcBody(unsigned char* buf, int length)=0;
    virtual void parseHeader(unsigned char* buf)=0;
    virtual bool parseTail(unsigned char* buf)=0;
    virtual PacketModel* toPacketModel()=0;

    virtual PacketModel* packetIn() throw(Exception) = 0;
};
#endif //WORK_PACKETIN_H
