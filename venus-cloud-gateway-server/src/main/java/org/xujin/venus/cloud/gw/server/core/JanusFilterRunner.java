package org.xujin.venus.cloud.gw.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xujin.venus.cloud.gw.server.filter.base.DefaultFilterPipeLine;
import org.xujin.venus.cloud.gw.server.filter.error.ErrorFilter;
import org.xujin.venus.cloud.gw.server.filter.post.ResponseSendFilter;
import org.xujin.venus.cloud.gw.server.filter.pre.HttpProtocolCheckFilter;
import org.xujin.venus.cloud.gw.server.http.HttpCode;

/**
 * 管理Janus Server Filter中Filter的生命周期
 * @author xujin
 *
 */
public class JanusFilterRunner {

	private static Logger logger = LoggerFactory.getLogger(JanusFilterRunner.class);

	public static void run(JanusHandleContext context) {
		try {
			// 开始执行
			DefaultFilterPipeLine.getInstance().get(HttpProtocolCheckFilter.DEFAULT_NAME)
					.fireSelf(context);
		}
		catch (Throwable e) {
			errorProcess(context, e);
		}

	}

	// 错误处理的统一入口。由于异步的原因，在调用filter出错，会catch住，然后调用该接口
	public static void errorProcess(JanusHandleContext sessionContext, Throwable t) {

		try {
			Throwable throwable = sessionContext.getThrowable();
			// 说明在处理错误当中，又发生了错误(该方法被调用了两次)，则直接到发送数据的地方
			if (throwable != null) {
				DefaultFilterPipeLine.getInstance().get(ResponseSendFilter.DEFAULT_NAME)
						.fireSelf(sessionContext);
			}
			else { // 有错误，则将指向errorFilter进行处理
				sessionContext.setThrowable(t);
				DefaultFilterPipeLine.getInstance().get(ErrorFilter.DEFAULT_NAME)
						.fireSelf(sessionContext);
			}

		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			sessionContext.setMercuryHttpCode(HttpCode.HTTP_INTERNAL_SERVER_ERROR);
			// 直接打印错误
			DefaultFilterPipeLine.getInstance().get(ResponseSendFilter.DEFAULT_NAME)
					.fireSelf(sessionContext);
		}

	}

}