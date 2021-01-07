package cn.gniic.common.filter.sign;


import java.io.InputStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author xbkaishui
 * @version $Id: CodecUtil.java,  2019-08-01 10:18 AM xbkaishui Exp $$
 */
public class CodecUtil {

    public static String getSha256(InputStream ins) {
        byte[] digestData = null;
        try {
            digestData = DigestUtils.sha256(ins);
            return base64Encode(digestData);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSha256(String data) {
        byte[] digestData = null;
        try {
            digestData = DigestUtils.sha256(data);
            return base64Encode(digestData);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static String base64Encode(byte[] data) {
        return StringUtils.newStringUtf8(Base64.encodeBase64(data));
    }

    public static String md5(String data) {
        return DigestUtils.md5Hex(data);
    }

}
