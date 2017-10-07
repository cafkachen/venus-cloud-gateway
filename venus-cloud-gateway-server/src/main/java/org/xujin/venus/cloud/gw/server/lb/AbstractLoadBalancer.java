package org.xujin.venus.cloud.gw.server.lb;

import java.util.List;

import org.springframework.cloud.client.ServiceInstance;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;

/**
 * 负载均衡器，顶级抽象类
 * @author xujin
 *
 */
public abstract class AbstractLoadBalancer implements LoadBalancer {

	@Override
	public ServiceInstance choose(List<ServiceInstance> serverList,
			JanusHandleContext janusHandleContext) {

		if (serverList == null || serverList.size() == 0) {
			return null;
		}
		if (serverList.size() == 1) {
			return serverList.get(0);
		}
		return doChoose(serverList, janusHandleContext);
	}

	/**
	 * 基于特定算法，选举可用server实例
	 *
	 * @param instanceList the instance list
	 * @param command the command
	 * @return the server instance
	 */
	protected abstract ServiceInstance doChoose(List<ServiceInstance> instanceList,
			JanusHandleContext janusHandleContext);

}
