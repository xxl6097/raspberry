//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: Utils.java
 * Create: 2016/3/10 11:43
 * Author: uuxia
 */
#ifndef WORK_UTILS_H
#define WORK_UTILS_H
#include "PacketModel.h"
//如果是非Android使用此程序，则注释掉此头文件引用
//#include "../Android_log_print.h"
#ifndef IS_DEBUG
#define Logc(format,...) printf(format,##__VA_ARGS__)
#endif

#define null NULL

//全局buffer大小
#define BUFFERSIZE 65535
//处理2字节大小端转换
static void convertToLittleEndianFor16Bit(unsigned short *data)
{
    unsigned short s1 = (((unsigned short)(*data) &(unsigned short)0x00ff) << 8);
    unsigned short s2 = (((unsigned short)(*data) &(unsigned short)0xff00) >> 8);

    //unsigned short s1 = (((short)(*data) << 8) & (short)0x00ff);
    //unsigned short s2 = (((short)(*data) >> 8) & (short)0xff00);
    *data = (short)(s1 | s2);
//    *data = ((short)(\
//                     (((short)(*data) &(short)0x00ff) << 8) |\
//                     (((short)(*data) &(short)0xff00) >> 8)));
}

//处理4字节大小端转换

static void convertToLittleEndianFor32Bit(unsigned int *data)
{
    unsigned int d1 = (((unsigned int)(*data) & (unsigned int)0xff000000) >> 24);
    unsigned int d2 = (((unsigned int)(*data) & (unsigned int)0x00ff0000) >> 8);
    unsigned int d3 = (((unsigned int)(*data) & (unsigned int)0x0000ff00) << 8 );
    unsigned int d4 = (((unsigned int)(*data) & (unsigned int)0x000000ff) << 24);
    *data= (unsigned int)(d1 | d2 | d3 | d4);
}


/*/处理8字节大小端转换
 static void convertToLittleEndianForLong(unsigned long *data)
 {
	unsigned int d1 = (((long)(data) & 0xff00000000000000) >> 56);
	unsigned int d2 = (((long)(data) & 0x00ff000000000000) >> 40);
	unsigned int d3 = (((long)(data) & 0x0000ff0000000000) >> 24);
	unsigned int d4 = (((long)(data) & 0x000000ff00000000) >> 8);
	unsigned int d5 = (((long)(data) & 0x00000000ff000000) << 8);
	unsigned int d6 = (((long)(data) & 0x0000000000ff0000) << 24);
	unsigned int d7 = (((long)(data) & 0x000000000000ff00) << 40);
	unsigned int d8 = (((long)(data) & 0x00000000000000ff) << 56);
	*data= (unsigned int)(d1 | d2 | d3 | d4 | d5 | d6 | d7 | d8);
 }*/

//处理8字节大小端转换
static void convertToLittleEndianFor64Bit(unsigned char* data)
{
    unsigned char d0 = data[0];
    unsigned char d1 = data[1];
    unsigned char d2 = data[2];
    unsigned char d3 = data[3];
    unsigned char d4 = data[4];
    unsigned char d5 = data[5];
    unsigned char d6 = data[6];
    unsigned char d7 = data[7];
    data[0] = d7;
    data[1] = d6;
    data[2] = d5;
    data[3] = d4;
    data[4] = d3;
    data[5] = d2;
    data[6] = d1;
    data[7] = d0;
}

/**
* CRC16/X25校验
*
* @param data
* @param length
* @return
**/
static void CRC16x25Calc(unsigned char* fcs,unsigned char* data,int length)
{
	int j = 0;
	int crc16 = 0x0000FFFF;
	for (int i = 0; i < length; i++) {
		crc16 ^= data[i] & 0x000000FF;
		for (j = 0; j < 8; j++) {
			int flags = crc16 & 0x00000001;
			if (flags != 0) {
				crc16 = (crc16 >> 1) ^ 0x8408;
			} else {
				crc16 >>= 0x01;
			}
		}
	}
	int ret = ~crc16 & 0x0000FFFF;
	/*unsigned char crc[2];
	crc[1] = (ret & 0x000000FF);
	crc[0] = ((ret >> 8) & 0x000000FF);
	*/
	fcs[1] = (ret & 0x000000FF);
	fcs[0] = ((ret >> 8) & 0x000000FF);
//	return crc;
}

static char* GetMacAddress(unsigned char* nMacAddress)
{
    char* macAddress = new char[8];
    sprintf(macAddress, "%02X%02X%02X%02X%02X%02X",nMacAddress[0],nMacAddress[1],nMacAddress[2],nMacAddress[3],nMacAddress[4],nMacAddress[5]);
    Logc("macAddress:%s\n",macAddress);
    return macAddress;

}

static char HexChar(char c)//与接受消息无关
{
    if((c>='0')&&(c<='9'))
        return c-0x30;
    else if((c>='A')&&(c<='F'))
        return c-'A'+10;
    else if((c>='a')&&(c<='f'))
        return c-'a'+10;
    else
        return 0x10;
}


static void MacString2ByteArrays(const char* str, unsigned char* macBytes)
{
    int index = 0;
    memset(macBytes,0,6);
    while (*str != '\0')
    {
        if (index % 2 == 0)
        {
            char a1 = HexChar(*str);
            char a2 = HexChar(*(str+1));
            macBytes[index/2] = ((a1 << 4) | a2);
        }
        *(str++);
        index++;
    }
}
#endif //WORK_UTILS_H
