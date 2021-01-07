package cn.gniic.common.tool.generate;

import static cn.gniic.common.constants.GeneratorConstants.GENERATOR_BASE_PACKAGE_NAME;
import static cn.gniic.common.constants.GeneratorConstants.SUPER_BASE_SERVICE_CLASS;
import static cn.gniic.common.constants.GeneratorConstants.SUPER_CONTROLLER_CLASS;
import static cn.gniic.common.constants.GeneratorConstants.SUPER_ENTITY_CLASS;
import static cn.gniic.common.constants.GeneratorConstants.SUPER_MAPPER_CLASS;
import static cn.gniic.common.constants.GeneratorConstants.SUPER_SERVICE_IMPL_CLASS;

import cn.gniic.common.tool.generate.config.DBConnectionConfig;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * 代码生成器父类
 * </p>
 *
 * @author Caratacus
 */
public abstract class SuperGenerator {

    /**
     * 获取TemplateConfig
     */
    protected TemplateConfig getTemplateConfig() {
        return new TemplateConfig().setXml(null);
    }

    /**
     * 获取InjectionConfig
     */
    protected InjectionConfig getInjectionConfig() {
        return new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>();
                this.setMap(map);
            }
        }.setFileOutConfigList(Collections.<FileOutConfig>singletonList(new FileOutConfig(
                "/templates/mapper.xml.vm") {
            // 自定义输出文件目录
            @Override
            public String outputFile(TableInfo tableInfo) {
                return getResourcePath() + "/mapper/" + tableInfo.getEntityName() + "Mapper.xml";
            }
        }));
    }

    /**
     * 获取PackageConfig
     */
    protected PackageConfig getPackageConfig() {
        return new PackageConfig()
                .setParent(getBasePackageName())
                .setController("controller")
                .setEntity("model.entity")
                .setMapper("mapper")
                .setService("service")
                .setServiceImpl("service.impl");
    }

    public String getBasePackageName() {
        return GENERATOR_BASE_PACKAGE_NAME;
    }

    /**
     * 获取StrategyConfig
     */
    protected StrategyConfig getStrategyConfig(String tableName) {
        List<TableFill> tableFillList = getTableFills();
        return new StrategyConfig()
                .setCapitalMode(false)// 全局大写命名
                .setTablePrefix("sys_")// 去除前缀
                .setNaming(NamingStrategy.underline_to_camel)// 表名生成策略
                //.setInclude(new String[] { "user" }) // 需要生成的表
                //自定义实体父类
                .setSuperEntityClass(SUPER_ENTITY_CLASS)
                // 自定义实体，公共字段
                .setSuperEntityColumns("id")
                .setTableFillList(tableFillList)
                // 自定义 mapper 父类
                .setSuperMapperClass(SUPER_MAPPER_CLASS)
                // 自定义 controller 父类
                .setSuperControllerClass(SUPER_CONTROLLER_CLASS)
                // 自定义 service 实现类父类
                .setSuperServiceImplClass(SUPER_SERVICE_IMPL_CLASS)
                // 自定义 service 接口父类
                .setSuperServiceClass(SUPER_BASE_SERVICE_CLASS)
                // 【实体】是否生成字段常量（默认 false）
                .setEntityColumnConstant(true)
                // 【实体】是否为构建者模型（默认 false）
                .setEntityBuilderModel(false)
                // 【实体】是否为lombok模型（默认 false）<a href="https://projectlombok.org/">document</a>
                .setEntityLombokModel(true)
                // Boolean类型字段是否移除is前缀处理
                .setEntityBooleanColumnRemoveIsPrefix(true)
                .setRestControllerStyle(false)
                .setRestControllerStyle(true)
                .setInclude(tableName);
    }

    /**
     * 获取TableFill策略
     */
    protected List<TableFill> getTableFills() {
        // 自定义需要填充的字段
        List<TableFill> tableFillList = new ArrayList<>();
        tableFillList.add(new TableFill("cTime", FieldFill.INSERT));
        tableFillList.add(new TableFill("uTime", FieldFill.INSERT_UPDATE));
        return tableFillList;
    }

    /**
     * 获取数据库连接
     */
    public abstract DBConnectionConfig getConnectionConfig();

    /**
     * 获取DataSourceConfig
     */
    public DataSourceConfig getDataSourceConfig() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig()
                .setDbType(DbType.MYSQL)// 数据库类型
                .setTypeConvert(new MySqlTypeConvert() {
                    @Override
                    public IColumnType processTypeConvert(GlobalConfig globalConfig,
                            String fieldType) {
                        if (fieldType.toLowerCase().equals("bit")) {
                            return DbColumnType.BOOLEAN;
                        }
                        if (fieldType.toLowerCase().equals("tinyint")) {
                            return DbColumnType.BOOLEAN;
                        }
                        if (fieldType.toLowerCase().equals("date")) {
                            return DbColumnType.LOCAL_DATE;
                        }
                        if (fieldType.toLowerCase().equals("time")) {
                            return DbColumnType.LOCAL_TIME;
                        }
                        if (fieldType.toLowerCase().equals("datetime")) {
                            return DbColumnType.LOCAL_DATE_TIME;
                        }
                        return super.processTypeConvert(globalConfig, fieldType);
                    }
                })
                .setDriverName("com.mysql.cj.jdbc.Driver");
        //get connection config
        DBConnectionConfig connectionConfig = getConnectionConfig();
        Preconditions.checkNotNull(connectionConfig, "connection info is null");
        dataSourceConfig.setUrl(connectionConfig.getUrl())
                .setUsername(connectionConfig.getUsername()).setPassword(
                connectionConfig.getPassword());
        return dataSourceConfig;
    }

    /**
     * 获取GlobalConfig
     */
    protected GlobalConfig getGlobalConfig() {
        String basePath = getJavaPath();
        System.out.println("base java Path " + basePath);
        return new GlobalConfig()
                .setOutputDir(basePath)//输出目录
                .setFileOverride(false)// 是否覆盖文件
                .setActiveRecord(false)// 开启 activeRecord 模式
                .setEnableCache(false)// XML 二级缓存
                .setBaseResultMap(false)// XML ResultMap
                .setBaseColumnList(false)// XML columList
                .setKotlin(false) //是否生成 kotlin 代码
                .setOpen(false)
                .setAuthor(getAuthor()) //作者
                //自定义文件命名，注意 %s 会自动填充表实体属性！
                .setEntityName("%s")
                .setMapperName("%sMapper")
                .setXmlName("%sMapper")
                .setServiceName("%sService")
                .setServiceImplName("%sServiceImpl")
                .setControllerName("%sRestController");
    }

    /**
     * 获取作者
     */
    public String getAuthor() {
        Map<String, String> map = System.getenv();
        String localUname = map.get("USERNAME");// 获取用户名
        if (StringUtils.isNotEmpty(localUname)) {
            return localUname;
        }
        localUname = map.get("USER");
        if (StringUtils.isNotEmpty(localUname)) {
            return localUname;
        }
        localUname = System.getProperty("user.name", "");
        return localUname;
    }


    /**
     * 获取根目录
     */
    private String getRootPath() {
        String cwd = System.getProperty("user.dir");
        return cwd;
    }

    /**
     * 获取JAVA目录
     */
    protected String getJavaPath() {
        return getRootPath() + "/src/main/java";
    }

    /**
     * 获取Resource目录
     */
    protected String getResourcePath() {
        return getRootPath() + "/src/main/resources";
    }

    /**
     * 获取AutoGenerator
     */
    public AutoGenerator getAutoGenerator(String tableName) {
        return new AutoGenerator()
                // 全局配置
                .setGlobalConfig(getGlobalConfig())
                // 数据源配置
                .setDataSource(getDataSourceConfig())
                // 策略配置
                .setStrategy(getStrategyConfig(tableName))
                // 包配置
                .setPackageInfo(getPackageConfig())
                // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
                .setCfg(getInjectionConfig())
                .setTemplate(getTemplateConfig());
    }

}
