package cn.gniic.common.filter;

import com.vip.vjtools.vjkit.mapper.JsonMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

/**
 * @author xbkaishui
 * @version $Id: HttpTraceLogFilter.java,  2019-07-26 2:47 PM xbkaishui Exp $$
 */
@Slf4j
public class HttpTraceLogFilter extends OncePerRequestFilter implements Ordered {

    private String IGNORE_CONTENT_TYPE = "multipart/form-data";

    private Set<String> urlBlackSet = new HashSet<>();

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        if (!isRequestValid(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!(request instanceof ModifyHttpServletRequestWrapper)) {
            request = new ModifyHttpServletRequestWrapper(request);
        }
        if (!(response instanceof ContentCachingResponseWrapper)) {
            response = new ContentCachingResponseWrapper(response);
        }
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
            status = response.getStatus();
        } finally {
            String path = request.getRequestURI();
            if (needFilter(request)) {
                //1. 记录日志
                HttpTraceLog traceLog = new HttpTraceLog();
                traceLog.setPath(path);
                traceLog.setMethod(request.getMethod());
                long latency = System.currentTimeMillis() - startTime;
                traceLog.setTimeTaken(latency);
                traceLog.setTime(LocalDateTime.now().toString());
                traceLog.setParameterMap(JsonMapper.INSTANCE.toJson(request.getParameterMap()));
                traceLog.setStatus(status);
                traceLog.setRequestBody(getRequestBody(request));
                traceLog.setResponseBody(getResponseBody(response));
                traceLog.setHeaders(getRequestHeader(request));
                log.info("trace_log: {}", JsonMapper.INSTANCE.toJson(traceLog));
            }
            updateResponse(response);
        }
    }

    /**
     * 是否需要过滤当前请求,
     * 规则是当前的请求不在黑名单内部，并且content_type 不是ignore范围
     */
    private boolean needFilter(HttpServletRequest request) {
        //文件上传不需要
        if (request instanceof MultipartHttpServletRequest) {
            return false;
        }
        String contentType = request.getContentType();
        if (StringUtils.isNotEmpty(contentType) && contentType.contains("multipart")) {
            return false;
        }
        String path = request.getRequestURI();
        boolean flag = true;
        if (CollectionUtils.isNotEmpty(urlBlackSet)) {
            flag = !urlBlackSet.stream().anyMatch(url -> path.startsWith(url));
        }
        flag = flag && !Objects
                .equals(IGNORE_CONTENT_TYPE, contentType);
        return flag;
    }

    private boolean isRequestValid(HttpServletRequest request) {
        try {
            new URI(request.getRequestURL().toString());
            return true;
        } catch (URISyntaxException ex) {
            return false;
        }
    }


    private String getRequestHeader(HttpServletRequest request) {
        Map<String, String> headersMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String headerName = enumeration.nextElement();
            String headerValue = request.getHeader(headerName);
            headersMap.put(headerName, headerValue);
        }
        headersMap.put("rip", request.getRemoteAddr());
        return JsonMapper.INSTANCE.toJson(headersMap);
    }

    public static String getRequestBody(HttpServletRequest request) {
        String requestBody = "";
        ContentCachingRequestWrapper wrapper = WebUtils
                .getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            try {
                requestBody = IOUtils
                        .toString(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
            } catch (IOException e) {
                // NOOP
            }
        }
        return requestBody;
    }

    public static String getResponseBody(HttpServletResponse response) {
        String responseBody = "";
        ContentCachingResponseWrapper wrapper = WebUtils
                .getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            try {
                responseBody = IOUtils
                        .toString(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
            } catch (IOException e) {
                // NOOP
            }
        }
        return responseBody;
    }

    private void updateResponse(HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper = WebUtils
                .getNativeResponse(response, ContentCachingResponseWrapper.class);
        Objects.requireNonNull(responseWrapper).copyBodyToResponse();
    }


    @Data
    private static class HttpTraceLog {

        private String path;
        private String parameterMap;
        private String method;
        private Long timeTaken;
        private String time;
        private Integer status;
        private String requestBody;
        private String responseBody;
        private String headers;

    }


    public Set<String> getUrlBlackSet() {
        return urlBlackSet;
    }

    public void setUrlBlackSet(Set<String> urlBlackSet) {
        this.urlBlackSet = urlBlackSet;
    }
}

