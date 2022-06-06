package top.ivan.simple.gateway.core.filter.impl;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.vavr.CheckedFunction1;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.ivan.simple.gateway.core.*;
import top.ivan.simple.gateway.core.annotation.FilterId;
import top.ivan.simple.gateway.core.filter.AroundFilter;
import top.ivan.simple.gateway.core.filter.FilterMetadata;
import top.ivan.simple.gateway.core.http.HttpResponse;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Function;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;

/**
 * @author Ivan
 * @since 2022/05/20 15:53
 */
@Slf4j
@FilterId(GTConstant.BULKHEAD_FILTER_NAME)
@Component
public class BulkheadFilter implements AroundFilter {
    /**
     * metadata配置项，该字段类型为{@link String}，用于指定服务分割ID，不配置则拥有独立ID
     */
    public static final String PROPERTIES_BULKHEAD = "bulkhead";

    /**
     * 限制时是否以独立的uri为单位，该字段类型为{@link Boolean},默认值为false（即不区分）
     */
    public static final String PROPERTIES_INDEPENDENT = "independent";

    /**
     * metadata配置项，该字段类型为{@link Integer}，用于指定服务请求最大并发数量，默认值{@code 20}
     */
    public static final String PROPERTIES_MAX_CONCURRENT_CALLS = "maxConcurrentCalls";

    /**
     * metadata配置项，该字段类型为{@link Integer}，单位毫秒，用于指定请求等待访问服务等待时间，默认值{@code 500}
     */
    public static final String PROPERTIES_MAX_WAIT_DURATION = "maxWaitDuration";

    /**
     * 额外配置源（即springboot方式的配置）
     */
    @Autowired
    private BulkheadRegistry registry;

    private static final BadGatewayException BULK_EXCEPTION = new BadGatewayException("系统繁忙!");

    @Override
    @SuppressWarnings("unchecked")
    public final HttpResponse invoke(WebRequest request, RequestInvokeChain chain, FilterMetadata metadata) throws BadRequestException {
        //加载配置
        Bulkhead bh = getBulkhead(request, metadata);

        //创建并发安全调用
        return Try.of(Bulkhead.decorateCheckedSupplier(bh, () -> chain.doChain(request)))
                //超过并发限制抛出BulkheadFullException，对此类异常进行包装
                .mapFailure(Case($(instanceOf(BulkheadFullException.class)), () -> BULK_EXCEPTION))
                //返回结果
                .get();
    }

    /**
     * 获取并发配置
     *
     * @param request
     * @param metadata 元数据，具体见本文件上方参数相关说明
     * @return
     */
    private Bulkhead getBulkhead(WebRequest request, FilterMetadata metadata) {
        boolean independent = metadata.readWithType(PROPERTIES_INDEPENDENT, Boolean.class, t -> Try.of(() -> Boolean.valueOf(t)).getOrElse(false));
        String id;
        if (independent) {
            id = request.getUri();
        } else {
            id = metadata.readWithType(PROPERTIES_BULKHEAD, String.class, t -> Option.of(t).getOrElse(UUID.randomUUID().toString()));
        }

        BulkheadConfig config = metadata.readAs(BulkheadConfig.class,
                () -> BulkheadConfig.custom()
                        .maxConcurrentCalls(tryCast(metadata.get(PROPERTIES_MAX_CONCURRENT_CALLS), Integer::parseInt, 20))
                        .maxWaitDuration(Duration.ofMillis(tryCast(metadata.get(PROPERTIES_MAX_WAIT_DURATION), Integer::parseInt, 500)))
                        .build()
        );
        return registry.bulkhead(id, config);
    }

    private static <T, P> T tryCast(P p, CheckedFunction1<P, T> call, T def) {
        try {
            return call.apply(p);
        } catch (Throwable t) {
            return def;
        }
    }
}
