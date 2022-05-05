package top.ivan.simple.gateway.core.match;

import top.ivan.simple.gateway.core.RequestMatcher;
import top.ivan.simple.gateway.core.WebRequest;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author Ivan
 * @since 2021/08/31 10:04
 */
public class CombineMatcher implements RequestMatcher {

    private final Predicate<WebRequest> condition;

    public CombineMatcher(boolean independent, RequestMatcher... matchers) {
        if (null == matchers || matchers.length == 0) {
            condition = r -> true;
            return;
        }
        if (matchers.length == 1) {
            condition = matchers[0]::match;
            return;
        }
        if (independent) {
            condition = parseIndependent(matchers);
        } else {
            condition = parseNoIndependent(matchers);
        }
    }

    public CombineMatcher(boolean independent, Collection<RequestMatcher> matchers) {
        this(independent, matchers.toArray(new RequestMatcher[0]));
    }


    @Override
    public boolean match(WebRequest request) {
        return condition.test(request);
    }

    public CombineMatcher combine(boolean independent, @NonNull RequestMatcher... matchers) {
        matchers = addToArray(matchers, this);
        return new CombineMatcher(independent, matchers);
    }

    private static <T> T[] addToArray(T[] source, T target) {
        int len = source.length;
        source = Arrays.copyOf(source, len + 1);
        source[len] = target;
        return source;
    }

    private static Predicate<WebRequest> parseIndependent(RequestMatcher[] matchers) {
        return r -> {
            for (RequestMatcher matcher : matchers) {
                if (matcher.match(r)) {
                    return true;
                }
            }
            return false;
        };
    }

    private static Predicate<WebRequest> parseNoIndependent(RequestMatcher[] matchers) {
        return r -> {
            boolean failed = false;
            for (int i = 0; i < matchers.length && !failed; i++) {
                if (!matchers[i].match(r)) {
                    failed = true;
                }
            }
            return !failed;
        };
    }
}
