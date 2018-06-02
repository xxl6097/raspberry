//

//

/*
 * -----------------------------------------------------------------
 * Copyright © 2016年 clife. All rights reserved.
 * Shenzhen H&T Intelligent Control Co.,Ltd.
 * -----------------------------------------------------------------
 * File: main.java
 * Create: 2016/3/10 12:09
 * Author: uuxia
 */
#include "MainTestDataClass.h"
#include "Utils.h"

int main(int argc, const char * argv[]) {
    //unsigned char nmac[] = {0xd8,0x90,0xee,0xff,0x67,0x78};
    //GetMacAddress(nmac);

    try {
        testPacket5AIn();
        //testPacket5AOut();
    } catch (Exception e) {
        Logc("\n \"carsh exception code:%d,msg:%s\" \n\n",e.errorcode,e.errormsg);
    }

	return 0;
}

