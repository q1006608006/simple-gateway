package top.ivan.simple.gateway.core.tools;

import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

/**
 * @author Ivan
 * @description
 * @date 2021/1/6
 */
public class ThreadLocalCache<T> {
    private final ConcurrentHashMap<Thread, CacheObject> allCachedMap;

    @Data
    private class CacheObject {
        private T object;
        private long createTime;

        public CacheObject(T object, long createTime) {
            this.object = object;
            this.createTime = createTime;
            allCachedMap.put(Thread.currentThread(), CacheObject.this);
        }
    }

    private final Supplier<T> supplier;
    private final long cacheMillions;

    private final ThreadLocal<CacheObject> localHandler;

    private final Consumer<T> releaseHandler;

    public ThreadLocalCache(Supplier<T> supplier, long cacheMillions) {
        this(supplier, null, cacheMillions);
    }

    public ThreadLocalCache(Supplier<T> supplier, Consumer<T> releaseHandler, long cacheMillions) {
        this.supplier = supplier;
        this.cacheMillions = cacheMillions;
        this.localHandler = ThreadLocal.withInitial(() -> new CacheObject(supplier.get(), System.currentTimeMillis()));
        this.allCachedMap = new ConcurrentHashMap<>();
        if (releaseHandler != null) {
            this.releaseHandler = releaseHandler;
        } else {
            this.releaseHandler = t -> {
            };
        }
    }


    public T getObject() {
        CacheObject cacheObject = localHandler.get();
        if (cacheMillions > 0 && System.currentTimeMillis() > (cacheObject.getCreateTime() + cacheMillions)) {
            releaseHandler.accept(cacheObject.getObject());
            cacheObject = new CacheObject(supplier.get(), System.currentTimeMillis());
            localHandler.set(cacheObject);
        }

        return cacheObject.getObject();
    }

    public void releaseLocal() {
        CacheObject object = allCachedMap.get(Thread.currentThread());
        if (null != object) {
            allCachedMap.remove(Thread.currentThread(), object);
            releaseHandler.accept(object.getObject());
        }
        localHandler.remove();
    }

    public void doInAllCached(ObjLongConsumer<T> operator) {
        for (CacheObject value : allCachedMap.values()) {
            operator.accept(value.getObject(), value.getCreateTime());
        }
    }

}
