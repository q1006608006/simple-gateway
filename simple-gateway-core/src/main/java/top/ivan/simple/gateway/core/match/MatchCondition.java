package top.ivan.simple.gateway.core.match;

import lombok.Data;

import java.util.List;

@Data
public class MatchCondition {
    private List<String> urls;
    private List<String> methods;
    private List<String> headers;
}