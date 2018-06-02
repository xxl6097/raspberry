//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: AbstractPacketIn.java
 * Create: 2016/3/10 11:44
 * Author: uuxia
 */
#ifndef WORK_ABSTRACTPACKETIN_H
#define WORK_ABSTRACTPACKETIN_H
#include "PacketModel.h"
class AbstractPacketIn{
public:
    virtual PacketModel* packetIn() throw(Exception) = 0;
};
#endif //WORK_ABSTRACTPACKETIN_H
