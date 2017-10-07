package org.xujin.venus.cloud.gw.server.lb;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LoadBalance Factory
 * @author xujin
 *
 */
public class LoadBalanceFactory {

	private static Logger log = LoggerFactory.getLogger(LoadBalanceFactory.class);

	private static final ConcurrentHashMap<String, LoadBalancer> serviceLoadBalancerMap = new ConcurrentHashMap<>();

	public static LoadBalancer createLoadBalancerForService(String serviceName,
			String loadBalancer) {

		LoadBalancer balancer = serviceLoadBalancerMap.get(serviceName);
		if (balancer == null) {
			LoadBalancer newLoadBalancer = createLoadBalancer(loadBalancer);
			serviceLoadBalancerMap.putIfAbsent(serviceName, newLoadBalancer);
			balancer = serviceLoadBalancerMap.get(serviceName);
		}

		return balancer;
	}

	private static LoadBalancer createLoadBalancer(String loadBalancer) {
		if (RandomLoadBalancer.NAME.equalsIgnoreCase(loadBalancer)) {
			return createRandomLoadBalancer();
		}
		else if (RoundRobinLoadBalancer.NAME.equalsIgnoreCase(loadBalancer)) {
			return createRoundRobinBalancer();
		}
		else if (HashLoadBalancer.NAME.equalsIgnoreCase(loadBalancer)) {
			return createHashBalancer();
		}
		else if (loadBalancer.contains(".")) {
			try {
				Class<?> loadBalancerClz = Class.forName(loadBalancer);
				return (LoadBalancer) loadBalancerClz.newInstance();
			}
			catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				log.error("无法创建指定的负载均衡器:{},ex:{}", loadBalancer, e.getMessage());
			}
		}
		return createRoundRobinBalancer();
	}

	/**
	 * Create random load balancer load balancer.
	 *
	 * @return the load balancer
	 */
	public static LoadBalancer createRandomLoadBalancer() {
		return new RandomLoadBalancer();
	}

	public static LoadBalancer createRoundRobinBalancer() {
		return new RoundRobinLoadBalancer();
	}

	public static LoadBalancer createHashBalancer() {
		return new HashLoadBalancer();
	}

}
