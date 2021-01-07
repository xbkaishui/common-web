package cn.gniic.common.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.ToString;

/**
 * @author xbkaishui
 * @version $Id: BaseResult.java,  2019-10-21 7:55 PM xbkaishui Exp $$
 */
@ToString
public class BaseResult<T> implements Serializable {

    private Integer code;
    /**
     * 结果集返回
     */
    private T data;

    private String version = "1.0";

    /**
     * 错误描述
     */
    private String msg;
    /**
     * 异常信息
     */
    private String exception;

    /**
     * 客户端是否展示
     */
    private Boolean show;
    /**
     * 当前时间戳
     */
    private LocalDateTime time;


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
