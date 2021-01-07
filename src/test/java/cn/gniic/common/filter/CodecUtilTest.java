package cn.gniic.common.filter;

import cn.gniic.common.filter.sign.CodecUtil;

import java.io.ByteArrayInputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by xbkaishui on 2019/8/1.
 */
public class CodecUtilTest {

    @Test
    public void getSha256() throws Exception {
        String data = "{\"nickName\":\"xbkaishuihlq22224444\"}";
        System.out.println(DigestUtils.sha256Hex(data));
        String signData = CodecUtil.getSha256(data);
        System.out.println(signData);

        String newSignData = CodecUtil.getSha256(new ByteArrayInputStream(new byte[0]));
        System.out.println(signData);

        Assert.assertEquals(newSignData, signData);
    }

}