package com.xxl.rpc.admin.registry.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author xuxueli 2018-12-03
 */
public class DiscoveryRequest extends OpenApiRequest implements Serializable {
    public static final long serialVersionUID = 42L;

    /**
     * instance list which want discovery
     */
    private List<DiscoveryInstance> instanceList;

    public List<DiscoveryInstance> getInstanceList() {
        return instanceList;
    }

    public void setInstanceList(List<DiscoveryInstance> instanceList) {
        this.instanceList = instanceList;
    }

}
