package top.ivan.simple.gateway.core.converter;

import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.lang.Nullable;

public class ChunkedMessageConverter extends ByteArrayHttpMessageConverter {

    private final InheritableThreadLocal<Boolean> returnLength = new InheritableThreadLocal<>();

    {
        returnLength.set(false);
    }

    @Override
    protected Long getContentLength(byte[] bytes, @Nullable MediaType contentType) {
        if (Boolean.TRUE.equals(returnLength.get())) {
            return (long) bytes.length;
        }
        return null;
    }

    public void setReturnLength(boolean val) {
        returnLength.set(val);
    }

}
