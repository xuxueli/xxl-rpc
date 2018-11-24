package com.xxl.rpc.admin.service.impl;

import com.xxl.rpc.admin.core.model.XxlRpcRegistry;
import com.xxl.rpc.admin.core.model.XxlRpcRegistryData;
import com.xxl.rpc.admin.core.model.XxlRpcRegistryMessage;
import com.xxl.rpc.admin.core.result.ReturnT;
import com.xxl.rpc.admin.core.util.JacksonUtil;
import com.xxl.rpc.admin.core.util.PropUtil;
import com.xxl.rpc.admin.dao.IXxlRpcRegistryDao;
import com.xxl.rpc.admin.dao.IXxlRpcRegistryDataDao;
import com.xxl.rpc.admin.dao.IXxlRpcRegistryMessageDao;
import com.xxl.rpc.admin.service.IXxlRpcRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxueli 2016-5-28 15:30:33
 */
@Service
public class XxlRpcRegistryServiceImpl implements IXxlRpcRegistryService, InitializingBean, DisposableBean {
    private static Logger logger = LoggerFactory.getLogger(XxlRpcRegistryServiceImpl.class);


    @Resource
    private IXxlRpcRegistryDao xxlRpcRegistryDao;
    @Resource
    private IXxlRpcRegistryDataDao xxlRpcRegistryDataDao;
    @Resource
    private IXxlRpcRegistryMessageDao xxlRpcRegistryMessageDao;

    @Value("${xxl.rpc.registry.data.filepath}")
    private String registryDataFilePath;


