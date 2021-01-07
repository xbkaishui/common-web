package cn.gniic.common.filter.sign;

import cn.gniic.common.constants.SignConstants;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Created by xbkaishui on 2019/8/1.
 */
public class RequestSignerTest {

    private RequestSigner requestSigner = new RequestSigner();

    @Test
    public void verify() throws Exception {

        String auth = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5eWV2QXYwMTU2MzI2NTUzNTIwNSIsInVpZCI6IjIwNzUyOSIsImV4cCI6MTU2NDkzNTc3MiwiYXV0aG9yaXRpZXMiOiJST0xFX0FETUlOLEFVVEhfV1JJVEUifQ.NwhM15wlSpdCZo8RSg2f7w-IGxhG_DKS8y81l59qP1LHS1-Nm1sLeGgSG0ebMbpWEX1Nk3HPI9xdeStowpao9Q";
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader(SignConstants.SIGN_AUTH_HEADER, auth);
        servletRequest.addHeader(SignConstants.SIGN_TIME_HEADER, "1564644246000");
        servletRequest.addHeader(SignConstants.USER_AGENT_HEADER, "2.1.1  samsung SM-G570M");
        servletRequest.setMethod("GET");
        servletRequest.addParameter("month", "201911");
        servletRequest.addHeader(SignConstants.SIGN_HEADER, "d509a595457b348e112b5aae1db57c86");
        requestSigner.verify(servletRequest);
        servletRequest.addHeader(SignConstants.SIGN_HEADER, "d509a595457b348e112b5aae1db57c87");
        requestSigner.verify(servletRequest);
    }

    @Test
    public void sign() throws Exception {
        String auth = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5eWV2QXYwMTU2MzI2NTUzNTIwNSIsInVpZCI6IjIwNzUyOSIsImV4cCI6MTU2NDkzNTc3MiwiYXV0aG9yaXRpZXMiOiJST0xFX0FETUlOLEFVVEhfV1JJVEUifQ.NwhM15wlSpdCZo8RSg2f7w-IGxhG_DKS8y81l59qP1LHS1-Nm1sLeGgSG0ebMbpWEX1Nk3HPI9xdeStowpao9Q";
        auth = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqOHNpZExUMTU2NDA1NTM1NDEwNiIsInVpZCI6IjIwNzU0OCIsImV4cCI6MTU2NzIzNzc4NSwiYXV0aG9yaXRpZXMiOiJST0xFX0FETUlOLEFVVEhfV1JJVEUifQ.bQV60k7byeTJ8Inflp2PAx3WL2KkR2Ti4qrPxf0dAD-K1eLPQf9xAlPvM3PiZkLUxzAwd-2Hkx1JBJKL9eT-xA";
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader(SignConstants.SIGN_AUTH_HEADER, auth);
        servletRequest.addHeader(SignConstants.SIGN_TIME_HEADER, "1564644246000");
        servletRequest.addHeader(SignConstants.USER_AGENT_HEADER, "2.1.1  samsung SM-G570M");
        servletRequest.setMethod("GET");
        servletRequest.addParameter("month", "201911");
        String sign = requestSigner.sign(servletRequest);
        System.out.println(sign);
        Assert.assertNotNull(sign);
        Assert.assertEquals("6dc3e6e68f228c97ecf2d2a454139ac4", sign);
    }

}