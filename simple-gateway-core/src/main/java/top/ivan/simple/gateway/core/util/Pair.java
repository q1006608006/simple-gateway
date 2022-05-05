package top.ivan.simple.gateway.core.util;

import lombok.Data;

/**
 * @author Ivan
 * @since 2022/03/31 18:30
 */
@Data
public class Pair<K, V> {

    private K key;
    private V value;

    public Pair(K k, V v) {
        this.key = k;
        this.value = v;
    }
}
