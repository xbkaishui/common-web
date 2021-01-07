package cn.gniic.common.tool.generate.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author xingbingbing.xb
 * @version $Id: DBConnectionConfig.java, v 0.1 2019年04月28日 2:56 PM xbkaishui Exp $
 */
@Getter
@ToString
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class DBConnectionConfig {

    private String url;
    /**
     * 数据库连接用户名
     */
    private String username;
    /**
     * 数据库连接密码
     */
    private String password;

}