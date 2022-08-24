package com.xxl.rpc.admin.test.dao;

import com.xxl.rpc.admin.core.model.XxlRpcRegistry;
import com.xxl.rpc.admin.core.util.JacksonUtil;
import com.xxl.rpc.admin.dao.IXxlRpcRegistryDao;
import com.xxl.rpc.admin.dao.IXxlRpcRegistryDataDao;
import com.xxl.rpc.admin.dao.IXxlRpcRegistryMessageDao;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class XxlRpcRegistryDaoTest {
    private static Logger logger = LoggerFactory.getLogger(XxlRpcRegistryDaoTest.class);

    @Resource
    private IXxlRpcRegistryDao xxlRpcRegistryDao;
    @Resource
    private IXxlRpcRegistryDataDao xxlRpcRegistryDataDao;
    @Resource
    private IXxlRpcRegistryMessageDao xxlRpcRegistryMessageDao;

    @Test
    public void test(){
        List<XxlRpcRegistry> registryList = xxlRpcRegistryDao.pageList(0, 100, null, null, null);
        logger.info(JacksonUtil.writeValueAsString(registryList));
    }


}
