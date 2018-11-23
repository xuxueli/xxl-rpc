package com.xxl.rpc.admin.test.dao;

import com.xxl.rpc.admin.dao.IXxlRpcRegistryDao;
import com.xxl.rpc.admin.dao.IXxlRpcRegistryDataDao;
import com.xxl.rpc.admin.dao.IXxlRpcRegistryMessageDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DaoTest {

    @Resource
    private IXxlRpcRegistryDao xxlRpcRegistryDao;
    @Resource
    private IXxlRpcRegistryDataDao xxlRpcRegistryDataDao;
    @Resource
    private IXxlRpcRegistryMessageDao xxlRpcRegistryMessageDao;

    @Test
    public void test(){
        xxlRpcRegistryDao.pageList(0, 100, null, null, null);
    }


}
