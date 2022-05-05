package top.ivan.simple.gateway.core.match;

import top.ivan.simple.gateway.core.UriMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PatternMatchUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Ivan
 * @description
 * @date 2021/1/7
 */
public class SimpleUriMatcher implements UriMatcher {
    private final Predicate<String> condition;

    public SimpleUriMatcher(List<String> matches) {
        if (CollectionUtils.isEmpty(matches)) {
            condition = uri -> true;
            return;
        }
        List<String> matchList = new ArrayList<>();
        List<String> unMatchList = new ArrayList<>();
        for (String match : matches) {
            if (match.startsWith("-")) {
                unMatchList.add(match.substring(1));
            } else {
                matchList.add(match);
            }
        }
        String[] unMatchArray = unMatchList.toArray(new String[0]);
        String[] matchArray = matchList.toArray(new String[0]);

        if (matchList.isEmpty()) {
            if (!unMatchList.isEmpty()) {
                condition = uri -> !PatternMatchUtils.simpleMatch(unMatchArray, uri);
            } else {
                condition = uri -> true;
            }
        } else if (unMatchList.isEmpty()) {
            condition = uri -> PatternMatchUtils.simpleMatch(matchArray, uri);
        } else {
            condition = uri -> !PatternMatchUtils.simpleMatch(unMatchArray, uri) && PatternMatchUtils.simpleMatch(matchArray, uri);
        }
    }

    @Override
    public boolean match(String uri) {
        return condition.test(uri);
    }
}
