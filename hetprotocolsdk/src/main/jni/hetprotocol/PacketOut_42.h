//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: PacketOut_42.java
 * Create: 2016/3/11 11:22
 * Author: uuxia
 */
#ifndef WORK_PACKETOUT_42_H
#define WORK_PACKETOUT_42_H
#include "PacketOut.h"

class PacketOut_42 :public PacketOut{
public:
	PacketModel* m_packet;
public:
	PacketOut_42(PacketModel* nPacketModel);
	~PacketOut_42(void);

public:
	void putHead(unsigned char* buf);
	void putBody(unsigned char* buf);
	void putCRC(unsigned char* buf);
	void fill(unsigned char* buf);

	char* packetOut() throw(Exception);
};


#endif //WORK_PACKETOUT_42_H
