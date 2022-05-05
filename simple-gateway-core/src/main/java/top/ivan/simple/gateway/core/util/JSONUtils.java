package top.ivan.simple.gateway.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ivan
 * @since 2021/08/13 15:05
 */
public class JSONUtils {
    private static final ObjectMapper mapper;
    public static final JavaType[] EMPTY_TYPES = new JavaType[0];

    static {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        //取消默认转换timestamps形式
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //忽略空Bean转json的错误
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //允许pojo中有在json串中不存在的字段
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //允许有注释
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    private JSONUtils() {
    }

    public static String toString(Object obj) {
        return illegal(() -> mapper.writeValueAsString(obj));
    }

    public static <T> T read(byte[] json, Class<T> type) {
        return illegal(() -> mapper.readValue(json, type));

    }

    public static <T> T read(byte[] json, TypeReference<T> ref) {
        return illegal(() -> mapper.readValue(json, ref));
    }

    public static <T> T read(String json, Class<T> type) {
        return illegal(() -> mapper.readValue(json, type));
    }

    public static <T> T read(String json, TypeReference<T> ref) {
        return illegal(() -> mapper.readValue(json, ref));
    }

    public static <T> T read(String json, JavaType type) {
        return illegal(() -> mapper.readValue(json, type));
    }

    public static <T> T read(byte[] json, JavaType type) {
        return illegal(() -> mapper.readValue(json, type));
    }

    public static <T> T read(String json, JAVAType javaType) {
        JavaType type = javaType.toDeclareType(getMapper().getTypeFactory());
        return read(json, type);
    }

    public static <T> T read(byte[] json, JAVAType javaType) {
        JavaType type = javaType.toDeclareType(getMapper().getTypeFactory());
        return read(json, type);
    }

    public static <T> T read(String json, Class<?> base, Object... elems) {
        return read(json, constructType(base, elems));
    }

    public static <T> T read(byte[] json, Class<?> base, Object... elems) {
        return read(json, constructType(base, elems));
    }

    public static <T> List<T> readList(String json, Class<T> eleType) {
        return read(json, constructType(ArrayList.class, eleType));
    }

    public static <T> List<T> readList(byte[] json, Class<T> eleType) {
        return read(json, constructType(ArrayList.class, eleType));
    }

    public static <K, V> Map<K, V> readMap(String json, Class<K> keyType, Class<V> valType) {
        return read(json, constructType(LinkedHashMap.class, keyType, valType));
    }

    public static <K, V> Map<K, V> readMap(byte[] json, Class<K> keyType, Class<V> valType) {
        return read(json, constructType(LinkedHashMap.class, keyType, valType));
    }

    public static JavaType constructType(Class<?> base, Object... elems) {
        return constructType(getMapper().getTypeFactory(), base, elems);
    }

    public static JavaType constructType(TypeFactory factory, Class<?> base, Object... elements) {
        List<JavaType> types = new ArrayList<>();
        for (Object elem : elements) {
            if (elem instanceof Class) {
                types.add(factory.constructType((Type) elem));
            } else if (elem instanceof TypeReference) {
                types.add(factory.constructType((TypeReference<?>) elem));
            } else if (elem instanceof JavaType) {
                types.add((JavaType) elem);
            } else if (elem instanceof JAVAType) {
                types.add(((JAVAType) elem).toDeclareType(factory));
            } else {
                types.add(factory.constructType(elem.getClass()));
            }
        }
        return factory.constructParametricType(base, types.toArray(EMPTY_TYPES));
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    private static <T> T illegal(CheckedSupplier<T> t) {
        try {
            return t.get();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    interface CheckedSupplier<T> {
        T get() throws Exception;
    }

}
