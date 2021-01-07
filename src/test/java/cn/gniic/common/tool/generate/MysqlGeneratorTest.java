package cn.gniic.common.tool.generate;

import org.junit.Test;

/**
 *
 * @author xbkaishui
 * @version $Id: MysqlGeneratorTest.java, v 0.1 2019-04-28-2:55 PM xbkaishui Exp $
 */
public class MysqlGeneratorTest {
    @Test
    public void generator() throws Exception {
        String tableName = "task_info";
        MysqlGenerator generator = new MysqlGenerator();
        generator.generator(tableName);
    }


}