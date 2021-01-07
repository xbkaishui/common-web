package cn.gniic.common.p6spy;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.FormattedLogger;
import lombok.extern.slf4j.Slf4j;

/**
 * P6spy日志实现
 * <p>
 * https://blog.csdn.net/z69183787/article/details/43453581
 * <p/>
 *
 * @author Caratacus
 * @see FormattedLogger
 */
@Slf4j
public class P6spyLogger extends FormattedLogger {

    @Override
    public void logException(Exception e) {
        log.error(e.getMessage(), e);
    }

    @Override
    public void logText(String text) {
        log.info(text);
    }

    @Override
    public void logSQL(int connectionId, String now, long elapsed,
            Category category, String prepared, String sql, String url) {
        String categoryName = category.getName();
        //ignore commit category
        if (Category.COMMIT.getName().equalsIgnoreCase(categoryName)) {
            return;
        }
        if (StringUtils.isNotEmpty(sql) && sql.toUpperCase()
                .contains("SELECT 1 FROM DUAL".toUpperCase())) {
            return;
        }
        final String msg = strategy.formatMessage(connectionId, now, elapsed,
                category.toString(), prepared, sql, url);

        //add print result
        if (Category.RESULT.equals(category) || Category.RESULTSET.equals(category)) {
            log.info(msg);
            return;
        }
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        if (Category.ERROR.equals(category)) {
            log.error(msg);
        } else if (Category.WARN.equals(category)) {
            log.warn(msg);
        } else if (Category.DEBUG.equals(category)) {
            log.debug(msg);
        } else {
            log.info(msg);
        }
    }

    @Override
    public boolean isCategoryEnabled(Category category) {
        if (Category.ERROR.equals(category)) {
            return log.isErrorEnabled();
        } else if (Category.WARN.equals(category)) {
            return log.isWarnEnabled();
        } else if (Category.DEBUG.equals(category)) {
            return log.isDebugEnabled();
        } else {
            return log.isInfoEnabled();
        }
    }
}
