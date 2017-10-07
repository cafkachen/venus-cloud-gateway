package org.xujin.venus.cloud.gw.server.utils;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.lb.HashLoadBalancer;
import org.xujin.venus.cloud.gw.server.lb.LoadBalanceFactory;
import org.xujin.venus.cloud.gw.server.lb.LoadBalancer;

@Component
public class SpringCloudHelper implements ApplicationContextAware {

	private static ApplicationContext applicationContext = null;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		if (SpringCloudHelper.applicationContext == null) {
			SpringCloudHelper.applicationContext = applicationContext;
		}

	}

	public static ServiceInstance getServiceInstanceByLB(
			JanusHandleContext sessionContext, String applicationName) {
		LoadBalancer loadBalancer = LoadBalanceFactory
				.createLoadBalancerForService("applicationName", HashLoadBalancer.NAME);
		List<ServiceInstance> list = applicationContext
				.getBean("discoveryClient", DiscoveryClient.class)
				.getInstances(applicationName);
		ServiceInstance instance = loadBalancer.choose(list, sessionContext);
		return instance;
	}

}
