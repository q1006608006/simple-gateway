package top.ivan.simple.gateway.core.tools;

import java.util.function.Supplier;

/**
 * @author Ivan
 * @description
 * @date 2020/6/16
 */
public class SingletonObject<T> {
    private volatile T volObj;

    private T obj;

    private Supplier<T> supplier;

    public SingletonObject(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T getObject() {
        if (supplier != null && null == obj) {
            synchronized (this) {
                if (null == obj) {
                    volObj = supplier.get();
                    obj = volObj;
                    volObj = null;
                    supplier = null;
                }
            }
        }
        return obj;
    }
}
