//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: AbstractPacketFactory.java
 * Create: 2016/3/10 11:39
 * Author: uuxia
 */
#ifndef WORK_ABSTRACTPACKETFACTORY_H
#define WORK_ABSTRACTPACKETFACTORY_H
#include "AbstractPacketIn.h"
#include "AbstractPacketOut.h"

class AbstractPacketFactory
{
public:
    virtual AbstractPacketIn* createIn() = 0;
    virtual AbstractPacketOut* createOut() = 0;
};
#endif //WORK_ABSTRACTPACKETFACTORY_H
