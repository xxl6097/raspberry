package com.het.httpserver.core.impl;

import com.het.httpserver.bean.ApiResult;
import com.het.httpserver.bean.FileBrowserBean;
import com.het.httpserver.core.AbstractGetHttpFactory;
import com.het.httpserver.util.GsonUtil;
import com.het.httpserver.util.HttpConst;
import com.het.httpserver.util.Logc;
import com.het.httpserver.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBrowserImpl extends AbstractGetHttpFactory {
    final String SHOWFILE_PATH = "/v1/api/files";
    final String DELFILE_PATH = "/v1/api/delfiles";

    @Override
    protected String onMessageReceive(String path, Map<String, String> param) {
        Logc.d("=========================FileBrowserImpl :" + path + " " + (param == null ? "param is null" : param.toString()));
        String result = null;
        if (Util.isEmpty(path))
            return result;
        if (param == null)
            return result;
        if (path.startsWith(SHOWFILE_PATH)) {
            String callback = param.get("callback");
            String clientid = param.get("clientid");
            result = ";"+callback + "("+ packFiles(clientid)+");";
        }else if (path.startsWith(DELFILE_PATH)) {
            String callback = param.get("callback");
            String clientid = param.get("clientid");
            result = ";"+callback + "("+ delFiles(clientid)+");";
        }
        return result;
    }

    private String delFiles(String clientid){
        String filePath = HttpConst.FILE_PATH+clientid;
        String result = null;
        List<File> files = Util.getTxTFileList(filePath);
        StringBuffer sb = new StringBuffer();
        for (File file : files){
            boolean isSuc = file.delete();
            String msg = "["+file.getName()+"]删除"+(isSuc?"成功":"失败");
            Logc.i(msg);
            sb.append(msg);
            sb.append("\r\n");
        }
        ApiResult<String> apiResult = new ApiResult<>();
        apiResult.setCode(0);
        apiResult.setData(sb.toString());
        result = GsonUtil.getInstance().toJson(apiResult);
        System.out.println("delFiles："+result);
        return result;
    }

    private String packFiles(String clientid){
        String filePath = HttpConst.FILE_PATH+clientid;
        String result = null;
        List<File> files = Util.getTxTFileList(filePath);
        String baseUrl = "http://uuxia.cn:8123/java/file/"+clientid+"/";
        List<FileBrowserBean> fileBrowserBeanList = new ArrayList<>();
        for (File file:files){
            String fineName = file.getName();
            long fileSize = file.length();
            String fileUrl = baseUrl + fineName;
            fileBrowserBeanList.add(new FileBrowserBean(fineName,fileUrl,fileSize));
        }
        ApiResult<List<FileBrowserBean>> apiResult = new ApiResult<>();
        apiResult.setCode(0);
        apiResult.setData(fileBrowserBeanList);
        result = GsonUtil.getInstance().toJson(apiResult);
        System.out.println("FileBrowserImpl："+result);
        return result;
    }
}
