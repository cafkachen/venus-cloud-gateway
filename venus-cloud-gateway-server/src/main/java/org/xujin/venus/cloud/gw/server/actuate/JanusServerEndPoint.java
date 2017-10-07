package org.xujin.venus.cloud.gw.server.actuate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;
import org.xujin.venus.cloud.gw.server.lb.HashLoadBalancer;
import org.xujin.venus.cloud.gw.server.lb.LoadBalanceFactory;
import org.xujin.venus.cloud.gw.server.lb.LoadBalancer;

/**
 * Janus Server的端点信息
 * @author xujin
 *
 */
@RestController
@RequestMapping("/janus/server")
public class JanusServerEndPoint {

	@Autowired
	private DiscoveryClient discoveryClient;

	@GetMapping("/getServiceList")
	public String getServiceList() {
		JanusHandleContext janusHandleContext = new JanusHandleContext();
		LoadBalancer loadBalancer = LoadBalanceFactory
				.createLoadBalancerForService("janus-provider", HashLoadBalancer.NAME);
		List<ServiceInstance> list = discoveryClient.getInstances("janus-provider");
		ServiceInstance Instance = loadBalancer.choose(list, janusHandleContext);
		return String.valueOf(Instance.getUri());
	}

}
