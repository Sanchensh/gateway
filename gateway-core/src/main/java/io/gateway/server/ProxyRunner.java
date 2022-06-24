package io.gateway.server;

import io.gateway.common.SessionContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyRunner {
	public static void run(SessionContext context) {
		try {
//			GatewayFilterPipeLine.instance.get(HttpProtocolCheckFilter.DEFAULT_NAME).fireSelf(context); // 开始执行
		} catch (Throwable e) {
			errorProcess(context, e);
		}
	}

	// 错误处理的统一入口。由于异步的原因，在调用filter出错，会catch住，然后调用该接口
	public static void errorProcess(SessionContext sessionContext, Throwable t) {
		try {
			Throwable throwable = sessionContext.getThrowable();
			// 说明在处理错误当中，又发生了错误(该方法被调用了两次)，则直接到发送数据的地方
			if (throwable != null) {
				if (t != null) {
					log.error("系统内部错误，错误信息：{}", throwable);
				}
//				GatewayFilterPipeLine.instance.get(ErrorResponseSenderFilter.DEFAULT_NAME).fireSelf(sessionContext);
			} else { // 有错误，则将指向errorFilter进行处理
				sessionContext.setThrowable(t);
//				GatewayFilterPipeLine.instance.get(FilterNameUtil.getFilterName(ErrorMessageLogFilter.class)).fireSelf(sessionContext);
			}
		} catch (Exception e) {
//			sessionContext.setHttpResponseStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
			// 直接打印错误
//			GatewayFilterPipeLine.instance.get(ErrorResponseSenderFilter.DEFAULT_NAME).fireSelf(sessionContext);
		}
	}
}
