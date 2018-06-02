//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: PacketUtils.h
 * Create: 2016/3/15 12:43
 * Author: uuxia
 */
#ifndef WORK_PACKETUTILS_H
#define WORK_PACKETUTILS_H

#include "PacketModel.h"
#include "PacketFactory.h"

static PacketModel* in(DataModel* dataModel) throw(Exception)
{
    PacketModel* packet = new PacketModel();
    packet->data = dataModel->data;
    packet->dataLen = dataModel->size;
    packet->packetstart = dataModel->data[0];
    packet->protocolversion = dataModel->data[1];

    PacketFactory* p = new PacketFactory(packet);
    AbstractPacketIn* absFac = p->createIn();
    absFac->packetIn();
    return packet;
}

static DataModel* out(PacketModel* packet)throw(Exception)
{
    PacketFactory* p = new PacketFactory(packet);
    AbstractPacketOut * absFac = p->createOut();
    absFac->packetOut();
    unsigned char data[BUFFERSIZE];
    memset(data,0,BUFFERSIZE);
    memcpy(data,packet->data,packet->dataLen);
    int len = 0;
    /*for (int i=0;i<packet->dataLen;i++)
    {
        //Logc("0x%0.2X,",*(data+i)&0xff);
        len = i;
    }*/
    Logc("\npacket.size:%d\n",++len);
    if (p)
    {
        delete p;
        p = NULL;
    }
    DataModel* dataModel = new DataModel();
    dataModel->data = data;
    dataModel->size = packet->dataLen;
    return dataModel;
}

#endif //WORK_PACKETUTILS_H
