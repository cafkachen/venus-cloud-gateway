package org.xujin.anus.server;

import org.junit.Test;
import org.xujin.venus.cloud.gw.server.startup.JanusBootStrap;

public class JanusBootStrapTest {

	@Test
	public void initDynamicFilterTest() {

		// FileUtils.writeStringToFile("/apps/conf/janus/filter.conf", "sss");
		JanusBootStrap.initConfigManager();

	}
}
