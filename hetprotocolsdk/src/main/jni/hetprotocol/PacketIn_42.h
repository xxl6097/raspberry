//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: PacketIn_42.java
 * Create: 2016/3/11 11:22
 * Author: uuxia
 */
#ifndef WORK_PACKETIN_42_H
#define WORK_PACKETIN_42_H

#include "PacketIn.h"
class PacketIn_42 :public PacketIn{
public:
	PacketModel* m_packet;
public:
	PacketIn_42(PacketModel* nPacketModel);
	~PacketIn_42(void);
public:
	bool validateHeader(unsigned char* buf);
	unsigned char* calcBody(unsigned char* buf, int length);
	void parseHeader(unsigned char* buf);
	bool parseTail(unsigned char* buf);
	PacketModel* toPacketModel();

	PacketModel* packetIn() throw(Exception);
};


#endif //WORK_PACKETIN_42_H
