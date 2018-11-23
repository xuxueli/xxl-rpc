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
        if (xxlRpcRegistry.getData()==null) {
            xxlRpcRegistry.setData("[]");
        }
        List<String> valueList = JacksonUtil.readValue(xxlRpcRegistry.getData(), List.class);
        if (valueList == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "注册Value数据格式非法（JSON格式字符串数组）");
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
        if (xxlRpcRegistry.getData()==null) {
            xxlRpcRegistry.setData("[]");
        }
        List<String> valueList = JacksonUtil.readValue(xxlRpcRegistry.getData(), List.class);
        if (valueList == null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "注册Value数据格式非法（JSON格式字符串数组）");
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
        TreeSet<String> valueSet = new TreeSet<>();
        if (xxlRpcRegistryDataList!=null && xxlRpcRegistryDataList.size()>0) {
            for (XxlRpcRegistryData dataItem: xxlRpcRegistryDataList) {
                valueSet.add(dataItem.getValue());
            }
        }
        String dataJson = JacksonUtil.writeValueAsString(valueSet);

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
        xxlRpcRegistryDataDao.deleteData(xxlRpcRegistryData.getBiz(), xxlRpcRegistryData.getEnv(), xxlRpcRegistryData.getKey());

        // checkRegistryDataAndSendMessage
        checkRegistryDataAndSendMessage(xxlRpcRegistryData);

        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> discovery(XxlRpcRegistryData xxlRpcRegistryData) {
        String valueSet = getFileRegistryData(xxlRpcRegistryData);
        return new ReturnT<String>(valueSet);
    }


    // ------------------------ broadcase + file data ------------------------

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private volatile boolean executorStoped = false;
    private volatile List<Integer> readedMessageIds = Collections.synchronizedList(new ArrayList<Integer>());

    // get
    public String getFileRegistryData(XxlRpcRegistryData xxlRpcRegistryData){

        // fileName
        String fileName = registryDataFilePath
                .concat(File.separator).concat(xxlRpcRegistryData.getBiz())
                .concat(File.separator).concat(xxlRpcRegistryData.getEnv())
                .concat(File.separator).concat(xxlRpcRegistryData.getKey())
                .concat(".properties");

        // read
        Properties prop = PropUtil.loadProp(fileName);
        if (prop!=null) {
            return prop.getProperty("data");
        }
        return null;
    }

    // set
    public String setFileRegistryData(XxlRpcRegistry xxlRpcRegistry){

        // fileName
        String fileName = registryDataFilePath
                .concat(File.separator).concat(xxlRpcRegistry.getBiz())
                .concat(File.separator).concat(xxlRpcRegistry.getEnv())
                .concat(File.separator).concat(xxlRpcRegistry.getKey())
                .concat(".properties");

        // write
        Properties prop = new Properties();
        prop.setProperty("data", xxlRpcRegistry.getData());

        PropUtil.writeProp(prop, fileName);

        return fileName;
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
            if (childFile.isFile() && registryDataFileList.contains(childFile.getPath())) {
                childFile.delete();
            }
            if (childFile.isDirectory()) {
                filterChildPath(childFile, registryDataFileList);
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        /**
         * broadcase new one registry-data-file     (1/1s)
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

                                if (message.getType() == 0) {
                                    XxlRpcRegistry xxlRpcRegistry = JacksonUtil.readValue(message.getData(), XxlRpcRegistry.class);

                                    // lock pass, only human update
                                    if (xxlRpcRegistry.getStatus() == 1) {
                                        continue;
                                    }

                                    setFileRegistryData(xxlRpcRegistry);
                                }
                            }
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
         *  clean old message   (1/10s)
         *
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
                        // clean old message;
                        xxlRpcRegistryMessageDao.cleanMessage(beatTime);

                        // clean old registry-data in db
                        xxlRpcRegistryDataDao.cleanData(beatTime*2);

                        // sync registry-data, db + file
                        int offset = 0;
                        int pagesize = 1000;
                        List<String> registryDataFileList = new ArrayList<>();

                        List<XxlRpcRegistry> registryList = xxlRpcRegistryDao.pageList(offset, pagesize, null, null, null);
                        while (registryList!=null && registryList.size()>0) {

                            for (XxlRpcRegistry registryItem: registryList) {

                                // lock pass, only human update
                                if (registryItem.getStatus() == 1) {
                                    continue;
                                }

                                // data json
                                List<XxlRpcRegistryData> xxlRpcRegistryDataList = xxlRpcRegistryDataDao.findData(registryItem.getBiz(), registryItem.getEnv(), registryItem.getKey());
                                TreeSet<String> valueSet = new TreeSet<>();
                                if (xxlRpcRegistryDataList!=null && xxlRpcRegistryDataList.size()>0) {
                                    for (XxlRpcRegistryData dataItem: xxlRpcRegistryDataList) {
                                        valueSet.add(dataItem.getValue());
                                    }
                                }
                                String dataJson = JacksonUtil.writeValueAsString(valueSet);

                                // sync db + file
                                if (!registryItem.getData().equals(dataJson)) {
                                    registryItem.setData(dataJson);
                                    registryItem.setVersion(UUID.randomUUID().toString().replaceAll("-", ""));
                                    xxlRpcRegistryDao.update(registryItem);

                                    String registryDataFile = setFileRegistryData(registryItem);
                                    registryDataFileList.add(registryDataFile);
                                }

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
