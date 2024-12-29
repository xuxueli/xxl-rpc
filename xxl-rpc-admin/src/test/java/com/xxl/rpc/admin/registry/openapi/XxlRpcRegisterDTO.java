package com.xxl.rpc.admin.registry.openapi;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class XxlRpcRegisterDTO {

    public static class RegisterInstance implements Serializable {
        public static final long serialVersionUID = 42L;

        /**
         * AppName（应用唯一标识）
         */
        private String appname;

        /**
         * 注册节点IP
         */
        private String ip;

        /**
         * 注册节点端口号
         */
        private int port;

        /**
         * 扩展信息（可选）
         */
        private String extendInfo;

        public RegisterInstance() {
        }
        public RegisterInstance(String appname, String ip, int port, String extendInfo) {
            this.appname = appname;
            this.ip = ip;
            this.port = port;
            this.extendInfo = extendInfo;
        }

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getExtendInfo() {
            return extendInfo;
        }

        public void setExtendInfo(String extendInfo) {
            this.extendInfo = extendInfo;
        }

    }

    public static class RegisterRequest implements Serializable {
        public static final long serialVersionUID = 42L;

        /**
         * accessToken
         */
        private String accessToken;

        /**
         * Env
         */
        private String env;

        /**
         * client instance
         */
        private RegisterInstance instance;


        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getEnv() {
            return env;
        }

        public void setEnv(String env) {
            this.env = env;
        }

        public RegisterInstance getInstance() {
            return instance;
        }

        public void setInstance(RegisterInstance instance) {
            this.instance = instance;
        }

    }

    public static class OpenApiResponse implements Serializable {
        public static final long serialVersionUID = 42L;

        public static final int SUCCESS_CODE = 200;
        public static final int FAIL_CODE = 203;


        private int code;

        private String msg;

        public OpenApiResponse() {}
        public OpenApiResponse(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }


        @Override
        public String toString() {
            return "OpenApiResponse{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    '}';
        }

        public boolean isSuccess() {
            return code == SUCCESS_CODE;
        }

    }

    public static class DiscoveryRequest implements Serializable {
        public static final long serialVersionUID = 42L;

        /**
         * accessToken
         */
        private String accessToken;

        /**
         * Env
         */
        private String env;

        /**
         * instance list which want discovery
         */
        private List<String> appnameList;

        /**
         * simple Query
         *      true: only summary data (md5)
         *      false: query all data (detail + md5)
         */
        private boolean simpleQuery;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getEnv() {
            return env;
        }

        public void setEnv(String env) {
            this.env = env;
        }

        public List<String> getAppnameList() {
            return appnameList;
        }

        public void setAppnameList(List<String> appnameList) {
            this.appnameList = appnameList;
        }

        public boolean isSimpleQuery() {
            return simpleQuery;
        }

        public void setSimpleQuery(boolean simpleQuery) {
            this.simpleQuery = simpleQuery;
        }

    }

    public static class DiscoveryResponse extends OpenApiResponse implements Serializable {
        public static final long serialVersionUID = 42L;

        /**
         * Env
         */
        private String env;

        /**
         * discovery result data
         *
         * structure：Map
         * 		key：appname
         * 		value：List<RegisterInstance> = List ～ instance
         *
         */
        private Map<String, List<InstanceCacheDTO>> discoveryData;

        /**
         * discovery result data-md5
         *
         * structure：Map
         * 		key：appname
         * 		value：md5
         *
         */
        private Map<String, String> discoveryDataMd5;

        public DiscoveryResponse(){}
        public DiscoveryResponse(int code, String msg) {
            super(code, msg);
        }

        public String getEnv() {
            return env;
        }

        public void setEnv(String env) {
            this.env = env;
        }

        public Map<String, List<InstanceCacheDTO>> getDiscoveryData() {
            return discoveryData;
        }

        public void setDiscoveryData(Map<String, List<InstanceCacheDTO>> discoveryData) {
            this.discoveryData = discoveryData;
        }

        public Map<String, String> getDiscoveryDataMd5() {
            return discoveryDataMd5;
        }

        public void setDiscoveryDataMd5(Map<String, String> discoveryDataMd5) {
            this.discoveryDataMd5 = discoveryDataMd5;
        }

    }

    public static class InstanceCacheDTO implements Serializable {
        private static final long serialVersionUID = 42L;

        /**
         * Env（环境唯一标识）
         */
        private String env;

        /**
         * AppName（应用唯一标识）
         */
        private String appname;

        /**
         * 注册节点IP
         */
        private String ip;

        /**
         * 注册节点端口号
         */
        private int port;

        /**
         * 扩展信息
         */
        private String extendInfo;

        public InstanceCacheDTO() {
        }

        public String getEnv() {
            return env;
        }

        public void setEnv(String env) {
            this.env = env;
        }

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getExtendInfo() {
            return extendInfo;
        }

        public void setExtendInfo(String extendInfo) {
            this.extendInfo = extendInfo;
        }

        @Override
        public String toString() {
            return "InstanceCacheDTO{" +
                    "env='" + env + '\'' +
                    ", appname='" + appname + '\'' +
                    ", ip='" + ip + '\'' +
                    ", port=" + port +
                    ", extendInfo='" + extendInfo + '\'' +
                    '}';
        }

        // tool

        /**
         * get sort key
         *
         * @return
         */
        public String getSortKey() {
            return ip + ":" + port;
        }

    }

}
