package cn.gniic.common.web.responses;

import cn.gniic.common.model.ErrorCode;
import cn.gniic.common.web.util.ResponseUtils;
import cn.gniic.common.web.responses.SuccessResponses.SuccessResponsesBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

/**
 * GET: 200 OK
 * POST: 201 Created
 * PUT: 200 OK
 * PATCH: 200 OK
 * DELETE: 204 No Content
 * 接口返回(多态)
 *
 * @author Caratacus
 */
public class ApiResponses<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 不需要返回结果，默认返回状态200
     */
    public static ApiResponses<Void> success(HttpServletResponse response) {
        return success(response, HttpStatus.OK);
    }

    /**
     * 不需要返回结果
     */
    public static ApiResponses<Void> success(HttpServletResponse response, HttpStatus status) {
        return success(response, status, null, null);
    }

    public static ApiResponses<Void> success(HttpServletResponse response, HttpStatus status,
            String msg) {
        return success(response, status, null, msg);
    }

    /**
     * 成功返回
     */
    public static <T> ApiResponses<T> success(HttpServletResponse response, T object) {
        return success(response, HttpStatus.OK, object, null);
    }

    /**
     * 成功返回
     */
    public static <T> ApiResponses<T> success(HttpServletResponse response, T object, String msg) {
        return success(response, HttpStatus.OK, object, msg);

    }

    public static <T> ApiResponses<T> success(HttpServletResponse response, HttpStatus status,
            T object) {
        return success(response, HttpStatus.OK, object, null);

    }

    /**
     * 成功返回
     */
    public static <T> ApiResponses<T> success(HttpServletResponse response, HttpStatus status,
            T object, String msg) {
        if (status != null) {
            response.setStatus(status.value());
        }
        SuccessResponsesBuilder<T> builder = SuccessResponses.<T>builder().code(status.value());
        if (object != null) {
            builder.result(object);
        }
        //msg 给默认值
        if (StringUtils.isEmpty(msg)) {
            msg = "OK";
        }
        builder.msg(msg);
        return builder.build();

    }

    /**
     * 成功返回
     */
    public static <T> ApiResponses<T> success(HttpServletResponse response, Integer status,
                                              T object, String msg) {
        SuccessResponsesBuilder<T> builder = SuccessResponses.<T>builder().code(status);
        if (object != null) {
            builder.result(object);
        }
        //msg 给默认值
        if (StringUtils.isEmpty(msg)) {
            msg = "OK";
        }
        builder.msg(msg);
        return builder.build();

    }

    /**
     * 失败返回
     */
    public static <T> ApiResponses<T> failure(ErrorCode errorCode, Exception exception) {
        return ResponseUtils
                .exceptionMsg(FailedResponse.builder().msg(errorCode.getMsg()), exception)
                .show(errorCode.isShow())
                .time(LocalDateTime.now())
                .code(errorCode.getHttpCode())
                .build();
    }

    /**
     * 失败返回
     */
    public static <T> ApiResponses<T> failure(Integer errorCode, String errorMsg) {
        return failure(ErrorCode.builder().httpCode(errorCode).msg(errorMsg).build(), null);
    }

}
