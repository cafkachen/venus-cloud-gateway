package org.xujin.venus.cloud.gw.server.lb;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.xujin.venus.cloud.gw.server.core.JanusHandleContext;

public class HashLoadBalancer extends AbstractLoadBalancer {

	public static final String NAME = "hash";

	private static Logger log = LoggerFactory.getLogger(LoadBalanceFactory.class);

	@Override
	protected ServiceInstance doChoose(List<ServiceInstance> instanceList,
			JanusHandleContext janusHandleContext) {
		String hashKey = "janus_server";
		try {
			int hashServerSize = instanceList.size();
			if (hashServerSize > 0) {
				int index = Math.abs(hashKey.hashCode() % hashServerSize);
				return instanceList.get(index);
			}
			return null;
		}
		catch (Throwable ex) {
			log.error("LBHash:" + ex.getMessage(), ex);
			return null;
		}
	}

}
