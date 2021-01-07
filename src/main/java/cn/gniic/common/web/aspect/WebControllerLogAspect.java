package cn.gniic.common.web.aspect;

import cn.gniic.common.web.util.LogUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 *
 * @author xingbingbing.xb
 * @version $Id: WebControllerLogAspect.java, v 0.1 2019年04月28日 2:03 PM xbkaishui Exp $
 */
@Aspect
public class WebControllerLogAspect {

    @Pointcut("execution(public * cn.gniic..*RestController.*(..))")
    @SuppressWarnings("EmptyMethod")
    public void pointCut() {
    }

    @AfterReturning(returning = "ret", pointcut = "pointCut()")
    public void doAfterReturning(Object ret) {
        LogUtils.doAfterReturning(ret);
    }
}