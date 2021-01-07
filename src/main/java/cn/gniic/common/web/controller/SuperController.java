package cn.gniic.common.web.controller;

import cn.gniic.common.constants.PageCons;
import cn.gniic.common.util.TypeUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.gniic.common.util.AntiSQLFilter;
import cn.gniic.common.web.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SuperController
 *
 * @author Caratacus
 */
public class SuperController {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    /**
     * 成功返回
     *
     * @param object
     * @return
     */
    public <T> ApiResponses<T> success(T object) {
        return ApiResponses.<T>success(response, object);
    }

    /**
     * 成功返回
     *
     * @return
     */
    public ApiResponses<Void> success() {
        return success(HttpStatus.OK);
    }

    /**
     * 成功返回
     *
     * @param status
     * @param object
     * @return
     */
    public <T> ApiResponses<T> success(HttpStatus status, T object) {
        return ApiResponses.<T>success(response, status, object);
    }

    /**
     * 成功返回
     *
     * @param status
     * @return
     */
    public ApiResponses<Void> success(HttpStatus status) {
        return ApiResponses.<Void>success(response, status);
    }

    /**
     * 失败返回
     * @param errorCode
     * @return
     */
    public ApiResponses fail(Integer errorCode) {
        return ApiResponses.failure(errorCode, null);
    }

    /**
     * 失败返回
     * @param errorCode
     * @param errorMsg
     * @return
     */
    public ApiResponses fail(Integer errorCode, String errorMsg) {
        return ApiResponses.failure(errorCode, errorMsg);
    }
    /**
     * 获取分页对象
     *
     * @return
     */
    protected <T> Page<T> getPage() {
        return getPage(false);
    }

    /**
     * 获取分页对象
     *
     * @param openSort
     * @return
     */
    protected <T> Page<T> getPage(boolean openSort) {
        int index = 1;
        // 页数
        Integer cursor = TypeUtils.castToInt(request.getParameter(PageCons.PAGE_PAGE), index);
        // 分页大小
        Integer limit = TypeUtils.castToInt(request.getParameter(PageCons.PAGE_ROWS), PageCons.DEFAULT_LIMIT);
        // 是否查询分页
        Boolean searchCount = TypeUtils.castToBoolean(request.getParameter(PageCons.SEARCH_COUNT), true);
        limit = limit > PageCons.MAX_LIMIT ? PageCons.MAX_LIMIT : limit;
        Page<T> page = new Page<>(cursor, limit, searchCount);
        if (openSort) {
            page.setAsc(getParameterSafeValues(PageCons.PAGE_ASCS));
            page.setDesc(getParameterSafeValues(PageCons.PAGE_DESCS));
        }
        return page;
    }

    /**
     * 获取安全参数(SQL ORDER BY 过滤)
     *
     * @param parameter
     * @return
     */
    protected String[] getParameterSafeValues(String parameter) {
        return AntiSQLFilter.getSafeValues(request.getParameterValues(parameter));
    }
}
