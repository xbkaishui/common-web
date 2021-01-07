package cn.gniic.common.filter.sign;

import cn.gniic.common.constants.SignConstants;
import cn.gniic.common.enums.ErrorCodeEnum;
import cn.gniic.common.filter.MultiReadHttpServletRequest;
import cn.gniic.common.web.util.ResponseUtils;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * 登陆请求签名校验
 *
 * @author xbkaishui
 * @version $Id: HttpRequestSignFilter.java,  2019-07-26 3:01 PM xbkaishui Exp $$
 */
@Slf4j
public class HttpRequestSignVerifyFilter extends OncePerRequestFilter implements Ordered {

    private static final RequestSigner requestSigner = new RequestSigner();

    /**
     * 是否严格模式，严格模式下则直接抛异常
     */
    private boolean strictMode = false;

    /**
     * 签名失败是否终止请求，如果终止为true
     */
    private boolean stopRequest = true;

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 6;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        boolean needVerify = needVerify(request);
        String requestSign = request.getHeader(SignConstants.SIGN_HEADER);
        if (needVerify && strictMode) {
            if (StringUtils.isEmpty(requestSign)) {
                log.error("requestSign is empty in strict mode, just return ");
                ResponseUtils.sendFail(request, response, ErrorCodeEnum.SIGN_CHECK_FAILED,
                        new RuntimeException("verify failed!"));
                return;
            }
        }
        //兼容老的应用，如果老的不传默认放行 todo delete later
        if (StringUtils.isEmpty(requestSign) || !needVerify) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!(request instanceof MultiReadHttpServletRequest)) {
            request = new MultiReadHttpServletRequest(request);
        }
        try {
            requestSigner.verify(request);
        } catch (Exception e) {
            //print verify log
            log.error(e.getMessage(), e);
            //校验失败
            if (stopRequest) {
                ResponseUtils.sendFail(request, response, ErrorCodeEnum.SIGN_CHECK_FAILED, e);
                return;
            } else {
                log.warn("ignore sign verify fail");
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean needVerify(HttpServletRequest request) {
        //文件上传不需要
        if (request instanceof MultipartHttpServletRequest) {
            return false;
        }
        String contentType = request.getContentType();
        if (StringUtils.isNotEmpty(contentType) && contentType.contains("multipart")) {
            return false;
        }
        return true;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    public boolean isStopRequest() {
        return stopRequest;
    }

    public void setStopRequest(boolean stopRequest) {
        this.stopRequest = stopRequest;
    }
}
