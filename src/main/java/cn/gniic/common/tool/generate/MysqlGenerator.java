package cn.gniic.common.tool.generate;

import cn.gniic.common.tool.generate.config.DBConnectionConfig;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import cn.gniic.common.constants.GeneratorConstants;

/**
 * <p>
 * Mysql代码生成器
 * </p>
 *
 * @author Caratacus
 */
public class MysqlGenerator extends SuperGenerator {

    /**
     * <p>
     * MySQL generator
     * </p>
     */
    public void generator(String tableName) {

        // 代码生成器
        AutoGenerator mpg = getAutoGenerator(tableName);
        mpg.execute();
        if (tableName == null) {
            System.err.println(" Generator Success !");
        } else {
            System.err.println(" TableName【 " + tableName + " 】" + "Generator Success !");

        }
    }

    @Override
    public String getBasePackageName() {
        return GeneratorConstants.GENERATOR_BASE_PACKAGE_NAME + ".demoweb";
    }

    @Override
    public DBConnectionConfig getConnectionConfig() {
        return DBConnectionConfig.builder().username("ctesting").password("c&g0$X%$^ig$d^^#otdb").url(
                "jdbc:mysql://credigotdb.cglzav2afyxr.rds.cn-north-1.amazonaws.com.cn:3306/credigotdb?serverTimezone=GMT%2B8&characterEncoding=utf-8&useSSL=false&autoReconnect=true&failOverReadOnly=false").build();
    }
}
