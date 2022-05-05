package top.ivan.simple.gateway.core.util;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Type;

public class TypeDef<T> extends TypeReference<T> {
    private final Type type;

    public TypeDef(Class<T> typeClass) {
        super();
        this.type = typeClass;
    }

    @Override
    public Type getType() {
        return type;
    }
}
