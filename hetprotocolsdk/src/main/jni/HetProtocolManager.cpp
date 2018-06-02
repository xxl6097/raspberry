#include "com_het_protocol_CProtocolManager.h"
#include "HetUtils.h"

#include "Android_log_print.h"
/*
 * Class:     com_het_protocol_CProtocolManager
 * Method:    parseData
 * Signature: ([B)Lcom/het/udp/wifi//model/PacketModel;
 */
jobject JNICALL Java_com_het_protocol_CProtocolManager_parseData
        (JNIEnv *env, jobject obj, jobject bytearr)
{
        jobject jObj = NULL;
        try {
            jObj = parseData(env,obj,bytearr);
        }catch (Exception e){
            Logc("parseData carsh exception code:%d,msg:%s",e.errorcode,e.errormsg);
            IOException(env,e.errormsg);
        }
    if(env->ExceptionOccurred()!=NULL)
    {
        Logc("wahhahha=========++++++++++++++++++++ ");
    }
        Logc("Java_com_het_hetprotocol_ProtocolManager_parseData  call after ");
        return jObj;
}

/*
 * Class:     com_het_protocol_CProtocolManager
 * Method:    packageData
 * Signature: (Lcom/het/udp/wifi//model/PacketModel;)[B
 */
jbyteArray JNICALL Java_com_het_protocol_CProtocolManager_packageData
        (JNIEnv *env, jobject obj, jobject objModel)
{
        jbyteArray data = NULL;
        try
          {
            data = packageData(env,obj,objModel);
          }
         catch(Exception e)
           {
             Logc("packageData carsh exception code:%d,msg:%s",e.errorcode,e.errormsg);
             IOException(env,e.errormsg);
           }
        return data;
}