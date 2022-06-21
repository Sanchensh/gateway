package io.sufe.gateway.filter.exception;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.sufe.gateway.common.SessionContext;
import io.sufe.gateway.exception.GatewayServerException;
import io.sufe.gateway.filter.AbstractFilter;
import io.sufe.gateway.filter.AbstractFilterContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * 错误统一处理
 *
 */
@Slf4j
public class ErrorMessageLogFilter extends AbstractFilter {
	public static String DEFAULT_NAME = PRE_FILTER_NAME + ErrorMessageLogFilter.class.getSimpleName().toUpperCase();

	@Override
	public String name() {
		return DEFAULT_NAME;
	}

	@Override
	public void run(AbstractFilterContext filterContext, SessionContext sessionContext) throws GatewayServerException {
		Throwable throwable = sessionContext.getThrowable();
		filterContext.fireFilter(sessionContext, ErrorResponseHeaderFilter.DEFAULT_NAME);
	}

}
