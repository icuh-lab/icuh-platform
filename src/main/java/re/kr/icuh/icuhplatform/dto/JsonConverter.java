package re.kr.icuh.icuhplatform.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Converter
public class JsonConverter<T> implements AttributeConverter<T, String> {

    protected final ObjectMapper objectMapper;

    public JsonConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String convertToDatabaseColumn(T entityAttribute) {
        if (ObjectUtils.isEmpty(entityAttribute)) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(entityAttribute);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (StringUtils.hasText(dbData)) {
            Class<?> clazz = GenericTypeResolver.resolveTypeArgument(getClass(), JsonConverter.class);
            try {
                return (T) objectMapper.readValue(dbData, clazz); // unchecked exception이 발생할 수 있다는건가..
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
