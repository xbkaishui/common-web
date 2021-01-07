package cn.gniic.common.http;

import cn.gniic.common.http.cookie.OkHttp3CookieHelper;
import okhttp3.Cookie;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

/**
 * Created by xbkaishui on 2020/3/5.
 */
public class HttpClientUtilTest {

    @Test
    public void testWithRetry() {
        OkHttpClient client = HttpClientUtil.getNOSSLHttpClient();
        String baseUrl = "https://www.jianshu.com/xx/dd";
        Request request = new Request.Builder().url(baseUrl).get().build();
        HttpClientUtil.executeRequest(client, request);
    }

    @Test
    public void testPostHeader() throws Exception {
        System.setProperty("INCLUDE_HTTP_HEADERS","mock_header");
        String url = "http://localhost:9083/user/demo";
        Request request = new Request.Builder().header("mock_header", "mock_value").url(url)
                .build();
        Response response = new OkHttpClient().newCall(request).execute();
        System.out.println(response.toString());
    }

    @Test
    public void newNoSSLHttpClientBuilder() throws Exception {

        String baseUrl = "https://www.jianshu.com/";
        String pageUrl = "https://www.jianshu.com/p/1a222a9394cexxxddd";

        OkHttp3CookieHelper cookieHelper = new OkHttp3CookieHelper();

        OkHttpClient.Builder builder = HttpClientUtil.newNoSSLHttpClientBuilder(null, null, false);
        builder.cookieJar(cookieHelper.cookieJar());
        OkHttpClient client = builder.build();

        Request request = new Request.Builder().url(baseUrl).get().build();
        HttpClientUtil.executeRequest(client, request);

        Cookie mockCookie = new Cookie.Builder().name("test").value("xxxx").path("/xxxx")
                .expiresAt(System.currentTimeMillis() + 100000).domain("www.jianshu.com").build();
        cookieHelper.setCookie(pageUrl, mockCookie);

        request = new Request.Builder().url(pageUrl).get().build();
        HttpClientUtil.executeRequest(client, request);
    }

}