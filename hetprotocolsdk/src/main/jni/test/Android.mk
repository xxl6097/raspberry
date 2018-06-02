LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE    := uuxia
LOCAL_SRC_FILES := main.cpp
LOCAL_C_INCLUDES += \
	$(LOCAL_PATH) \
	$(LOCAL_PATH)/../hetprotocol
LOCAL_STATIC_LIBRARIES := hetprotocol
LOCAL_LDLIBS := -lm -llog
# 若要使用main函数测试程序，则开启下面的代码
#include $(BUILD_EXECUTABLE)

#使用步骤如下：
#1.开启本文件低11行代码；
#2.修改hetprotocol/Android.mk文件，让生成.a那行代码开启(26行代码)，且关闭生成so那行代码(24行代码)；
#3.在jni目录先执行ndk-build生成可执行文件
