package cn.gniic.common.web.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 成功返回
 *
 * @author Caratacus
 */
@Getter
@ToString
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class SuccessResponses<T> extends ApiResponses<T> {

    private static final long serialVersionUID = 1L;
    /**
     * http 状态码
     */
    private Integer code;
    /**
     * 结果集返回
     */
    private T result;

    /**
     * 返回消息
     */
    private String msg = "OK";

    private String version = "1.0";

}
