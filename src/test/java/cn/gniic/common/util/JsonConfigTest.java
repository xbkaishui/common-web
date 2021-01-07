package cn.gniic.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Created by xbkaishui on 2020/3/31.
 */
public class JsonConfigTest {

    @Test
    public void getJsonMapper() throws Exception {

        ObjectMapper objectMapper = JsonConfig.getJsonMapper();
        Map<String, Object> dataMap = new HashMap();
        dataMap.put("data", LocalDate.now());

        String data = objectMapper.writeValueAsString(dataMap);
        System.out.println(data);

    }

}