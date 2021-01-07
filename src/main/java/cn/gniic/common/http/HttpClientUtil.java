package cn.gniic.common.http;

import cn.gniic.common.http.interceptor.HttpResponseCodeRetryInterceptor;
import cn.gniic.common.http.interceptor.HttpRequestRetryInterceptor;

import java.net.Proxy;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.Authenticator;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xbkaishui
 * @version $Id: HttpClientUtil.java,  2019-06-21 9:25 PM xbkaishui Exp $$
 */
public class HttpClientUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);
    public static final int READ_TIMEOUT = 10;
    public static final int WRITE_TIMEOUT = 120;
    public static final int CONNECT_TIMEOUT = 120;
    public static final int MAX_RETRY = 3;

    private static OkHttpClient noSSLHttpClient;

    private static OkHttpClient noSSLHttpClientWithOutRetry;


    /**
     * 每次调用都新生成一个httpclient，并设置代理，
     * okhttpclient本身比较轻量级
     */
    public static OkHttpClient newNoSSLHttpClientWithProxy(Proxy proxy,
            Authenticator proxyAuthenticator) {
        return newNoSSLHttpClientWithProxy(proxy, proxyAuthenticator, true);
    }

    /**
     * 每次调用都新生成一个httpclient，并设置代理，
     * okhttpclient本身比较轻量级
     */
    public static OkHttpClient newNoSSLHttpClientWithProxy(Proxy proxy,
            Authenticator proxyAuthenticator, boolean ignoreLargeContent) {
        Builder okHttpBuilder = newNoSSLHttpClientBuilder(proxy, proxyAuthenticator,
                ignoreLargeContent);
        return okHttpBuilder.build();
    }

    public static Builder newNoSSLHttpClientBuilder(Proxy proxy, Authenticator proxyAuthenticator,
            boolean ignoreLargeContent) {
        Builder okHttpBuilder = new Builder();
        //config time out
        configTimeout(okHttpBuilder);
        // Install the all-trusting trust manager
        configNoSSL(okHttpBuilder);
        // add log body
        configLogBody(okHttpBuilder, ignoreLargeContent);
        //config proxy
        if (proxy != null) {
            okHttpBuilder.proxy(proxy);
            if (proxyAuthenticator != null) {
                okHttpBuilder.proxyAuthenticator(proxyAuthenticator);
            }
        }
        return okHttpBuilder;
    }

    public static void configLogBody(Builder okHttpBuilder, boolean ignoreLargeContent) {
        HttpLogger logger = new HttpLogger();
        logger.setIgnoreLargeContent(ignoreLargeContent);
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(logger);
        logInterceptor.setLevel(Level.BODY);
        okHttpBuilder.addNetworkInterceptor(logInterceptor);
    }

    public static void configNoSSL(Builder okHttpBuilder) {
        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            okHttpBuilder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            okHttpBuilder.hostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * 生成一个新的httpclient
     */
    public static OkHttpClient newHttpClientWithAuthenticator(Authenticator authenticator,
            boolean needRetry) {
        return newHttpClientWithAuthenticator(authenticator, needRetry, null);
    }

    /**
     * 生成一个新的httpclient
     */
    public static OkHttpClient newHttpClientWithAuthenticator(Authenticator authenticator,
            boolean needRetry, Interceptor interceptor) {
        Builder okHttpBuilder = new Builder();
        //add retry
        configTimeout(okHttpBuilder);

        // Install the all-trusting trust manager
        configNoSSL(okHttpBuilder);

        // add log body
        configLogBody(okHttpBuilder, true);
        if (authenticator != null) {
            okHttpBuilder.authenticator(authenticator);
        }
        if (needRetry) {
            okHttpBuilder.addInterceptor(new HttpRequestRetryInterceptor(MAX_RETRY));
        }
        if (interceptor != null) {
            okHttpBuilder.addInterceptor(interceptor);
        }
        return okHttpBuilder.build();
    }

    /**
     * 单例模式，整个jvm只有一个httpclient，用于通用的请求处理
     * 默认会对httpcode不成功的做重试操作，建议内部使用
     */
    public synchronized static OkHttpClient getNOSSLHttpClient() {
        if (noSSLHttpClient != null) {
            return noSSLHttpClient;
        }
        Builder okHttpBuilder = new Builder();
        //add retry
        configTimeout(okHttpBuilder);

        // Install the all-trusting trust manager
        configNoSSL(okHttpBuilder);

        configResponseCodeRetry(okHttpBuilder);
        // add log body
        configLogBody(okHttpBuilder, true);
//        okHttpBuilder.addInterceptor(new UnzippingInterceptor());
        noSSLHttpClient = okHttpBuilder.build();
        return noSSLHttpClient;
    }

    private static void configResponseCodeRetry(Builder builder) {
        builder.addInterceptor(new HttpResponseCodeRetryInterceptor(MAX_RETRY));
    }

    /**
     * 不需要重试，用户外部接口调用
     */
    public synchronized static OkHttpClient getNOSSLHttpClientWithoutRetry() {
        if (noSSLHttpClientWithOutRetry != null) {
            return noSSLHttpClientWithOutRetry;
        }
        Builder okHttpBuilder = new Builder();
        //add retry
        configTimeout(okHttpBuilder);

        // Install the all-trusting trust manager
        configNoSSL(okHttpBuilder);

        okHttpBuilder.addInterceptor(new HttpRequestRetryInterceptor(MAX_RETRY));

        // add log body
        configLogBody(okHttpBuilder, true);
//        okHttpBuilder.addInterceptor(new UnzippingInterceptor());
        noSSLHttpClientWithOutRetry = okHttpBuilder.build();
        return noSSLHttpClientWithOutRetry;
    }

    public static void configTimeout(Builder okHttpBuilder) {
        okHttpBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .connectionPool(new ConnectionPool(20, 5L, TimeUnit.MINUTES));
    }

    static final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                        String authType)
                        throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                        String authType)
                        throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
    };

    public static Pair<String, String> executeRequest(OkHttpClient httpClient, Request request) {
        try {
            Response response = httpClient.newCall(request).execute();
            String url = response.request().url().toString();
            return new ImmutablePair<>(response.body().string(), url);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
}
