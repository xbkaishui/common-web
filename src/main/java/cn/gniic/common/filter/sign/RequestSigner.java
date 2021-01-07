package cn.gniic.common.filter.sign;

import cn.gniic.common.constants.SignConstants;
import com.google.common.collect.Sets;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xbkaishui
 * @version $Id: RequestSigner.java,  2019-08-01 1:50 PM xbkaishui Exp $$
 */
public class RequestSigner {

    private static final Set<String> HEADER_NAMES = Sets
            .newHashSet("authorization", "user-agent");

    private static final Logger log = LoggerFactory.getLogger(RequestSigner.class);

    public static final String LINE_SEPARATOR = "\n";

    /**
     * 如果内容为空的情况返回的hash值
     */
    private static final String DEFAULT_BODY_PALOAD_SHA_CONTENT = "47DEQpj8HBSa+/TImW+5JCeuQeRkm5NMpJWZG3hSuFU=";

    /**
     * 验证签名是否有效
     */
    public void verify(HttpServletRequest request) {
        String serverSign = sign(request);
        String clientSign = request.getHeader(SignConstants.SIGN_HEADER);
        if (!StringUtils.equals(clientSign, serverSign)) {
            throw new RuntimeException(
                    "sign verify fail client " + clientSign + " server " + serverSign);
        }
    }

    public String sign(HttpServletRequest request) {
        String signString = calcSignString(request);
        //try md5
        String signResult = signString(signString);
        log.info("sign result {} ", signResult);
        return signResult;
    }

    private String signString(String signString) {
        return CodecUtil.md5(signString);
    }

    private String calcSignString(HttpServletRequest request) {
        String ts = getTsHeader(request);
        String contentSha256 = calculateContentHash(request);
        if (log.isInfoEnabled()) {
            log.info("canonical Request contentSha256: {} ", contentSha256);
        }
        String queryParams = getCanonicalizedQueryString(request);
        log.info("query parameter {} ", queryParams);
        final StringBuilder canonicalRequestBuilder = new StringBuilder();
        canonicalRequestBuilder
                .append(getCanonicalizedHeaderString(request))
                .append(LINE_SEPARATOR)
                .append(queryParams)
                .append(LINE_SEPARATOR)
                .append(contentSha256)
                .append(LINE_SEPARATOR)
                .append(SignConstants.SECRET)
                .append(LINE_SEPARATOR)
                .append(ts);

        final String canonicalRequest = canonicalRequestBuilder.toString();
        if (log.isInfoEnabled()) {
            log.info("Canonical Request: {} ", canonicalRequest);
        }
        return canonicalRequest;
    }

    private String getTsHeader(HttpServletRequest request) {
        String ts = request.getHeader(SignConstants.SIGN_TIME_HEADER);
        if (StringUtils.isEmpty(ts)) {
            ts = "";
        }
        return ts;
    }

    /**
     * Calculate the hash of the request's payload. Subclass could override this
     * method to provide different values for "x-amz-content-sha256" header or
     * do any other necessary set-ups on the request headers. (e.g. aws-chunked
     * uses a pre-defined header value, and needs to change some headers
     * relating to content-encoding and content-length.)
     */
    protected String calculateContentHash(HttpServletRequest request) {
        InputStream payloadStream = getBinaryRequestPayloadStream(request);
        return CodecUtil.getSha256(payloadStream);
    }

    protected InputStream getBinaryRequestPayloadStream(HttpServletRequest request) {
        if (usePayloadForQueryParameters(request)) {
            return new ByteArrayInputStream(new byte[0]);
        }
        return getBinaryRequestPayloadStreamWithoutQueryParams(request);
    }

    protected InputStream getBinaryRequestPayloadStreamWithoutQueryParams(
            HttpServletRequest request) {
        try {
            InputStream is = request.getInputStream();
            if (is == null) {
                return new ByteArrayInputStream(new byte[0]);
            }
            return is;
//            ContentCachingRequestWrapper wrapper = WebUtils
//                    .getNativeRequest(request, ContentCachingRequestWrapper.class);
//            return new ByteArrayInputStream(wrapper.getContentAsByteArray());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to read request payload to sign request: " + e.getMessage(), e);
        }
    }

    /**
     * 获取请求头内容
     */
    protected String getCanonicalizedHeaderString(HttpServletRequest request) {
        Map<String, String> headersMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String headerName = enumeration.nextElement();
            String headerValue = request.getHeader(headerName);
            if (!shouldExcludeHeaderFromSigning(headerName)) {
                continue;
            }
            if (StringUtils.isNotEmpty(headerValue)) {
                headersMap.put(headerName, headerValue);
            }
        }
        StringBuilder buffer = new StringBuilder();

        headersMap.keySet().forEach(key -> {
            if (buffer.length() > 0) {
                buffer.append("&");
            }
            buffer.append(key)
                    .append("=")
                    .append(headersMap.get(key));
        });
        String content = buffer.toString();
        log.info("sign header {} ", content);
        return content;
    }

    private boolean shouldExcludeHeaderFromSigning(String header) {
        return HEADER_NAMES.contains(header);
    }


    /**
     * Examines the specified query string parameters and returns a
     * canonicalized form.
     * <p>
     * The canonicalized query string is formed by first sorting all the query
     * string parameters, then URI encoding both the key and value and then
     * joining them, in order, separating key value pairs with an '&'.
     *
     * @param parameters The query string parameters to be canonicalized.
     * @return A canonicalized form for the specified query string parameters.
     */
    protected String getCanonicalizedQueryString(Map<String, String[]> parameters) {

        final SortedMap<String, List<String>> sorted = new TreeMap<>();

        /**
         * Signing protocol expects the param values also to be sorted after url
         * encoding in addition to sorted parameter names.
         */
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            final String[] paramValues = entry.getValue();
            if (paramValues == null) {
                continue;
            }
            final List<String> encodedValues = new ArrayList<String>(
                    paramValues.length);
            for (String value : paramValues) {
                encodedValues.add(value);
            }
            Collections.sort(encodedValues);
            sorted.put(entry.getKey(), encodedValues);
        }

        final StringBuilder result = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : sorted.entrySet()) {
            for (String value : entry.getValue()) {
                if (result.length() > 0) {
                    result.append("&");
                }
                result.append(entry.getKey())
                        .append("=")
                        .append(value);
            }
        }
        return result.toString();
    }

    public String getCanonicalizedQueryString(HttpServletRequest request) {
        /*
         * If we're using POST and we don't have any request payload content,
         * then any request query parameters will be sent as the payload, and
         * not in the actual query string.
         */
        if (usePayloadForQueryParameters(request)) {
            return "";
        }
        return this.getCanonicalizedQueryString(request.getParameterMap());
    }

    public static boolean usePayloadForQueryParameters(HttpServletRequest request) {
        boolean requestIsPOST = StringUtils.equalsIgnoreCase(request.getMethod(), "POST");
        boolean requestHasNoPayload = false;
        try {
            requestHasNoPayload = (request.getInputStream() == null);
        } catch (IOException e) {
            requestHasNoPayload = true;
        }

        return requestIsPOST && requestHasNoPayload;
    }
}
