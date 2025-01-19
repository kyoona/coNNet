package houseInception.connet.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import houseInception.connet.exception.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectMapperUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(ObjectMapperUtil.class);

    public static <T> T parseJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            log.error("ObjectMapperUtil.ObjectMapperUtil 오류 발생 : {}를 파싱할 수 없습니다.", json);
            throw new SerializationException(json);
        }
    }

    public static String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new SerializationException(value.getClass().getName());
        }
    }
}
