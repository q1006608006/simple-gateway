package top.ivan.simple.gateway.core.http;

import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Ivan
 * @description
 * @date 2020/12/23
 */
@Component
public class HttpInvokerImpl implements HttpInvoker {
    private static final Set<String> ignoreHeaders;

    private static HttpClientConfig conf;

    static {
        ignoreHeaders = new TreeSet<>();
        ignoreHeaders.add("content-length");
        conf = new HttpClientConfig();
        conf.setConnectionTimeout(2000);
        conf.setSoTimeout(2000);
        conf.setMaxTotal(20);
    }

    @Autowired
    public void setConf(HttpClientConfig hcConf) {
        conf = hcConf;
    }

    static class HttpClientHandler {

        static CloseableHttpClient client = initClient();

        static ConnectionCheckTask checkTask;

        static ExecutorService executor;

        static CloseableHttpClient initClient() {
            // 保持连接策略
            ConnectionKeepAliveStrategy strategy = (response, context) -> {
                HeaderElementIterator it =
                        new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext()) {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase("timeout")) {
                        return Long.parseLong(value) * 1000;
                    }
                }
                return 60000; // 如果没有约定，则默认定义时长为60s
            };

            // 创建池
            PoolingHttpClientConnectionManager mgr = new PoolingHttpClientConnectionManager();
            mgr.setMaxTotal(conf.getMaxTotal());
            if (conf.getDefaultMaxPerRoute() > 0) {
                mgr.setDefaultMaxPerRoute(conf.getDefaultMaxPerRoute());
            }
            if (conf.getMaxPerRoute() != null) {
                Map<String, Integer> maxPerRoute = conf.getMaxPerRoute();
                Set<Map.Entry<String, Integer>> entrySet = maxPerRoute.entrySet();
                for (Map.Entry<String, Integer> entry : entrySet) {
                    String host = entry.getKey();
                    Integer max = entry.getValue();
                    mgr.setMaxPerRoute(new HttpRoute(HttpHost.create(host)), max);
                }
            }
            // 创建httpclient
            client = HttpClients.custom().setKeepAliveStrategy(strategy).setConnectionManager(mgr).build();

            //创建线程池
            ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
            bean.setThreadGroupName("HttpClient-Conn-Checker");
            bean.setCorePoolSize(1);
            bean.setMaxPoolSize(1);
            bean.initialize();
            executor = bean.getObject();

            // 创建连接检测线程
            checkTask = new ConnectionCheckTask(mgr);
            executor.execute(checkTask);
            return client;
        }

        static class ConnectionCheckTask implements Runnable {
            private final HttpClientConnectionManager connMgr;
            private volatile boolean shutdown;

            public ConnectionCheckTask(HttpClientConnectionManager connMgr) {
                this.connMgr = connMgr;
            }

            @Override
            public void run() {
                try {
                    while (!shutdown && !Thread.currentThread().isInterrupted()) {
                        synchronized (this) {
                            wait(5000);
                            // Close expired connections
                            connMgr.closeExpiredConnections();
                            // Optionally, close connections
                            // that have been idle longer than 60 sec
                            connMgr.closeIdleConnections(60, TimeUnit.SECONDS);
                        }
                    }
                } catch (InterruptedException ex) {
                    // terminate
                }
            }

            public void shutdown() {
                shutdown = true;
                synchronized (this) {
                    notifyAll();
                }
            }
        }

    }

    private CloseableHttpClient getHttpClient() {
        return HttpClientHandler.client;
    }


    private static RequestConfig defaultRequestConfig(int timeout) {

        return RequestConfig.custom()
                // 数据传输过程中数据包之间间隔的最大时间
                .setSocketTimeout(timeout > 0 ? timeout : conf.getSoTimeout())
                // 连接建立时间，三次握手完成时间
                .setConnectTimeout(conf.getConnectionTimeout())
                // 重点参数
                .setExpectContinueEnabled(true)
                .build();
    }

    private static HttpRequestBase parseBaseRequest(HttpRequest request, RequestConfig conf) {
        HttpHeaders headers = request.getHeaders();
        MediaType srcType = request.getContentType();
        ContentType contentType = null;
        if (null != srcType) {
            contentType = ContentType.create(srcType.getType(), srcType.getCharset());
        }

        HttpRequestBase base = createRequest(request.getUrl(), request.getMethod());

        if (base instanceof HttpEntityEnclosingRequestBase) {
            ((HttpEntityEnclosingRequestBase) base).setEntity(
                    EntityBuilder.create()
                            .setContentType(contentType)
                            .setBinary(request.getBody())
                            .build()
            );
        }

        headers.forEach((h, v) -> {
            if (!ignoreHeaders.contains(h.toLowerCase(Locale.ENGLISH))) {
                base.addHeader(new BasicHeader(h, String.join(",", v)));
            }
        });

        if (null == conf) {
            conf = defaultRequestConfig(0);
        }
        base.setConfig(conf);
        return base;
    }

    @Override
    public HttpResponse invoke(HttpRequest request) throws IOException {
        return invoke(request, 0);
    }

    @Override
    public HttpResponse invoke(HttpRequest request, int timeout) throws IOException {
        return invoke(request, defaultRequestConfig(timeout));
    }

    @Override
    public HttpResponse invoke(HttpRequest request, Object conf) throws IOException {
        if (!(null == conf || conf instanceof RequestConfig)) {
            throw new IllegalArgumentException("required conf type 'org.apache.http.client.config.RequestConfig', but accept: " + conf.getClass());
        }
        CloseableHttpClient client = getHttpClient();
        HttpRequestBase baseRequest = parseBaseRequest(request, (RequestConfig) conf);
        HttpContext ctx = HttpClientContext.create();

        CloseableHttpResponse baseResponse = client.execute(baseRequest, ctx);
        HttpEntity entity = baseResponse.getEntity();

        int code = baseResponse.getStatusLine().getStatusCode();
        HttpHeaders respHeaders = new HttpHeaders();
        for (Header header : baseResponse.getAllHeaders()) {
            respHeaders.add(header.getName(), header.getValue());
        }

        return HttpResponse.build(code, respHeaders, EntityUtils.toByteArray(entity));
    }

    @PreDestroy
    public static void destroy() {
        if (HttpClientHandler.checkTask != null) {
            HttpClientHandler.checkTask.shutdown();
        }
    }

    private static HttpRequestBase createRequest(String url, HttpMethod method) {
        switch (method) {
            case POST:
                return new HttpPost(url);
            case PUT:
                return new HttpPut(url);
            case PATCH:
                return new HttpPatch(url);
            case HEAD:
                return new HttpHead(url);
            case TRACE:
                return new HttpTrace(url);
            case DELETE:
                return new HttpDelete(url);
            case OPTIONS:
                return new HttpOptions(url);
            default:
                return new HttpGet(url);
        }
    }

}
