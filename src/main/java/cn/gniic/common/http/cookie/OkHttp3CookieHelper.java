package cn.gniic.common.http.cookie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Cookie Helper for OkHttp3 <br> <br> <p> usage example:<br>
 *
 * <pre>
 * <code>
 * String url = "https://example.com/webapi";
 *
 * 		OkHttp3CookieHelper cookieHelper = new OkHttp3CookieHelper();
 * 		cookieHelper.setCookie(url, "cookie_name", "cookie_value");
 *
 * 		OkHttpClient client = new OkHttpClient.Builder()
 * 				.cookieJar(cookieHelper.cookieJar())
 * 				.build();
 *
 * 		Request request = new Request.Builder()
 * 				.url(url)
 * 				.build();
 * </code>
 * </pre>
 *
 *
 * okhttp 默认的cookie策略是不保存cookie，对于一些网站会有问题，需要增加cookie支持 copied from https://github.com/riversun/okhttp3-cookie-helper/blob/master/src/main/java/org/riversun/okhttp3/OkHttp3CookieHelper.java
 *
 * @author Tom Misawa (riversun.org@gmail.com)
 * @version $Id: OkHttp3CookieHelper.java,  2020-03-05 9:59 AM xbkaishui Exp $$
 */
@Slf4j
public class OkHttp3CookieHelper {

    private Map<String, List<Cookie>> mServerCookieStore = new ConcurrentHashMap<>();

    private Map<String, List<Cookie>> mClientCookieStore = new ConcurrentHashMap<>();

    private CookieJar mCookieJar = new CookieJar() {
        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {

            List<Cookie> serverCookieList = mServerCookieStore.get(url.host());

            if (serverCookieList == null) {
                serverCookieList = new ArrayList<>();
            }

            String urlPath = url.encodedPath();

            serverCookieList = filterCookieByPath(urlPath, serverCookieList);

            List<Cookie> clientCookieStore = mClientCookieStore.get(url.host());

            if (clientCookieStore != null) {
                clientCookieStore = filterCookieByPath(urlPath, clientCookieStore);
                serverCookieList.addAll(clientCookieStore);
            }

            return serverCookieList != null ? serverCookieList : new ArrayList<>();
        }

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> unmodifiableCookieList) {
            // Why 'new ArrayList<Cookie>'?
            // Since 'unmodifiableCookieList' can not be changed, create a new
            // one
            String hostCookieKey = url.host();
            List<Cookie> oldCookies = mServerCookieStore.get(hostCookieKey);
            List<Cookie> newCookies = new ArrayList<>(unmodifiableCookieList);
            Set<String> cookieNameSets = newCookies.stream().map(cookie -> generateCookieId(cookie))
                    .collect(Collectors.toSet());
            //如果新的cookie没有包含老的，则加上
            if (CollectionUtils.isNotEmpty(oldCookies)) {
                oldCookies.forEach(oldCookie -> {
                    String cookieId = generateCookieId(oldCookie);
                    if (!cookieNameSets.contains(cookieId)) {
                        log.info("add old cookie {} ", oldCookie);
                        newCookies.add(oldCookie);
                    }
                });
            }
            mServerCookieStore.put(url.host(), newCookies);

            // The persistence code should be described here if u want.
        }

    };

    private String generateCookieId(Cookie oldCookie) {
        return oldCookie.name() + "_" + oldCookie.path();
    }

    private List<Cookie> filterCookieByPath(String urlPath, List<Cookie> clientCookieStore) {
        clientCookieStore = clientCookieStore.stream().filter(cookie -> {
            String path = cookie.path();
            if (StringUtils.isEmpty(path)) {
                return true;
            }
            if (StringUtils.containsIgnoreCase(urlPath, path)) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        return clientCookieStore;
    }

    /**
     * Set cookie
     */
    public void setCookie(String url, Cookie cookie) {

        final String host = HttpUrl.parse(url).host();

        List<Cookie> cookieListForUrl = mClientCookieStore.get(host);
        if (cookieListForUrl == null) {
            cookieListForUrl = new ArrayList<>();
            mClientCookieStore.put(host, cookieListForUrl);
        }
        putCookie(cookieListForUrl, cookie);

    }

    /**
     * Set cookie
     */
    public void setCookie(String url, String cookieName, String cookieValue) {
        final HttpUrl httpUrl = HttpUrl.parse(url);
        setCookie(url, Cookie.parse(httpUrl, cookieName + "=" + cookieValue));
    }

    /**
     * Set cookie
     */
    public void setCookie(HttpUrl httpUrl, String cookieName, String cookieValue) {
        setCookie(httpUrl.host(), Cookie.parse(httpUrl, cookieName + "=" + cookieValue));
    }

    /**
     * Returns CookieJar
     */
    public CookieJar cookieJar() {
        return mCookieJar;
    }

    private void putCookie(List<Cookie> storedCookieList, Cookie newCookie) {

        Cookie oldCookie = null;
        for (Cookie storedCookie : storedCookieList) {

            // create key for comparison
            final String oldCookieKey = storedCookie.name() + storedCookie.path();
            final String newCookieKey = newCookie.name() + newCookie.path();

            if (oldCookieKey.equals(newCookieKey)) {
                oldCookie = storedCookie;
                break;
            }
        }
        if (oldCookie != null) {
            storedCookieList.remove(oldCookie);
        }
        storedCookieList.add(newCookie);
    }

}
