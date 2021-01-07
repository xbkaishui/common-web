package cn.gniic.common.model;

import cn.gniic.common.model.convert.Convert;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 自增主键父类
 * </p>
 *
 * @author Caratacus
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BaseModel extends Convert {

    protected Integer id;

}
