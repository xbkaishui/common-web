package cn.gniic.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author xbkaishui
 * @version $Id: JsonConfig.java,  2019-07-31 2:25 PM xbkaishui Exp $$
 */
public class JsonConfig {

    /**
     * local date序列化成timestamp,时分秒取12:00
     */
    public static class LocalDateSerializer extends JsonSerializer<LocalDate> {

        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            //add your custom date parser
            gen.writeNumber(
                    value.atTime(LocalTime.NOON).atZone(ZoneId.systemDefault()).toInstant()
                            .toEpochMilli());
        }
    }

    public static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            Date date = new Date();
            date.setTime(Long.parseLong(p.getText()));
            Instant instant = date.toInstant();
            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
            return localDateTime.toLocalDate();
        }
    }

    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen,
                SerializerProvider serializers) throws IOException {
            //add your custom date parser
            gen.writeNumber(
                    value.atZone(ZoneId.systemDefault()).toInstant()
                            .toEpochMilli());
        }
    }

    /**
     * 序列化datetime 为固定的中午12点0分0秒
     */
    public static class LocalDateTimeWithFixHourSerializer extends JsonSerializer<LocalDateTime> {

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen,
                SerializerProvider serializers) throws IOException {
            //add your custom date parser
            gen.writeNumber(
                    value.withHour(12).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli());
        }
    }


    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            Date date = new Date();
            date.setTime(Long.parseLong(p.getText()));
            Instant instant = date.toInstant();
            ZoneId zone = ZoneId.systemDefault();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
            return localDateTime;
        }
    }

    public static ObjectMapper getJsonMapper() {
        ObjectMapper objectMapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        simpleModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        simpleModule
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

}
