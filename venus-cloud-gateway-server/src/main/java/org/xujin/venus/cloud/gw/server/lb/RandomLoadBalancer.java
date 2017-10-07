package org.xujin.venus.cloud.gw.server.lb;

import java.util.List;
import java.util.Random;

import org.springframework.cloud.client.ServiceInstance;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;

/**
 * RandomLoadBalancer 随机算法
 * @author xujin
 *
 */
public class RandomLoadBalancer extends AbstractLoadBalancer {

	public static final String NAME = "random";

	private final Random random = new Random();

	public static final int DEFAULT_WEIGHT = 100;

	@Override
	protected ServiceInstance doChoose(List<ServiceInstance> instanceList,
			JanusHandleContext janusHandleContext) {
		int sum = getTotalWeight(instanceList);
		if (sum == 0) {
			return null;
		}

		// 权重一致，从所有服务器里随机一条
		if ((int) (sum * 1d / instanceList.size()) == DEFAULT_WEIGHT) {
			int index = PlatformDependent.threadLocalRandom()
					.nextInt(instanceList.size());
			return instanceList.get(index);
		}
		else {
			// 权重不一致, 环形扣减每台机的权重
			int point = PlatformDependent.threadLocalRandom().nextInt(sum) + 1;
			for (int i = 0, size = instanceList.size(); i < size; i++) {
				ServiceInstance serverInfo = instanceList.get(i);
				point -= DEFAULT_WEIGHT;
				if (point <= 0) {
					return serverInfo;
				}
			}
		}
		return null;
	}

	private static int getTotalWeight(List<ServiceInstance> serversList) {
		int totalWeight = 0;
		for (int i = 0, size = serversList.size(); i < size; i++) {
			totalWeight += DEFAULT_WEIGHT;
		}
		return totalWeight;
	}
}
