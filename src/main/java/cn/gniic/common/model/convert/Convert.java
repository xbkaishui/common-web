package cn.gniic.common.model.convert;

import cn.gniic.common.converter.BeanConverter;

import java.io.Serializable;

/**
 * <p>
 * 普通实体父类
 * </p>
 *
 * @author Caratacus
 */
public class Convert implements Serializable {

    /**
     * 获取自动转换后的JavaBean对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T convert(Class<T> clazz) {
        return BeanConverter.convert(clazz, this);
    }
}
