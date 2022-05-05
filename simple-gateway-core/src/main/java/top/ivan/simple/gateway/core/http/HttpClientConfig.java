package top.ivan.simple.gateway.core.http;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component("redirectConfig")
@ConfigurationProperties(prefix = "httpclient", ignoreInvalidFields = true)
public class HttpClientConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    private int maxTotal = 20;
    private int defaultMaxPerRoute;
    private int soTimeout = 2000;
    private int connectionTimeout = 2000;
    private Map<String,Integer> maxPerRoute;

    public HttpClientConfig() {
    }

    public int getMaxTotal() {
        return this.maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getDefaultMaxPerRoute() {
        return this.defaultMaxPerRoute;
    }

    public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
    }

    public int getSoTimeout() {
        return this.soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Map<String,Integer> getMaxPerRoute() {
        return this.maxPerRoute;
    }

    public void setMaxPerRoute(Map<String,Integer> maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }
}
