//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: PacketVersionManager.java
 * Create: 2016/3/10 12:13
 * Author: uuxia
 */
#ifndef WORK_PACKETVERSIONMANAGER_H
#define WORK_PACKETVERSIONMANAGER_H
#include "AbstractPacketFactory.h"
#include "Packet_5A.h"
#include "Packet_41.h"
#include "Packet_42.h"

class PacketVersionManager
{
public:
    PacketVersionManager(void);
    virtual ~PacketVersionManager(void);
public:
    AbstractPacketFactory* createVersion(PacketModel* pModel) throw(Exception);

};


#endif //WORK_PACKETVERSIONMANAGER_H
