package top.ivan.simple.gateway.core.selector;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import top.ivan.simple.gateway.core.*;
import top.ivan.simple.gateway.core.annotation.Matchers;
import top.ivan.simple.gateway.core.annotation.SelectorId;
import top.ivan.simple.gateway.core.match.CombineMatcher;
import top.ivan.simple.gateway.core.match.ConditionsMatcher;
import top.ivan.simple.gateway.core.match.MatchCondition;
import top.ivan.simple.gateway.core.tools.SpringAnnotationExUtils;
import top.ivan.simple.gateway.core.util.PropertiesUtil;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ivan on 2019/11/19.
 * @version 1.0
 * 自动selector配置注入配置
 */
@Slf4j
@Component
public class AutoSelectorConfig {
    public static final String BASE_PATH = GTConstant.GATEWAY_PROPERTIES_PREFIX;

    @Getter
    private List<SelectorContext> selectorOrderList;

    private SelectorFactory selectorFactory;
    private Environment env;
    private ConfigurableApplicationContext appContext;
    private GatewayProperties gatewayProperties;
    private GatewayConfigurer gatewayConfigurer;
    private Map<String, RequestSelector> selectorMap;

    @Autowired
    public void autoWiredFields(SelectorFactory factory, GatewayProperties gatewayProperties, Map<String, RequestSelector> selectorMap, GatewayConfigurer configurer, Environment env, ConfigurableApplicationContext context) {
        this.selectorFactory = factory;
        this.env = env;
        this.appContext = context;
        this.gatewayProperties = gatewayProperties;
        this.gatewayConfigurer = configurer;
        this.selectorMap = selectorMap;
    }

    @PostConstruct
    private void init() {
        //获取映射列表
        Map<String, SelectorContext> ctxMap = initSelectorContextMap(appContext, selectorMap);

        //读取文件配置（application.yml中)
        Map<String, SelectorMetadata> confMap = gatewayProperties.getSelector();
        if (null == confMap) {
            //用个空的map初始化
            confMap = Collections.emptyMap();
        }

        selectorOrderList = new ArrayList<>();

        //加载Selector的上下文
        confMap.forEach((selectorId, conf) -> {
            //获得关联Selector基本信息
            SelectorContext ctx = getCombinedContext(selectorId, ctxMap);
            ctx.setMetadata(conf);
            selectorOrderList.add(ctx);

            log.debug("finish Selector(id:{}) auto-config: {}", selectorId, ctx);
        });

        //外部配置入口
        gatewayConfigurer.configureSelectors(selectorOrderList);

        //完成Selector的载入
        for (SelectorContext ctx : selectorOrderList) {
            if (ctx.getSelector() instanceof RequestSelector.ConfigAware) {
                injectConfig(ctx);
            }
            initSelectorContext(ctx);
        }
    }

    private void initSelectorContext(SelectorContext ctx) {
        SelectorMetadata conf = ctx.getMetadata();
        // 初始化Selector上下文
        RequestMatcher matcher = parserMatcher(appContext, conf, ctx);
        ctx.setMatcher(matcher);
        ctx.setRouteId(conf.getRouteId());
    }

    private void injectConfig(SelectorContext context) {
        String selectorId = context.getSelectorId();
        RequestSelector selector = context.getSelector();
        String configPath = getSelectorBinderPath(selectorId).toLowerCase();


        try {
            //配置的路径
            Class<? extends SelectorMetadata> dataType = ((RequestSelector.ConfigAware<? extends SelectorMetadata>) selector).getConfigType();

            SelectorMetadata conf = null;
            if (context.getMetadata().isPatch()) {
                conf = PropertiesUtil.loadResource(gatewayProperties.getConfigLocation(), selectorId, dataType);
            }

            if (null == conf) {
                Binder binder = Binder.get(env);
                conf = binder.bind(configPath, Bindable.of(dataType)).get();
                //继承根配置
                conf.extend(context.getMetadata());
            }

            //注入配置
            Method setConfMethod = ReflectionUtils.findMethod(selector.getClass(), "setConfig", dataType);
            if (null == setConfMethod) {
                throw new NoSuchMethodException("not found: " + selector.getClass().getName() + ".setConfig(" + dataType.getName() + ")");
            }
            setConfMethod.invoke(selector, conf);

            context.setMetadata(conf);
            log.debug("resolved Selector(id:{}) config-aware: {}", selectorId, dataType);
        } catch (Exception e) {
            throw new RuntimeException("set config for '" + configPath + "' error", e);
        }
    }

    private Map<String, SelectorContext> initSelectorContextMap(ApplicationContext context, Map<String, RequestSelector> originMap) {
        Map<String, SelectorContext> ctxMap = new HashMap<>();
        //收集RequestSelector详细信息
        for (Map.Entry<String, RequestSelector> entry : originMap.entrySet()) {
            String beanName = entry.getKey();
            RequestSelector selector = entry.getValue();

            String selectorId = getSelectorId(context, beanName, selector);

            SelectorContext ctx = new SelectorContext();
            ctx.setBeanName(beanName);
            ctx.setSelectorId(selectorId);
            ctx.setSelector(selector);

            ctxMap.put(beanName, ctx);
            ctxMap.put(selectorId, ctx);
        }

        return ctxMap;
    }

    private SelectorContext getCombinedContext(String selectorId, Map<String, SelectorContext> originMap) {
        SelectorContext ctx = originMap.get(selectorId);
        if (ctx == null) {
            RequestSelector selector = getNameTypeSelector(selectorId);
            ctx = new SelectorContext();
            ctx.setSelectorId(selectorId);
            ctx.setBeanName(selectorId);
            ctx.setSelector(selector);
            originMap.put(selectorId, ctx);

            log.debug("bind Selector(id:{}) with '{}'", selectorId, selector);
        }
        return ctx;
    }

    private RequestSelector getNameTypeSelector(String selectorName) {
        return selectorFactory.resolve(selectorName);
    }

    private RequestMatcher parserMatcher(ApplicationContext appContext, SelectorMetadata metadata, SelectorContext context) {
        List<MatchCondition> conditions = metadata.getMatches();
        if (CollectionUtils.isEmpty(conditions)) {
            List<AnnotationAttributes> matchers = getMatchersValue(context, appContext);
            if (CollectionUtils.isEmpty(matchers)) {
                return RequestMatcher.allPermit();
            }
            conditions = new ArrayList<>();
            for (AnnotationAttributes attr : matchers) {
                MatchCondition cond = new MatchCondition();
                cond.setUrls(Arrays.asList(attr.getStringArray("urls")));
                cond.setHeaders(Arrays.asList(attr.getStringArray("headers")));
                cond.setMethods(Arrays.asList(attr.getStringArray("methods")));
                conditions.add(cond);
            }
        }

        List<RequestMatcher> matchers = conditions.stream().map(ConditionsMatcher::new).collect(Collectors.toList());

        return new CombineMatcher(true, matchers);
    }

    private static String getSelectorId(ApplicationContext context, String bn, RequestSelector selector) {
        return SpringAnnotationExUtils.findAnnotationValue(SelectorId.class, "value", selector, context, bn, bn);
    }

    private static List<AnnotationAttributes> getMatchersValue(SelectorContext ctx, ApplicationContext appContext) {
        return SpringAnnotationExUtils.findRepeatAnnotationAttributes(Matchers.class, ctx.getSelector(), appContext, ctx.getBeanName());
    }

    private static String getSelectorBinderPath(String selectorName) {
        return BASE_PATH + ".selector." + selectorName;
    }

}
