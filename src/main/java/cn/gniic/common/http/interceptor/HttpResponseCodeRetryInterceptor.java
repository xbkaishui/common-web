package cn.gniic.common.http.interceptor;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * http网络请求重试，根据responce code，只要是response code 不成功的情况下默认重试调用3次
 *
 * @author xbkaishui
 * @version $Id: HttpClientUtil.java,  2019-06-21 9:25 PM xbkaishui Exp $$
 */
@Slf4j
public class HttpResponseCodeRetryInterceptor implements Interceptor {

    /**
     * 重试时间间隔：3秒
     */
    private static final Integer RETRY_INTERVAL = 3 * 1000;

    /**
     * 重试次数
     */
    private int maxRetry;

    public HttpResponseCodeRetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(Chain chain) {
        int retry = 0;
        Request request = chain.request();
        Response response = null;
        boolean responseOK = false;
        while (!responseOK && retry < maxRetry) {
            try {
                response = proceed(chain, request);
                responseOK = response.isSuccessful();
            } catch (Exception e) {
                log.error(String.format("请求 %s 失败，异常信息：%s", request.url(), e.getMessage()), e);
            } finally {
                if (!responseOK) {
                    log.info("请求重试,当前次数: [{}]", retry);
                    retry++;
                    try {
                        Thread.sleep(RETRY_INTERVAL);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
        // otherwise just pass the original response on
        return response;
    }

    private Response proceed(Chain chain, Request request) throws IOException {
        return chain.proceed(request);
    }
}

