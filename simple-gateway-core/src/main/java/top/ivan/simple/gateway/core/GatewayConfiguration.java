package top.ivan.simple.gateway.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.ivan.simple.gateway.core.annotation.Matcher;
import top.ivan.simple.gateway.core.annotation.SelectorId;
import top.ivan.simple.gateway.core.controller.DefaultExecuteController;
import top.ivan.simple.gateway.core.converter.ChunkedMessageConverter;
import top.ivan.simple.gateway.core.http.HttpInvoker;
import top.ivan.simple.gateway.core.http.HttpInvokerImpl;
import top.ivan.simple.gateway.core.match.SimpleUriMatcher;
import top.ivan.simple.gateway.core.route.SimpleRouteManager;
import top.ivan.simple.gateway.core.selector.impl.UnavailableRequestSelector;
import top.ivan.simple.gateway.core.tools.RedirectUtils;

import java.util.List;

/**
 * @author Ivan
 * @description 网关自动化配置
 * @date 2020/5/20
 */
@Configuration
@ComponentScan(basePackageClasses = GatewayConfiguration.class)
@ConditionalOnProperty(name = GTConstant.GATEWAY_ENABLE, havingValue = "true")
public class GatewayConfiguration implements WebMvcConfigurer {
    public static final String GLOBAL_MATCHER = "gatewayGlobalMatcher";

    @Bean
    public WebMvcRegistrations getWebMvcRegistrations() {
        return new WebMvcRegistrations() {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new GatewayHandlerMapping();
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(GlobalExecuteController.class)
    public GlobalExecuteController getGlobalExecuteController() {
        return new DefaultExecuteController();
    }

    @Bean("noneSelector")
    @ConditionalOnMissingBean(RequestSelector.class)
    public RequestSelector getDefaultSelector() {
        return body -> null;
    }

    @ConditionalOnMissingBean(UriMatcher.class)
    @Bean(GLOBAL_MATCHER)
    public UriMatcher getUriMatcher(GatewayProperties prop) {
        List<String> matchList = prop.getMatches();
        if (CollectionUtils.isEmpty(matchList)) {
            return str -> true;
        }
        return new SimpleUriMatcher(matchList);
    }

    @ConditionalOnMissingBean(GlobalExecuteAdvice.class)
    @Bean
    public GlobalExecuteAdvice getGlobalExecuteAdvice() {
        return new GlobalExecuteAdvice() {
        };
    }

    @ConditionalOnMissingBean(GatewayConfigurer.class)
    @Bean
    public GatewayConfigurer getGatewayConfigurer() {
        return new GatewayConfigurer() {
        };
    }

    @ConditionalOnMissingBean(HttpInvoker.class)
    @Bean
    public HttpInvoker getHttpInvoker() {
        return new HttpInvokerImpl();
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, getChunkedMessageConverter());
    }

    @Bean
    public ChunkedMessageConverter getChunkedMessageConverter() {
        return new ChunkedMessageConverter();
    }

    @ConditionalOnMissingBean(RouteManager.class)
    @Bean(GTConstant.DEFAULT_ROUTE_MANAGER_ID)
    public RouteManager getRouteManager() {
        return new SimpleRouteManager();
    }

    @Bean
    public RedirectUtils getRedirectUtils() {
        return new RedirectUtils();
    }

    @SelectorId(GTConstant.UNAVAILABLE_SELECTOR)
    @Matcher(urls = "/*")
    @Bean
    public UnavailableRequestSelector getUnavailableSelector() {
        return new UnavailableRequestSelector();
    }

}
