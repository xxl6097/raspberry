//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: AbstractPacketOut.java
 * Create: 2016/3/10 11:44
 * Author: uuxia
 */
#ifndef WORK_ABSTRACTPACKETOUT_H
#define WORK_ABSTRACTPACKETOUT_H
#include "PacketModel.h"
class AbstractPacketOut
{
public:
    virtual char* packetOut() throw(Exception) = 0;

};
#endif //WORK_ABSTRACTPACKETOUT_H
