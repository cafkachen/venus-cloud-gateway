package org.xujin.venus.cloud.gw.server.lb;

import java.util.List;

import org.springframework.cloud.client.ServiceInstance;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;

/**
 * 轮询调度 负载均衡
 * @author xujin
 *
 */
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {

	public static final String NAME = "roundrobin";

	@Override
	protected ServiceInstance doChoose(List<ServiceInstance> instanceList,
			JanusHandleContext janusHandleContext) {
		return null;
	}

}
