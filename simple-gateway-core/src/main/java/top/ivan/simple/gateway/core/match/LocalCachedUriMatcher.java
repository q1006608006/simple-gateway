package top.ivan.simple.gateway.core.match;

import top.ivan.simple.gateway.core.GTConstant;
import top.ivan.simple.gateway.core.UriMatcher;
import org.springframework.util.PatternMatchUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * @author Ivan
 * @description
 * @date 2021/1/6
 */
public class LocalCachedUriMatcher implements UriMatcher {

    private final ThreadLocal<LinkedHashMap<String, Boolean>> localMap;

    private final BiPredicate<String, Boolean> cacheAble;
    private final String[] matches;

    public LocalCachedUriMatcher(List<String> list, int cacheMax, BiPredicate<String, Boolean> cacheAble) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("要求至少传入一个匹配项");
        }

        this.matches = list.toArray(new String[0]);
        this.cacheAble = cacheAble == null ? (u, r) -> r : cacheAble;

        localMap = ThreadLocal.withInitial(() -> new LinkedHashMap<String, Boolean>(cacheMax) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > cacheMax;
            }
        });
    }

    @Override
    public boolean match(String uri) {
        Map<String, Boolean> cacheMap = localMap.get();
        Boolean result = cacheMap.get(uri);
        if (result != null) {
            return result;
        }
        result = PatternMatchUtils.simpleMatch(matches, uri);
        if (cacheAble.test(uri, result)) {
            cacheMap.put(uri, result);
        }
        return result;
    }

    public static LocalCachedUriMatcher successCacheMatcher(List<String> list, int cacheMax) {
        return new LocalCachedUriMatcher(list, cacheMax, null);
    }

    public static LocalCachedUriMatcher successCacheMatcher(List<String> list) {
        return new LocalCachedUriMatcher(list, GTConstant.DEFAULT_URL_CACHE_MAX, null);
    }

    public static LocalCachedUriMatcher allCacheMatcher(List<String> list, int cacheMax) {
        return new LocalCachedUriMatcher(list, cacheMax, (u, r) -> true);
    }

    public static LocalCachedUriMatcher allCacheMatcher(List<String> list) {
        return new LocalCachedUriMatcher(list, GTConstant.DEFAULT_URL_CACHE_MAX, (u, r) -> true);
    }
}
