package top.ivan.simple.gateway.core.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.lang.NonNull;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ivan
 * @description
 * @date 2021/1/8
 */
public class FilterMetadata {
    private static final Object NULL_CACHE = new Object();

    private static final int MAX_TYPE_CACHE = 4096;
    private static final Map<Object, String> typeMap = new LinkedHashMap<Object, String>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Object, String> eldest) {
            return size() > MAX_TYPE_CACHE;
        }
    };

    private final Map<String, String> metadata;

    private final Map<String, Object> convertMap;


    public FilterMetadata(@NonNull Map<String, String> metadata) {
        this.metadata = metadata;
        this.convertMap = new HashMap<>();
    }

    public String get(String key) {
        return metadata.get(key);
    }

    public String getOrDefault(String key, String defVal) {
        return metadata.getOrDefault(key, defVal);
    }

    public Map<String, String> toMap() {
        return new HashMap<>(metadata);
    }

    @NonNull
    public Map<String, String> getMap(String key) {
        String prefix = key + ".";
        int prefixLen = prefix.length();
        Map<String, String> result = new HashMap<>();
        metadata.forEach((k, v) -> {
            if (k.startsWith(prefix))
                result.put(k.substring(prefixLen), v);
        });

        return result;
    }

    @NonNull
    public List<String> getList(String key) {
        int pos = 0;
        List<String> results = new ArrayList<>();
        while (true) {
            String orderKey = buildPath(key, String.valueOf(pos++));
            if (metadata.containsKey(orderKey)) {
                results.add(metadata.get(orderKey));
            } else {
                break;
            }
        }
        if (results.isEmpty()) {
            String val = metadata.get(key);
            if (null != val) {
                results.add(val);
            }
        }
        return results;
    }

    public <T> T readWithType(String key, Class<T> type, Function<String, T> ifAbsent) {
        return readWithType(key, type, () -> ifAbsent.apply(get(key)));
    }

    public <T> T readFromList(String key, Class<T> type, Function<List<String>, T> ifAbsent) {
        return readWithType(key, type, () -> ifAbsent.apply(getList(key)));
    }

    public <T> T readFromMap(String key, Class<T> type, Function<Map<String, String>, T> ifAbsent) {
        return readWithType(key, type, () -> ifAbsent.apply(getMap(key)));
    }

    @SuppressWarnings("unchecked")
    public <T> T readAs(Class<T> type, Supplier<T> supplier) {
        String key = getCanonicalName("$", type);
        return (T) convertMap.computeIfAbsent(key, k -> supplier.get());
    }

    @SuppressWarnings("unchecked")
    public <T> T readWithType(String key, TypeReference<T> ref, Supplier<T> supplier) {
        String cn = getCanonicalName(key, ref);
        Object result = convertMap.computeIfAbsent(cn, k -> Optional.ofNullable((Object) supplier.get()).orElse(NULL_CACHE));
        return NULL_CACHE == result ? null : (T) result;
    }


    @SuppressWarnings("unchecked")
    private <T> T readWithType(String key, Class<T> type, Supplier<T> supplier) {
        String cn = getCanonicalName(key, type);
        Object result = convertMap.computeIfAbsent(cn, k -> Optional.ofNullable((Object) supplier.get()).orElse(NULL_CACHE));
        return NULL_CACHE == result ? null : (T) result;
    }

    private String getCanonicalName(String key, Object type) {
        String tn = typeMap.computeIfAbsent(type, k -> {
            if (type instanceof Class) {
                return ((Class<?>) type).getCanonicalName();
            } else if (type instanceof TypeReference) {
                return ((TypeReference<?>) type).getType().getTypeName();
            } else if (type instanceof Type) {
                return ((Type) type).getTypeName();
            } else {
                return type.getClass().getCanonicalName();
            }
        });
        return String.join("-", key, tn);
    }

    public String buildPath(String... fields) {
        return String.join(".", fields);
    }

}
