package io.sufe.gateway.filter;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.sufe.gateway.common.SessionContext;
import io.sufe.gateway.filter.exception.ErrorMessageLogFilter;
import io.sufe.gateway.filter.exception.ErrorResponseSenderFilter;
import io.sufe.gateway.filter.prepare.HttpProtocolCheckFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Slf4j
public class ProxyRunner {
	public static void run(SessionContext context) {
		try {
			DefaultFilterPipeLine.INSTANCE.get(HttpProtocolCheckFilter.DEFAULT_NAME).fireSelf(context); // 开始执行
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
					log.error("系统内部错误，错误信息：{}", ExceptionUtils.getStackTrace(throwable));
				}
				DefaultFilterPipeLine.INSTANCE.get(ErrorResponseSenderFilter.DEFAULT_NAME).fireSelf(sessionContext);
			} else { // 有错误，则将指向errorFilter进行处理
				sessionContext.setThrowable(t);
				DefaultFilterPipeLine.INSTANCE.get(ErrorMessageLogFilter.DEFAULT_NAME).fireSelf(sessionContext);
			}
		} catch (Exception e) {
			log.error("系统内部错误，错误信息：{}", ExceptionUtils.getStackTrace(e));
//			sessionContext.setHttpResponseStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
			// 直接打印错误
			DefaultFilterPipeLine.INSTANCE.get(ErrorResponseSenderFilter.DEFAULT_NAME).fireSelf(sessionContext);
		}
	}
}
