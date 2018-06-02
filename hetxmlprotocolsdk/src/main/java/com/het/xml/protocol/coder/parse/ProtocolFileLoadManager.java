package com.het.xml.protocol.coder.parse;

import android.content.Context;
import android.text.TextUtils;

import com.het.log.Logc;
import com.het.xml.protocol.ProtocolManager;
import com.het.xml.protocol.coder.bean.BaseDefinition;
import com.het.xml.protocol.coder.bean.BitDefinition;
import com.het.xml.protocol.coder.bean.ByteDefinition;
import com.het.xml.protocol.coder.bean.ProtocolDefinition;
import com.het.xml.protocol.coder.parse.inter.AnalyzeProtocalXml;
import com.het.xml.protocol.coder.parse.inter.ProtocolXMLFileLoad;
import com.het.xml.protocol.coder.utils.Base64Utils;
import com.het.xml.protocol.coder.utils.StringUtil;
import com.het.xml.protocol.db.DeviceProtocolDao;
import com.het.xml.protocol.db.DeviceProtocolModel;
import com.het.xml.protocol.model.ProtocolBean;
import com.het.xml.protocol.model.ProtocolDataModel;
import com.thoughtworks.xstream.XStreamException;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * 协议配置加载管理器
 *
 * @Original jake  @improver uuxia
 */
public class ProtocolFileLoadManager implements ProtocolXMLFileLoad<ProtocolDataModel> {
    /**
     * 保存所有协议
     */
    private final HashMap<String, ProtocolDefinition> protocolMapper = new HashMap<String, ProtocolDefinition>();

    private final HashMap<String, HashMap<String, BaseDefinition>> pro0104Mapper = new HashMap<String, HashMap<String, BaseDefinition>>();
    private AnalyzeProtocalXml parser;

    private String filePath;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private DeviceProtocolDao deviceProtocolDao;

    private Context mContext;

    public HashMap<String, ProtocolDefinition> getProtocolMapper() {
        return protocolMapper;
    }

    public AnalyzeProtocalXml getParser() {
        return parser;
    }


