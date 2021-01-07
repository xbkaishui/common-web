package cn.gniic.common.http;

import cn.gniic.common.util.JsonUtil;
import com.google.common.collect.Sets;
import java.util.Set;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xbkaishui
 * @version $Id: HttpLogger.java,  2019-06-21 9:23 PM xbkaishui Exp $$
 */
public class HttpLogger implements HttpLoggingInterceptor.Logger {

    private static final Logger logger = LoggerFactory.getLogger(HttpLogger.class);

    private static final ThreadLocal<StringBuilder> mMessage = ThreadLocal
            .withInitial(() -> new StringBuilder());

    private static final Set<String> supportTypes = Sets.newHashSet("--> GET", "--> POST");

    private boolean ignoreLargeContent = true;

    @Override
    public void log(final String message) {
        supportTypes.stream().forEach(supportType -> {
            if (message.startsWith(supportType)) {
                mMessage.get().setLength(0);
            }
        });
        // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
        if ((message.startsWith("{") && message.endsWith("}"))
                || (message.startsWith("[") && message.endsWith("]"))) {
            String newMessage = JsonUtil.formatJson(JsonUtil.decodeUnicode(message));
            mMessage.get().append(newMessage.concat("\n"));
        } else {
            mMessage.get().append(message.concat("\n"));
        }
        // 响应结束，打印整条日志
        if (message.startsWith("<-- END HTTP")) {
            String logMsg = mMessage.get().toString();
            if (logMsg.length() > 1024 * 10 && ignoreLargeContent) {
                logger.warn("msg is too large skip");
            } else {
                logger.info(logMsg);
            }
            mMessage.remove();
        }
    }

    public boolean isIgnoreLargeContent() {
        return ignoreLargeContent;
    }

    public void setIgnoreLargeContent(boolean ignoreLargeContent) {
        this.ignoreLargeContent = ignoreLargeContent;
    }
}
