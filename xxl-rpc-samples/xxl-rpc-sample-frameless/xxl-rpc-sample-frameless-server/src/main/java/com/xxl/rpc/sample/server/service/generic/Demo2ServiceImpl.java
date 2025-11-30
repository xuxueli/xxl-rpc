package com.xxl.rpc.sample.server.service.generic;

import com.xxl.rpc.sample.server.service.DemoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Demo2ServiceImpl implements Demo2Service{
    private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

    @Override
    public User2DTO addUser(User2DTO user2DTO) {
        logger.info("addUser: {}", user2DTO);
        return new User2DTO(user2DTO!=null ? user2DTO.getName() : null, "add success.");
    }

}