    public void setParser(AnalyzeProtocalXml parser) {
        this.parser = parser;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void loadXMLFiles() {
        load(filePath);
    }

    public void setContext(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        deviceProtocolDao = new DeviceProtocolDao(mContext);
    }

    @Override
    public void load(String filePathRegex) {
        if (filePathRegex == null || "".equals(filePathRegex.trim())) {
            throw new IllegalArgumentException("argument can't be null");
        }
        String filePathArr[] = filePathRegex.split(",");
        String regex = ".*\\.([x|X][m|M][l|L])$";
        File files[] = null;
        Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "start look up matched files...");
        List<File> fileList = new ArrayList<File>();
        for (String filePath : filePathArr) {
            filePath = filePath.trim();
            //如果匹配,则找出所有符合条件的文件
            if (filePath.matches(regex)) {
                int separatorIndex = filePath.lastIndexOf(File.separator);
                String dirPath = filePath.substring(0, separatorIndex);
                final String filterRegex = filePath.substring(separatorIndex + 1).toLowerCase().replace(".", "\\.").replace("*", ".*");
                try {
                    files = getFiles(dirPath, filterRegex);
                } catch (FileNotFoundException e) {
                    Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, e.getMessage());
                }
            } else { //如果是目录
                try {
                    files = getFiles(filePath, ".*\\.(xml)$");
                } catch (FileNotFoundException e) {
                    Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, e.getMessage());
                }
            }
            if (files != null) {
                for (File file : files) {
                    String fileName = StringUtil.getFileName(file.getName());
                    if (fileName != null) {
                        fileList.add(file);
                        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "Load from File :" + fileName);
                    } else {
                        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "Load in Sqlite :" + fileName);
                    }
                }
            }
        }
        loadProtocalDefinition(fileList);
    }

    @Override
    public void loadXmlString(ProtocolDataModel protocolDataModel) {//ProtocolBean
        if (protocolDataModel == null)
            return;
        List<ProtocolBean> list = protocolDataModel.getList();
        if (list != null && list.size() > 0) {
            for (ProtocolBean proBean : list) {
                if (proBean != null && !TextUtils.isEmpty(proBean.getContent())) {
                    String xmlString = proBean.getContent();
                    if (!TextUtils.isEmpty(proBean.getCommand())) {
                        if (proBean.getCommand().equalsIgnoreCase("8007")) {
                            proBean.setCommand("4007");
                        }
                    }
                    ProtocolDefinition definition = (ProtocolDefinition) parser.paseXML(xmlString);
                    StringBuffer sb = new StringBuffer();
                    /*sb.append(proBean.getProductVersion());
                    sb.append("-");
                    sb.append(proBean.getDeviceTypeId());
                    sb.append("-");
                    sb.append(proBean.getDeviceSubtypeId());
                    sb.append("-");
                    sb.append(proBean.getCommand());*/

                    sb.append(proBean.getProductId());
                    sb.append("-");
                    sb.append(proBean.getCommand());//add 20180507 by uuxia

                    definition.setId(sb.toString());
                    proBean.setProtocolName(sb.toString());
                    protocolMapper.put(definition.getId(), definition);
                    protocolDataModel.setProductId(proBean.getProductId());
                    if (!TextUtils.isEmpty(proBean.getCommand()) && ProtocolManager.getInstance().isAutoCalcUpdateFlag()) {
                        if (proBean.getCommand().equalsIgnoreCase("0104")) {
                            process0104cmdData(definition);
                        }
                    }

                }
            }

            DeviceProtocolModel deviceProtocolModel = new DeviceProtocolModel();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = formatter.format(curDate);

            deviceProtocolModel.setUpdateTime(str);
            deviceProtocolModel.setProtocolDate(String.valueOf(protocolDataModel.getProtocolDate()));
            deviceProtocolModel.setProductId(protocolDataModel.getProductId());
            deviceProtocolModel.setBase64data(Base64Utils.getBase64Data(protocolDataModel));

            saveXmlStringSqlite(deviceProtocolModel);
        }
    }

    private void process0104cmdData(ProtocolDefinition definition) {
        try {
            if (definition != null) {
                List<ByteDefinition> bytelist = definition.getByteDefList();
                if (bytelist != null && bytelist.size() > 0) {
                    HashMap<String, BaseDefinition> mapper = new HashMap<String, BaseDefinition>();
                    int index = 0;
                    for (int i = 0; i < bytelist.size(); i++) {
                        ByteDefinition item = bytelist.get(i);
                        if (item != null) {
                            List<BitDefinition> bList = item.getBitDefList();
                            if (bList != null && bList.size() > 0) {
                                for (int j = 0; j < bList.size(); j++) {
                                    BitDefinition bItem = bList.get(j);
                                    //处理bit
                                    if (!TextUtils.isEmpty(bItem.getProperty())) {
                                        index += bItem.getLength();
                                        bItem.setIndex(index);
                                        mapper.put(bItem.getProperty(), bItem);
                                    }
                                }
                            } else {
                                //处理byte
                                if (!TextUtils.isEmpty(item.getProperty())) {
                                    index += item.getLength();
                                    item.setIndex(index);
                                    mapper.put(item.getProperty(), item);
                                }
                            }
                        }
                        pro0104Mapper.put(definition.getId(), mapper);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载协议定义
     *
     * @param fileList
     */
    private void loadProtocalDefinition(List<File> fileList) {
        WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            for (File file : fileList) {
                try {
                    ProtocolDefinition definition = (ProtocolDefinition) parser.parseXMLFile(file);
                    Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "parse successful file=" + file.getAbsolutePath());
                    String proXmlString = parser.toXml(definition);

                    DeviceProtocolModel deviceProtocolModel = new DeviceProtocolModel();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);

                    ProtocolDataModel protocolDataModel = new ProtocolDataModel();
                    protocolDataModel.setProductId(0);
                    protocolDataModel.setProtocolDate(0);
                    ProtocolBean protocolBean = new ProtocolBean();
                    protocolBean.setContent(proXmlString);
                    protocolBean.setProtocolName(definition.getId());
                    List<ProtocolBean> list = new ArrayList<>();
                    protocolDataModel.setList(list);
                    deviceProtocolModel.setUpdateTime(str);
                    deviceProtocolModel.setProtocolDate(String.valueOf(protocolDataModel.getProtocolDate()));
                    deviceProtocolModel.setProductId(protocolDataModel.getProductId());
                    deviceProtocolModel.setBase64data(Base64Utils.getBase64Data(protocolDataModel));

                    saveXmlStringSqlite(deviceProtocolModel);
                    protocolMapper.put(definition.getId(), definition);
                } catch (XStreamException e) {
                    Logc.e(Logc.HetLogRecordTag.WIFI_EX_LOG, "protocol xml file parse error[fileName:{}]-error msg:{} " + file.getAbsolutePath() + e.getMessage());
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    private File[] getFiles(String dirPath, final String filterRegex) throws FileNotFoundException {
        Logc.i(Logc.HetLogRecordTag.INFO_WIFI, "协议文件存放地址：" + dirPath);
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (!dir.exists()) {
                throw new FileNotFoundException("can't find the directory[" + dirPath + "]");
            }
            try {
                dirPath = getClass().getResource("/").toURI().getPath() + dirPath;
                dir = new File(dirPath);
                if (!dir.exists()) {
                    throw new FileNotFoundException("can't find the directory[" + dirPath + "]");
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String path = pathname.getName().toLowerCase();
                return path.matches(filterRegex);
            }
        });
    }


    /**
     * 获取协议定义
     *
     * @param id
     * @return
     */
    public ProtocolDefinition getProtocolDefinition(String id) {
        if (id == null) {
            throw new IllegalArgumentException("paramter can't be null");
        }
        ProtocolDefinition definition = null;
        ReadLock readLock = lock.readLock();
        try {
            readLock.lock();
            definition = protocolMapper.get(id.toUpperCase());
        } finally {
            readLock.unlock();
        }
        return definition;
    }

    public HashMap<String, BaseDefinition> get0104Mapper(String id) {
        if (id == null) {
            throw new IllegalArgumentException("paramter can't be null");
        }
        HashMap<String, BaseDefinition> definition = null;
        ReadLock readLock = lock.readLock();
        try {
            readLock.lock();
            definition = pro0104Mapper.get(id.toUpperCase());
        } finally {
            readLock.unlock();
        }
        return definition;
    }

    /**
     * 将xml字符串存入数据库
     *
     * @param definition
     */
    private void saveXmlStringSqlite(DeviceProtocolModel definition) {
        if (deviceProtocolDao != null && definition != null) {
            deviceProtocolDao.insert(definition);
        }
    }

    public static void main(String[] at) {
//        long time = 1460443881000;
        long cc = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
        Date curDate = new Date(cc);//获取当前时间
        String str = formatter.format(curDate);
//        System.out.println("long:" + cc + " str:" + str);
    }

    private void readInSqlteThread(final DeviceProtocolModel productModel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (deviceProtocolDao != null) {
                    if (productModel != null) {
                        ProtocolDataModel proArr = Base64Utils.getBase64Object(productModel.getBase64data(), ProtocolDataModel.class);
                        if (proArr != null && proArr.getList() != null && proArr.getList().size() > 0) {
                            List<ProtocolBean> list = proArr.getList();
                            for (ProtocolBean item : list) {
                                ProtocolDefinition definition = (ProtocolDefinition) parser.paseXML(item.getContent());
                                if (definition != null) {
                                    StringBuffer sb = new StringBuffer();
                                    sb.append(item.getProductVersion());
                                    sb.append("-");
                                    sb.append(item.getDeviceTypeId());
                                    sb.append("-");
                                    sb.append(item.getDeviceSubtypeId());
                                    sb.append("-");
                                    sb.append(item.getCommand());
                                    definition.setId(sb.toString());
                                    protocolMapper.put(definition.getId(), definition);
                                }
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private void readInSqlte(DeviceProtocolModel productModel) {
        if (deviceProtocolDao != null) {
            if (productModel != null) {
                ProtocolDataModel proArr = Base64Utils.getBase64Object(productModel.getBase64data(), ProtocolDataModel.class);
                if (proArr != null && proArr.getList() != null && proArr.getList().size() > 0) {
                    List<ProtocolBean> list = proArr.getList();
                    for (ProtocolBean item : list) {
                        ProtocolDefinition definition = (ProtocolDefinition) parser.paseXML(item.getContent());
                        if (definition != null) {
                            StringBuffer sb = new StringBuffer();
                            sb.append(item.getProductVersion());
                            sb.append("-");
                            sb.append(item.getDeviceTypeId());
                            sb.append("-");
                            sb.append(item.getDeviceSubtypeId());
                            sb.append("-");
                            sb.append(item.getCommand());
                            definition.setId(sb.toString());
                            protocolMapper.put(definition.getId(), definition);
                        }
                    }
                }
            }
        }
    }

    public String getProtocolDate(int productId) {
        if (deviceProtocolDao != null) {
            DeviceProtocolModel productModel = deviceProtocolDao.get(productId);
            if (productModel != null) {
                readInSqlte(productModel);
                return productModel.getProtocolDate();
            }
        }
        return "0";
    }

    public ProtocolDataModel getProtocolByProductId(int productId) {
        if (deviceProtocolDao == null)
            return null;
        DeviceProtocolModel deviceProtocolModel = deviceProtocolDao.get(productId);
        if (deviceProtocolModel == null)
            return null;
        ProtocolDataModel proArr = Base64Utils.getBase64Object(deviceProtocolModel.getBase64data(), ProtocolDataModel.class);
        return proArr;
    }

    public void loadAll() {
        if (deviceProtocolDao == null)
            return;
        List<DeviceProtocolModel> deviceProtocolModel = deviceProtocolDao.loadAll();
        if (deviceProtocolModel == null)
            return;
        for (DeviceProtocolModel model : deviceProtocolModel) {
            readInSqlte(model);
        }
    }
}
