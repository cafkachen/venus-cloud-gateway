package org.xujin.venus.cloud.gw.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * Janus Server与admin交互的接口
 * @author xujin
 *
 */
@RestController
@RequestMapping("/query/")
public class ConfigController {

	/**
	 * 判断Janus Server是否集群中
	 */
	@GetMapping("/cluster")
	public void getDomainIfo() {

	}

	@GetMapping("/configs")
	public void getConfigs(
			@RequestParam(value = "version", required = false) String version,
			@RequestParam(value = "type") String type,
			@RequestParam(value = "oldChangeId", required = false) String oldChangeId) {

	}

}
