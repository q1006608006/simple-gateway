package top.ivan.simple.gateway.core.match;

import top.ivan.simple.gateway.core.RequestMatcher;
import top.ivan.simple.gateway.core.WebRequest;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Ivan
 * @description
 * @date 2021/1/8
 */
public class MethodMatcher implements RequestMatcher {

    private static final Set<String> METHOD_SET = new TreeSet<>(Arrays.stream(HttpMethod.values()).map(s -> s.toString().toLowerCase(Locale.ENGLISH)).collect(Collectors.toList()));

    private final Predicate<String> condition;

    public MethodMatcher(List<String> methods) {
        if (CollectionUtils.isEmpty(methods) || methods.size() == 1 && "*".equals(methods.get(0))) {
            condition = s -> true;
        } else {
            Set<String> matchSet = new TreeSet<>();
            if (methods.contains("*") || methods.stream().anyMatch(s -> s.startsWith("-"))) {
                matchSet.addAll(METHOD_SET);
            }
            for (String cond : methods) {
                String method = cond.toLowerCase(Locale.ENGLISH);
                if (method.startsWith("-")) {
                    method = method.substring(1);
                    matchSet.remove(method);
                } else if (METHOD_SET.contains(method)) {
                    matchSet.add(method);
                }
            }
            condition = s -> matchSet.contains(s.toLowerCase(Locale.ENGLISH));
        }
    }

    @Override
    public boolean match(WebRequest request) {
        return condition.test(request.getMethod());
    }
}
