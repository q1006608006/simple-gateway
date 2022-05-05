package top.ivan.simple.gateway.core.match;

import top.ivan.simple.gateway.core.RequestMatcher;
import top.ivan.simple.gateway.core.WebRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Ivan
 * @description
 * @date 2021/1/8
 */
public class HeadersMatcher implements RequestMatcher {
    private final List<Predicate<HttpServletRequest>> testList;


    public HeadersMatcher(List<String> conditions) {
        this.testList = new ArrayList<>();
        if (CollectionUtils.isEmpty(conditions)) {
            return;
        }
        for (String condition : conditions) {
            boolean reverse = false;
            String head;
            String value = null;
            int pos;
            if ((pos = condition.indexOf("!=")) > -1) {
                reverse = true;
                head = condition.substring(0, pos);
                value = condition.substring(pos + 2);
            } else if ((pos = condition.indexOf('=')) > -1) {
                head = condition.substring(0, pos);
                value = condition.substring(pos + 1);
            } else {
                head = condition;
            }
            testList.add(parse(head, value, reverse));
        }
    }

    @Override
    public boolean match(WebRequest request) {
        for (Predicate<HttpServletRequest> predicate : testList) {
            if (!predicate.test(request.getContext().getHttpRequest())) {
                return false;
            }
        }
        return true;
    }

    private Predicate<HttpServletRequest> parse(String head, String value, boolean reverse) {
        if (!StringUtils.hasLength(value)) {
            return req -> req.getHeader(head) != null;
        }
        return req -> {
            Enumeration<String> values = req.getHeaders(head);
            boolean matchResult = false;
            while (values.hasMoreElements()) {
                String val = values.nextElement();
                if (PatternMatchUtils.simpleMatch(value, val)) {
                    matchResult = true;
                    break;
                }
            }
            return reverse != matchResult;
        };
    }

}
