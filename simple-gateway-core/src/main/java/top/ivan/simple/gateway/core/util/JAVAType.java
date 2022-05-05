package top.ivan.simple.gateway.core.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * @author Ivan
 * @since 2021/10/20 17:26
 */
public class JAVAType {
    private final Class<?> baseType;
    private final Object[] elements;

    public JAVAType(Class<?> baseType, Object... elements) {
        this.baseType = baseType;
        this.elements = elements;
    }

    public Class<?> getBaseType() {
        return baseType;
    }

    public Object[] getElements() {
        return elements;
    }

    public JavaType toDeclareType(TypeFactory factory) {
        return JSONUtils.constructType(factory, baseType, elements);
    }
}
