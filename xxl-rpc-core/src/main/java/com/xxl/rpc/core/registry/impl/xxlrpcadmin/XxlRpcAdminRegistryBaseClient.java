package com.xxl.rpc.core.registry.impl.xxlrpcadmin;

import com.xxl.rpc.core.registry.impl.xxlrpcadmin.model.XxlRpcAdminRegistryDataItem;
import com.xxl.rpc.core.registry.impl.xxlrpcadmin.model.XxlRpcAdminRegistryRequest;
import com.xxl.rpc.core.registry.impl.xxlrpcadmin.model.XxlRpcAdminRegistryResponse;
import com.xxl.rpc.core.util.BasicHttpUtil;
import com.xxl.rpc.core.util.GsonTool;
import com.xxl.rpc.core.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * base util for registry
 *
 * @author xuxueli 2018-12-01 21:40:04
 */
public class XxlRpcAdminRegistryBaseClient {
    private static Logger logger = LoggerFactory.getLogger(XxlRpcAdminRegistryBaseClient.class);


    private String adminAddress;
    private String accessToken;
    private String env;

    private List<String> adminAddressArr;


    public XxlRpcAdminRegistryBaseClient(String adminAddress, String accessToken, String env) {

        // fill
        this.adminAddress = adminAddress.trim();
        this.accessToken = accessToken;
        this.env = env.trim();

        // valid
        if (this.adminAddress==null || this.adminAddress.trim().length()==0) {
            throw new XxlRpcException("xxl-rpc, admin registry config[ADMIN_ADDRESS] empty.");
        }
        if (this.env==null || this.env.trim().length()<2 || this.env.trim().length()>255) {
            throw new XxlRpcException("xxl-rpc, admin registry config[ENV] Invalid[2~255].");
        }

        // parse
        adminAddressArr = new ArrayList<>();
        if (this.adminAddress.contains(",")) {
            adminAddressArr.addAll(Arrays.asList(this.adminAddress.split(",")));
        } else {
            adminAddressArr.add(adminAddress);
        }

    }

    /**
     * registry
     *
     * @param registryDataList
     * @return
     */
    public boolean registry(List<XxlRpcAdminRegistryDataItem> registryDataList){

        // valid
        if (registryDataList==null || registryDataList.size()==0) {
            throw new XxlRpcException("xxl-rpc registryDataList empty");
        }
        for (XxlRpcAdminRegistryDataItem registryParam: registryDataList) {
            if (registryParam.getKey()==null || registryParam.getKey().trim().length()<4 || registryParam.getKey().trim().length()>255) {
                throw new XxlRpcException("xxl-rpc registryDataList#key Invalid[4~255]");
            }
            if (registryParam.getValue()==null || registryParam.getValue().trim().length()<4 || registryParam.getValue().trim().length()>255) {
                throw new XxlRpcException("xxl-rpc registryDataList#value Invalid[4~255]");
            }
        }

        // pathUrl
        String pathUrl = "/api/registry";

        // param
        XxlRpcAdminRegistryRequest registryParamVO = new XxlRpcAdminRegistryRequest();
        registryParamVO.setAccessToken(this.accessToken);
        registryParamVO.setEnv(this.env);
        registryParamVO.setRegistryDataList(registryDataList);

        String paramsJson = GsonTool.toJson(registryParamVO);

        // result
        XxlRpcAdminRegistryResponse respObj = requestAndValid(pathUrl, paramsJson, 5);
        return respObj != null;
    }

    /**
     * remove
     *
     * @param registryDataList
     * @return
     */
    public boolean remove(List<XxlRpcAdminRegistryDataItem> registryDataList) {
        // valid
        if (registryDataList==null || registryDataList.size()==0) {
            throw new XxlRpcException("xxl-rpc registryDataList empty");
        }
        for (XxlRpcAdminRegistryDataItem registryParam: registryDataList) {
            if (registryParam.getKey()==null || registryParam.getKey().trim().length()<4 || registryParam.getKey().trim().length()>255) {
                throw new XxlRpcException("xxl-rpc registryDataList#key Invalid[4~255]");
            }
            if (registryParam.getValue()==null || registryParam.getValue().trim().length()<4 || registryParam.getValue().trim().length()>255) {
                throw new XxlRpcException("xxl-rpc registryDataList#value Invalid[4~255]");
            }
        }

        // pathUrl
        String pathUrl = "/api/remove";

        // param
        XxlRpcAdminRegistryRequest registryParamVO = new XxlRpcAdminRegistryRequest();
        registryParamVO.setAccessToken(this.accessToken);
        registryParamVO.setEnv(this.env);
        registryParamVO.setRegistryDataList(registryDataList);

        String paramsJson = GsonTool.toJson(registryParamVO);

        // result
        XxlRpcAdminRegistryResponse respObj = requestAndValid(pathUrl, paramsJson, 5);
        return respObj != null;
    }

    /**
     * discovery
     *
     * @param keys
     * @return
     */
    public Map<String, TreeSet<String>> discovery(Set<String> keys) {
        // valid
        if (keys==null || keys.size()==0) {
            throw new XxlRpcException("xxl-rpc keys empty");
        }

        // pathUrl
        String pathUrl = "/api/discovery";

        // param
        XxlRpcAdminRegistryRequest registryParamVO = new XxlRpcAdminRegistryRequest();
        registryParamVO.setAccessToken(this.accessToken);
        registryParamVO.setEnv(this.env);
        registryParamVO.setKeys(new ArrayList<String>(keys));

        String paramsJson = GsonTool.toJson(registryParamVO);

        // result
        XxlRpcAdminRegistryResponse respObj = requestAndValid(pathUrl, paramsJson, 5);

        // parse
        if (respObj!=null && respObj.getRegistryData()!=null) {
            return respObj.getRegistryData();
        }

        return null;
    }

    /**
     * discovery
     *
     * @param keys
     * @return
     */
    public boolean monitor(Set<String> keys) {
        // valid
        if (keys==null || keys.size()==0) {
            throw new XxlRpcException("xxl-rpc keys empty");
        }

        // pathUrl
        String pathUrl = "/api/monitor";

        // param
        XxlRpcAdminRegistryRequest registryParamVO = new XxlRpcAdminRegistryRequest();
        registryParamVO.setAccessToken(this.accessToken);
        registryParamVO.setEnv(this.env);
        registryParamVO.setKeys(new ArrayList<String>(keys));

        String paramsJson = GsonTool.toJson(registryParamVO);

        // result
        XxlRpcAdminRegistryResponse respObj = requestAndValid(pathUrl, paramsJson, 60);
        return respObj != null;
    }

    // ---------------------- net for registry



    private XxlRpcAdminRegistryResponse requestAndValid(String pathUrl, String requestBody, int timeout){

        for (String adminAddressUrl: adminAddressArr) {
            String finalUrl = adminAddressUrl + pathUrl;

            // request
            String responseData = BasicHttpUtil.postBody(finalUrl, requestBody, timeout);
            if (responseData == null) {
                return null;
            }

            // parse resopnse
            XxlRpcAdminRegistryResponse resopnseMap = null;
            try {
                resopnseMap = GsonTool.fromJson(responseData, XxlRpcAdminRegistryResponse.class);
            } catch (Exception e) {
                logger.debug("XxlRegistryBaseClient response error, responseData={}", responseData);
            }

            // valid resopnse
            if (resopnseMap != null && resopnseMap.getCode() == XxlRpcAdminRegistryResponse.SUCCESS_CODE) {
                return resopnseMap;
            } else {
                logger.warn("XxlRegistryBaseClient response fail, responseData={}", responseData);
            }
        }

        return null;
    }

}
