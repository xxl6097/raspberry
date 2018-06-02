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
#include "PacketVersionManager.h"

PacketVersionManager::PacketVersionManager(){}
PacketVersionManager::~PacketVersionManager(){}

AbstractPacketFactory* PacketVersionManager::createVersion(PacketModel *pModel) throw(Exception)
{
    Logc("call PacketVersionManager::createVersion packetstart:%X\n",pModel->packetstart);
    if (pModel->packetstart == HET_5A_PROTOCOL) {
        return new Packet_5A(pModel);
    }else {
        if (pModel->protocolversion == HET_F2_PROTOCOL_41) {
            return new Packet_41(pModel);
        }else if(pModel->protocolversion == HET_F2_PROTOCOL_42){
            return new Packet_42(pModel);
        }else{
            throw CreateException(ERROR_INVALIDATE_HEADER, "invalidate packet,header is:%X,protocolVeriosn:%X",pModel->packetstart,pModel->protocolversion);
        }
	}
    throw CreateException(ERROR_INVALIDATE_HEADER, "invalidate packet header,header is:%X",pModel->packetstart);
}