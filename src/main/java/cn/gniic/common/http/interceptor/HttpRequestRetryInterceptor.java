package cn.gniic.common.http.interceptor;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by fayuan.peng on 2019/3/5.
 * 重试拦截器
 * 1、当出现IOException异常时，需要重试
 * 2、当响应报文为空时，需要重试
 */
@Slf4j
public class HttpRequestRetryInterceptor implements Interceptor {

    /**
     * 重试时间间隔：3秒
     */
    private static final Integer RETRY_INTERVAL = 3 * 1000;

    /**
     * 重试次数
     */
    private int maxRetry;

    public HttpRequestRetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(Chain chain) {
        int retry = 0;
        Request request = chain.request();

        while (retry <= maxRetry) {
            try {
                Response response = proceed(chain, request);
                /*String responseBody = response.body().string();
                if(StringUtils.isBlank(responseBody)) {
                    throw new IOException("响应报文为空");
                }*/
                return response;
            } catch (IOException ex) {
                retry++;
                log.info("请求 {} 失败，异常信息：{}", request.url(), ex.getMessage());
                log.info("请求重试,当前次数: [{}]", retry);
                try {
                    Thread.sleep(RETRY_INTERVAL);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        return null;
    }

    private Response proceed(Chain chain, Request request) throws IOException {
        return chain.proceed(request);
    }
}

