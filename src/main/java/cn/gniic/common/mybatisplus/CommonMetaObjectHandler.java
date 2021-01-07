package cn.gniic.common.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import java.time.LocalDateTime;
import java.util.Date;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * 通用填充类 适用于mybatis plus
 *
 * @author Caratacus
 */
public class CommonMetaObjectHandler implements MetaObjectHandler {

    /**
     * 创建时间
     */
    private final String createTime = "cTime";
    /**
     * 修改时间
     */
    private final String updateTime = "uTime";

    @Override
    public void insertFill(MetaObject metaObject) {
        setInsertFieldValByName(createTime, LocalDateTime.now(), metaObject);
        setInsertFieldValByName(updateTime, LocalDateTime.now(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setUpdateFieldValByName(updateTime, LocalDateTime.now(), metaObject);
    }

}