    @Override
    public Map<String, Object> pageList(int start, int length, String biz, String env, String key) {

        // page list
        List<XxlRpcRegistry> list = xxlRpcRegistryDao.pageList(start, length, biz, env, key);
        int list_count = xxlRpcRegistryDao.pageListCount(start, length, biz, env, key);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);		// 总记录数
        maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
        maps.put("data", list);  					// 分页列表
        return maps;
    }

    @Override
    public ReturnT<String> delete(int id) {
        XxlRpcRegistry xxlRpcRegistry = xxlRpcRegistryDao.loadById(id);
        if (xxlRpcRegistry != null) {
            xxlRpcRegistryDao.delete(id);
            xxlRpcRegistryDataDao.deleteData(xxlRpcRegistry.getBiz(), xxlRpcRegistry.getEnv(), xxlRpcRegistry.getKey());

            // sendRegistryDataUpdateMessage (delete)
            xxlRpcRegistry.setData("");
            xxlRpcRegistry.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
            sendRegistryDataUpdateMessage(xxlRpcRegistry);
        }

        return ReturnT.SUCCESS;
    }

    /**
     * send RegistryData Update Message
     */
    private void sendRegistryDataUpdateMessage(XxlRpcRegistry xxlRpcRegistry){
        String registryUpdateJson = JacksonUtil.writeValueAsString(xxlRpcRegistry);

        XxlRpcRegistryMessage registryMessage = new XxlRpcRegistryMessage();
        registryMessage.setType(0);
        registryMessage.setData(registryUpdateJson);
        xxlRpcRegistryMessageDao.add(registryMessage);
    }

    @Override
    public ReturnT<String> update(XxlRpcRegistry xxlRpcRegistry) {

        // valid
        if (xxlRpcRegistry.getBiz()==null || xxlRpcRegistry.getBiz().trim().length()==0 || xxlRpcRegistry.getBiz().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "业务线格式非法[0~255]");
        }
        if (xxlRpcRegistry.getEnv()==null || xxlRpcRegistry.getEnv().trim().length()==0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "环境格式非法[0~255]");
        }
        if (xxlRpcRegistry.getKey()==null || xxlRpcRegistry.getKey().trim().length()==0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "注册Key格式非法[0~255]");
        }
        if (xxlRpcRegistry.getData()==null || xxlRpcRegistry.getData().trim().length()==0) {
            xxlRpcRegistry.setData(JacksonUtil.writeValueAsString(new ArrayList<String>()));
        }
        List<String> valueList = JacksonUtil.readValue(xxlRpcRegistry.getData(), List.class);
        if (valueList == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "注册Value数据格式非法；限制为字符串数组JSON格式，如 [address,address2]");
        }

        // valid exist
        XxlRpcRegistry exist = xxlRpcRegistryDao.loadById(xxlRpcRegistry.getId());
        if (exist == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "ID参数非法");
        }

        // fill version
        boolean needMessage = false;
        if (!xxlRpcRegistry.getData().equals(exist.getData())) {
            xxlRpcRegistry.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
            needMessage = true;
        } else {
            xxlRpcRegistry.setVersion(exist.getVersion());
        }

        int ret = xxlRpcRegistryDao.update(xxlRpcRegistry);
        needMessage = ret>0?needMessage:false;

        if (needMessage) {
            // sendRegistryDataUpdateMessage (update)
            sendRegistryDataUpdateMessage(xxlRpcRegistry);
        }

        return ret>0?ReturnT.SUCCESS:ReturnT.FAIL;
    }

    @Override
    public ReturnT<String> add(XxlRpcRegistry xxlRpcRegistry) {

        // valid
        if (xxlRpcRegistry.getBiz()==null || xxlRpcRegistry.getBiz().trim().length()==0 || xxlRpcRegistry.getBiz().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "业务线格式非法[0~255]");
        }
        if (xxlRpcRegistry.getEnv()==null || xxlRpcRegistry.getEnv().trim().length()==0 || xxlRpcRegistry.getEnv().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "环境格式非法[0~255]");
        }
        if (xxlRpcRegistry.getKey()==null || xxlRpcRegistry.getKey().trim().length()==0 || xxlRpcRegistry.getKey().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "注册Key格式非法[0~255]");
        }
        if (xxlRpcRegistry.getData()==null || xxlRpcRegistry.getData().trim().length()==0) {
            xxlRpcRegistry.setData(JacksonUtil.writeValueAsString(new ArrayList<String>()));
        }
        List<String> valueList = JacksonUtil.readValue(xxlRpcRegistry.getData(), List.class);
        if (valueList == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "注册Value数据格式非法；限制为字符串数组JSON格式，如 [address,address2]");
        }

        // valid exist
        XxlRpcRegistry exist = xxlRpcRegistryDao.load(xxlRpcRegistry.getBiz(), xxlRpcRegistry.getEnv(), xxlRpcRegistry.getKey());
        if (exist != null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "注册Key请勿重复");
        }

        // fill version
        xxlRpcRegistry.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));

        int ret = xxlRpcRegistryDao.add(xxlRpcRegistry);
        boolean needMessage = ret>0?true:false;

        if (needMessage) {
            // sendRegistryDataUpdateMessage (add)
            sendRegistryDataUpdateMessage(xxlRpcRegistry);
        }

        return ret>0?ReturnT.SUCCESS:ReturnT.FAIL;
    }


    // ------------------------ remote registry ------------------------

    @Override
    public ReturnT<String> registry(XxlRpcRegistryData xxlRpcRegistryData) {

        // valid
        if (xxlRpcRegistryData.getBiz()==null || xxlRpcRegistryData.getBiz().trim().length()==0 || xxlRpcRegistryData.getBiz().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Biz Invalid[0~255]");
        }
        if (xxlRpcRegistryData.getEnv()==null || xxlRpcRegistryData.getEnv().trim().length()==0 || xxlRpcRegistryData.getEnv().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Env Invalid[0~255]");
        }
        if (xxlRpcRegistryData.getKey()==null || xxlRpcRegistryData.getKey().trim().length()==0 || xxlRpcRegistryData.getKey().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Key Invalid[0~255]");
        }
        if (xxlRpcRegistryData.getValue()==null || xxlRpcRegistryData.getValue().trim().length()==0 || xxlRpcRegistryData.getValue().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Value Invalid[0~255]");
        }

        // refresh or add
        int ret = xxlRpcRegistryDataDao.refresh(xxlRpcRegistryData);
        if (ret == 0) {
            xxlRpcRegistryDataDao.add(xxlRpcRegistryData);
        }

        // valid file status
        XxlRpcRegistry fileXxlRpcRegistry = getFileRegistryData(xxlRpcRegistryData);
        if (fileXxlRpcRegistry.getStatus() != 0) {
            return new ReturnT<String>(ReturnT.SUCCESS_CODE, "Status limited.");
        } else {
            if (fileXxlRpcRegistry.getDataList().contains(xxlRpcRegistryData.getValue())) {
                return new ReturnT<String>(ReturnT.SUCCESS_CODE, "Repeated limited.");
            }
        }

        // checkRegistryDataAndSendMessage
        checkRegistryDataAndSendMessage(xxlRpcRegistryData);

        return ReturnT.SUCCESS;
    }

    /**
     * update Registry And Message
     */
    private void checkRegistryDataAndSendMessage(XxlRpcRegistryData xxlRpcRegistryData){
        // data json
        List<XxlRpcRegistryData> xxlRpcRegistryDataList = xxlRpcRegistryDataDao.findData(xxlRpcRegistryData.getBiz(), xxlRpcRegistryData.getEnv(), xxlRpcRegistryData.getKey());
        List<String> valueList = new ArrayList<>();
        if (xxlRpcRegistryDataList!=null && xxlRpcRegistryDataList.size()>0) {
            for (XxlRpcRegistryData dataItem: xxlRpcRegistryDataList) {
                valueList.add(dataItem.getValue());
            }
        }
        String dataJson = JacksonUtil.writeValueAsString(valueList);

        // update registry and message
        XxlRpcRegistry xxlRpcRegistry = xxlRpcRegistryDao.load(xxlRpcRegistryData.getBiz(), xxlRpcRegistryData.getEnv(), xxlRpcRegistryData.getKey());
        boolean needMessage = false;
        if (xxlRpcRegistry == null) {
            xxlRpcRegistry = new XxlRpcRegistry();
            xxlRpcRegistry.setBiz(xxlRpcRegistryData.getBiz());
            xxlRpcRegistry.setEnv(xxlRpcRegistryData.getEnv());
            xxlRpcRegistry.setKey(xxlRpcRegistryData.getKey());
            xxlRpcRegistry.setData(dataJson);
            xxlRpcRegistry.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
            xxlRpcRegistryDao.add(xxlRpcRegistry);
            needMessage = true;
        } else {

            // check status, locked and disabled not use
            if (xxlRpcRegistry.getStatus() != 0) {
                return;
            }

            if (!xxlRpcRegistry.getData().equals(dataJson)) {
                xxlRpcRegistry.setData(dataJson);
                xxlRpcRegistry.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
                xxlRpcRegistryDao.update(xxlRpcRegistry);
                needMessage = true;
            }
        }

        if (needMessage) {
            // sendRegistryDataUpdateMessage (registry update)
            sendRegistryDataUpdateMessage(xxlRpcRegistry);
        }

    }

    @Override
    public ReturnT<String> remove(XxlRpcRegistryData xxlRpcRegistryData) {

        // valid
        if (xxlRpcRegistryData.getBiz()==null || xxlRpcRegistryData.getBiz().trim().length()==0 || xxlRpcRegistryData.getBiz().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Biz Invalid[0~255]");
        }
        if (xxlRpcRegistryData.getEnv()==null || xxlRpcRegistryData.getEnv().trim().length()==0 || xxlRpcRegistryData.getEnv().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Env Invalid[0~255]");
        }
        if (xxlRpcRegistryData.getKey()==null || xxlRpcRegistryData.getKey().trim().length()==0 || xxlRpcRegistryData.getKey().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Key Invalid[0~255]");
        }
        if (xxlRpcRegistryData.getValue()==null || xxlRpcRegistryData.getValue().trim().length()==0 || xxlRpcRegistryData.getValue().trim().length()>255) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "Value Invalid[0~255]");
        }

        // refresh or add
        xxlRpcRegistryDataDao.deleteDataValue(xxlRpcRegistryData.getBiz(), xxlRpcRegistryData.getEnv(), xxlRpcRegistryData.getKey(), xxlRpcRegistryData.getValue());

        // valid file status
        XxlRpcRegistry fileXxlRpcRegistry = getFileRegistryData(xxlRpcRegistryData);
        if (fileXxlRpcRegistry.getStatus() != 0) {
            return new ReturnT<String>(ReturnT.SUCCESS_CODE, "Status limited.");
        } else {
            if (!fileXxlRpcRegistry.getDataList().contains(xxlRpcRegistryData.getValue())) {
                return new ReturnT<String>(ReturnT.SUCCESS_CODE, "Repeated limited.");
            }
        }

        // checkRegistryDataAndSendMessage
        checkRegistryDataAndSendMessage(xxlRpcRegistryData);

        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> discovery(XxlRpcRegistryData xxlRpcRegistryData) {
        XxlRpcRegistry fileXxlRpcRegistry = getFileRegistryData(xxlRpcRegistryData);
        String dataJson = fileXxlRpcRegistry.getData();
        return new ReturnT<String>(dataJson);
    }

    // ------------------------ broadcase + file data ------------------------

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private volatile boolean executorStoped = false;
    private volatile List<Integer> readedMessageIds = Collections.synchronizedList(new ArrayList<Integer>());

    // get
    public XxlRpcRegistry getFileRegistryData(XxlRpcRegistryData xxlRpcRegistryData){

        // fileName
        String fileName = parseRegistryDataFileName(xxlRpcRegistryData.getBiz(), xxlRpcRegistryData.getEnv(), xxlRpcRegistryData.getKey());

        // read
        Properties prop = PropUtil.loadProp(fileName);
        if (prop!=null) {
            XxlRpcRegistry fileXxlRpcRegistry = new XxlRpcRegistry();
            fileXxlRpcRegistry.setData(prop.getProperty("data"));
            fileXxlRpcRegistry.setStatus(Integer.valueOf(prop.getProperty("status")));
            fileXxlRpcRegistry.setDataList(JacksonUtil.readValue(fileXxlRpcRegistry.getData(), List.class));
            return fileXxlRpcRegistry;
        }
        return null;
    }
    private String parseRegistryDataFileName(String biz, String env, String key){
        // fileName
        String fileName = registryDataFilePath
                .concat(File.separator).concat(biz)
                .concat(File.separator).concat(env)
                .concat(File.separator).concat(key)
                .concat(".properties");
        return fileName;
    }

    // set
    public String setFileRegistryData(XxlRpcRegistry xxlRpcRegistry){

        // fileName
        String fileName = parseRegistryDataFileName(xxlRpcRegistry.getBiz(), xxlRpcRegistry.getEnv(), xxlRpcRegistry.getKey());

        // write
        Properties prop = new Properties();
        prop.setProperty("data", xxlRpcRegistry.getData());
        prop.setProperty("status", String.valueOf(xxlRpcRegistry.getStatus()));

        String registryDataFile = PropUtil.writeProp(prop, fileName);

        logger.info(">>>>>>>>>>> xxl-rpc, setFileRegistryData: biz={}, env={}, key={}, data={}"
                , xxlRpcRegistry.getBiz(), xxlRpcRegistry.getEnv(), xxlRpcRegistry.getKey(), xxlRpcRegistry.getData());

        return registryDataFile;
    }
    // clean
    public void cleanFileRegistryData(List<String> registryDataFileList){
        filterChildPath(new File(registryDataFilePath), registryDataFileList);
    }

    public void filterChildPath(File parentPath, final List<String> registryDataFileList){
        if (!parentPath.exists() || parentPath.list()==null || parentPath.list().length==0) {
            return;
        }
        File[] childFileList = parentPath.listFiles();
        for (File childFile: childFileList) {
            if (childFile.isFile() && !registryDataFileList.contains(childFile.getPath())) {
                childFile.delete();

                logger.info(">>>>>>>>>>> xxl-rpc, cleanFileRegistryData, RegistryData Path={}", childFile.getPath());
            }
            if (childFile.isDirectory()) {
                if (parentPath.listFiles()!=null && parentPath.listFiles().length>0) {
                    filterChildPath(childFile, registryDataFileList);
                } else {
                    childFile.delete();
                }

            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        /**
         * broadcase new one registry-data-file     (1/1s)
         *
         * clean old message   (1/10s)
         */
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (!executorStoped) {
                    try {
                        // new message, filter readed
                        List<XxlRpcRegistryMessage> messageList = xxlRpcRegistryMessageDao.findMessage(readedMessageIds);
                        if (messageList!=null && messageList.size()>0) {
                            for (XxlRpcRegistryMessage message: messageList) {
                                readedMessageIds.add(message.getId());

                                if (message.getType() == 0) {   // from registry、add、update、deelete，ne need sync from db, only write

                                    XxlRpcRegistry xxlRpcRegistry = JacksonUtil.readValue(message.getData(), XxlRpcRegistry.class);

                                    // process data by status
                                    if (xxlRpcRegistry.getStatus() == 1) {
                                        // locked, not updated
                                    } else if (xxlRpcRegistry.getStatus() == 2) {
                                        // disabled, write empty
                                        xxlRpcRegistry.setData(JacksonUtil.writeValueAsString(new ArrayList<String>()));
                                    } else {
                                        // default, sync from db （aready sync before message, only write）
                                    }

                                    // sync file
                                    setFileRegistryData(xxlRpcRegistry);
                                }
                            }
                        }

                        // clean old message;
                        if (System.currentTimeMillis() % 10 ==0) {
                            xxlRpcRegistryMessageDao.cleanMessage(10);
                            readedMessageIds.clear();
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });

        /**
         *  clean old registry-data     (1/10s)
         *
         *  sync total registry-data db + file      (1+N/10s)
         *
         *  clean old registry-data file
         */
        final int beatTime = 10;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (!executorStoped) {
                    try {
                        // clean old registry-data in db
                        xxlRpcRegistryDataDao.cleanData(beatTime*2);

                        // sync registry-data, db + file
                        int offset = 0;
                        int pagesize = 1000;
                        List<String> registryDataFileList = new ArrayList<>();

                        List<XxlRpcRegistry> registryList = xxlRpcRegistryDao.pageList(offset, pagesize, null, null, null);
                        while (registryList!=null && registryList.size()>0) {

                            for (XxlRpcRegistry registryItem: registryList) {

                                // process data by status
                                if (registryItem.getStatus() == 1) {
                                    // locked, not updated
                                } else if (registryItem.getStatus() == 2) {
                                    // disabled, write empty
                                    String dataJson = JacksonUtil.writeValueAsString(new ArrayList<String>());
                                    registryItem.setData(dataJson);
                                } else {
                                    // default, sync from db
                                    List<XxlRpcRegistryData> xxlRpcRegistryDataList = xxlRpcRegistryDataDao.findData(registryItem.getBiz(), registryItem.getEnv(), registryItem.getKey());
                                    List<String> valueList = new ArrayList<String>();
                                    if (xxlRpcRegistryDataList!=null && xxlRpcRegistryDataList.size()>0) {
                                        for (XxlRpcRegistryData dataItem: xxlRpcRegistryDataList) {
                                            valueList.add(dataItem.getValue());
                                        }
                                    }
                                    String dataJson = JacksonUtil.writeValueAsString(valueList);

                                    // check update, sync db
                                    if (!registryItem.getData().equals(dataJson)) {
                                        registryItem.setData(dataJson);
                                        registryItem.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
                                        xxlRpcRegistryDao.update(registryItem);
                                    }
                                }

                                // sync file
                                String registryDataFile = setFileRegistryData(registryItem);

                                // collect registryDataFile
                                registryDataFileList.add(registryDataFile);
                            }


                            offset += 1000;
                            registryList = xxlRpcRegistryDao.pageList(offset, pagesize, null, null, null);
                        }

                        // clean old registry-data file
                        cleanFileRegistryData(registryDataFileList);

                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                    try {
                        TimeUnit.SECONDS.sleep(beatTime);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });


    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdownNow();
    }

}
