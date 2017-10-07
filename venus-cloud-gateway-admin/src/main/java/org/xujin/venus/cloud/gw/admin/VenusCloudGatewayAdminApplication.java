package org.xujin.venus.cloud.gw.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * janus-admin主应用程序入口
 * @author xujin
 *
 */
@SpringBootApplication
@MapperScan("org.xujin.venus.cloud.gw.admin.mapper")
public class VenusCloudGatewayAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(VenusCloudGatewayAdminApplication.class, args);
	}
}
