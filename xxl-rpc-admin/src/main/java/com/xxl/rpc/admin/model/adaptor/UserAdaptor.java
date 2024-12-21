package com.xxl.rpc.admin.model.adaptor;

import com.xxl.rpc.admin.model.dto.LoginUserDTO;
import com.xxl.rpc.admin.model.dto.UserDTO;
import com.xxl.rpc.admin.model.entity.User;
import com.xxl.tool.core.CollectionTool;
import com.xxl.tool.core.MapTool;
import com.xxl.tool.core.StringTool;

import java.util.*;
import java.util.stream.Collectors;

public class UserAdaptor {

    public static User adapt(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User xxlUser = new User();
        xxlUser.setId(userDTO.getId());
        xxlUser.setUsername(userDTO.getUsername());
        xxlUser.setPassword(userDTO.getPassword());
        xxlUser.setUserToken(userDTO.getUserToken());
        xxlUser.setStatus(userDTO.getStatus());
        xxlUser.setRealName(userDTO.getRealName());
        xxlUser.setRole(userDTO.getRole());
        xxlUser.setAddTime(userDTO.getAddTime());
        xxlUser.setUpdateTime(userDTO.getUpdateTime());
        return xxlUser;
    }

    public static UserDTO adapt(User user) {
        if (user == null) {
            return null;
        }

        UserDTO xxlUser = new UserDTO();
        xxlUser.setId(user.getId());
        xxlUser.setUsername(user.getUsername());
        xxlUser.setPassword(user.getPassword());
        xxlUser.setUserToken(user.getUserToken());
        xxlUser.setStatus(user.getStatus());
        xxlUser.setRealName(user.getRealName());
        xxlUser.setRole(user.getRole());
        xxlUser.setAddTime(user.getAddTime());
        xxlUser.setUpdateTime(user.getUpdateTime());
        return xxlUser;
    }

    /**
     * adapt to login user
     *
     * @param user
     * @return
     */
    public static LoginUserDTO adapt2LoginUser(User user) {
        if (user == null) {
            return null;
        }

        LoginUserDTO loginUserDTO = new LoginUserDTO();
        loginUserDTO.setId(user.getId());
        loginUserDTO.setUsername(user.getUsername());
        loginUserDTO.setPassword(user.getPassword());
        loginUserDTO.setUserToken(user.getUserToken());
        loginUserDTO.setRealName(user.getRealName());
        loginUserDTO.setRole(user.getRole());

        return loginUserDTO;
    }

}
