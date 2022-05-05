package top.ivan.simple.gateway.core.match;

import top.ivan.simple.gateway.core.RequestMatcher;
import top.ivan.simple.gateway.core.UriMatcher;
import top.ivan.simple.gateway.core.WebRequest;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author Ivan
 * @description
 * @date 2021/1/8
 */
public class ConditionsMatcher implements RequestMatcher {

    private final Predicate<WebRequest> condition;

    public ConditionsMatcher(List<String> uriMatches, List<String> methodMatches, List<String> headerMatches) {
        UriMatcher uriMatcher = new SimpleUriMatcher(uriMatches);
        MethodMatcher methodMatcher = new MethodMatcher(methodMatches);
        HeadersMatcher headersMatcher = new HeadersMatcher(headerMatches);

        CombineMatcher combine = new CombineMatcher(false, new UriRequestMatcher(uriMatcher), methodMatcher, headersMatcher);
        condition = combine::match;
    }

    public ConditionsMatcher(MatchCondition condition) {
        UriMatcher uriMatcher = new SimpleUriMatcher(condition.getUrls());
        UriRequestMatcher uriReqMatcher = new UriRequestMatcher(uriMatcher);
        MethodMatcher methodMatcher = new MethodMatcher(condition.getMethods());
        HeadersMatcher headersMatcher = new HeadersMatcher(condition.getHeaders());
        CombineMatcher combine = new CombineMatcher(false, uriReqMatcher, methodMatcher, headersMatcher);

        this.condition = combine::match;
    }


    @Override
    public boolean match(WebRequest request) {
        return condition.test(request);
    }

}
