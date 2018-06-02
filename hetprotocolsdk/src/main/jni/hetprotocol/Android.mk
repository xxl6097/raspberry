LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    := hetprotocol
LOCAL_SRC_FILES := \
Packet_5A.cpp \
Packet_41.cpp \
Packet_42.cpp \
PacketFactory.cpp \
PacketIn_5A.cpp \
PacketIn_41.cpp \
PacketIn_42.cpp \
PacketOut_5A.cpp \
PacketOut_41.cpp \
PacketOut_42.cpp \
PacketVersionManager.cpp \
../HetProtocolManager.cpp \


LOCAL_CPPFLAGS += -fexceptions
LOCAL_LDLIBS += -llog
#LOCAL_LDLIBS := -llog -landroid_runtime
#生成so
include $(BUILD_SHARED_LIBRARY)
#生成.a 供main函数测试
#include $(BUILD_STATIC_LIBRARY)

#编译so(BUILD_SHARED_LIBRARY)
#编译.a(PREBUILT_STATIC_LIBRARY)   BUILD_STATIC_LIBRARY







