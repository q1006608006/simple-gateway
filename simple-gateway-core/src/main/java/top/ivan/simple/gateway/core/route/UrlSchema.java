package top.ivan.simple.gateway.core.route;

import lombok.Data;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ivan
 * @description
 * @date 2020/12/29
 */
@Data
public class UrlSchema {
    private static final String URL_SCHEMA_REGEX = "((.*)://)?([-.\\w:]+)((/[^?]*)?(\\?(.*))?)?";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_SCHEMA_REGEX);
    public static final int URL_REGEX_GROUP_PROTOCOL = 2;
    public static final int URL_REGEX_GROUP_HOST = 3;
    public static final int URL_REGEX_GROUP_PATH = 5;
    public static final int URL_REGEX_GROUP_QUERY_STRING = 7;

    private String id;
    private String protocol;
    private String host;
    private String path;
    private String queryString;
    private MultiValueMap<String, String> properties;

    public MultiValueMap<String, String> getProperties() {
        if (null == properties) {
            properties = new LinkedMultiValueMap<>();
            URLEncodedUtils.parse(queryString, Charset.defaultCharset()).forEach(p -> properties.add(p.getName(), p.getValue()));
        }
        return properties;
    }


    public static UrlSchema parse(String schema) throws URISyntaxException {
        Matcher matcher = URL_PATTERN.matcher(schema);
        if (!matcher.matches()) {
            throw new URISyntaxException(schema, "not supported schema");
        }
        UrlSchema info = new UrlSchema();
        info.setProtocol(matcher.group(URL_REGEX_GROUP_PROTOCOL));
        info.setHost(matcher.group(URL_REGEX_GROUP_HOST));
        info.setPath(matcher.group(URL_REGEX_GROUP_PATH));
        info.setQueryString(matcher.group(URL_REGEX_GROUP_QUERY_STRING));
        return info;
    }


}
