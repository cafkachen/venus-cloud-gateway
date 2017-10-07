package org.xujin.venus.cloud.gw.server.lb;

import java.util.List;

import org.springframework.cloud.client.ServiceInstance;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;

/**
 * LoadBalancer 共同的方法
 * @author xujin
 *
 */
public interface LoadBalancer {

	ServiceInstance choose(List<ServiceInstance> list,
			JanusHandleContext janusHandleContext);
}
