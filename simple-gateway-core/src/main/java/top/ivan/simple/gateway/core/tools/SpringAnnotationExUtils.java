package top.ivan.simple.gateway.core.tools;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ivan
 * @description
 * @date 2021/1/5
 */
public class SpringAnnotationExUtils {
    private SpringAnnotationExUtils() {
    }

    private static final String ANNOTATION_FIELD_VALUE = "value";

    @Nullable
    public static AnnotationAttributes getAnnotationAttributesOnMethod(Class<? extends Annotation> annotationClass, ConfigurableApplicationContext context, String beanName) {
        String annoClazzName = annotationClass.getName();
        BeanDefinition bd = context.getBeanFactory().getBeanDefinition(beanName);
        if (bd instanceof AnnotatedBeanDefinition) {
            Set<MethodMetadata> dataSet = ((AnnotatedBeanDefinition) bd).getMetadata().getAnnotatedMethods(annoClazzName);
            if (!dataSet.isEmpty()) {
                return (AnnotationAttributes) dataSet.stream().findFirst().orElseThrow(IllegalAccessError::new).getAnnotationAttributes(annoClazzName);
            }
        }
        return null;
    }

    @Nullable
    public static <T> T getAnnotationValueOnMethod(Class<? extends Annotation> annotationClass, String field, ConfigurableApplicationContext context, String beanName) {
        AnnotationAttributes attr = getAnnotationAttributesOnMethod(annotationClass, context, beanName);
        if (null != attr) {
            return (T) attr.get(field);
        }
        return null;
    }

    @NonNull
    public static <T> List<T> getAnnotationValuesOnMethod(Class<? extends Annotation> annotationClass, String field, ConfigurableApplicationContext context, String beanName) {
        AnnotationAttributes attr = getAnnotationAttributesOnMethod(annotationClass, context, beanName);
        List<T> results = new ArrayList<>();
        if (null != attr) {
            Object target = attr.get(field);
            if (target instanceof Object[]) {
                T[] vas = (T[]) target;
                Collections.addAll(results, vas);
            } else {
                results.add((T) target);
            }
        }
        return results;
    }

    @Nullable
    public static AnnotationAttributes findAnnotationAttributes(Class<? extends Annotation> annotationClass, Object target, ApplicationContext context, String beanName) {
        if (null != context && context.containsBean(beanName) && context instanceof ConfigurableApplicationContext) {
            AnnotationAttributes attributes = getAnnotationAttributesOnMethod(annotationClass, (ConfigurableApplicationContext) context, beanName);
            if (null != attributes) {
                return attributes;
            }
        }

        Annotation annotation = AnnotationUtils.findAnnotation(target.getClass(), annotationClass);
        if (null != annotation) {
            return AnnotationUtils.getAnnotationAttributes(annotation, false, true);
        }

        return null;
    }

    @Nullable
    public static <T> T findAnnotationValue(Class<? extends Annotation> annotationClass, String field, Object target, ApplicationContext context, String beanName, T defaultValue) {
        T result;
        Annotation annotation;
        if (null != context && context.containsBean(beanName)) {
            if (context instanceof ConfigurableApplicationContext && (result = getAnnotationValueOnMethod(annotationClass, field, (ConfigurableApplicationContext) context, beanName)) != null) {
                return result;
            }
            annotation = context.findAnnotationOnBean(beanName, annotationClass);
        } else {
            annotation = AnnotationUtils.findAnnotation(target.getClass(), annotationClass);
        }
        result = getAnnotationField(annotation, field);
        return null == result ? defaultValue : result;
    }

    @NonNull
    public static <T> List<T> findAnnotationValues(Class<? extends Annotation> annotationClass, String field, Object target, ApplicationContext context, String beanName) {
        List<T> results;
        T[] result;
        Annotation annotation;
        if (null != context && context.containsBean(beanName)) {
            if (context instanceof ConfigurableApplicationContext) {
                results = getAnnotationValuesOnMethod(annotationClass, field, (ConfigurableApplicationContext) context, beanName);
                if (!results.isEmpty()) {
                    return results;
                }
            }
            annotation = context.findAnnotationOnBean(beanName, annotationClass);
        } else {
            annotation = AnnotationUtils.findAnnotation(target.getClass(), annotationClass);
        }

        results = new ArrayList<>();
        if ((result = getAnnotationField(annotation, field)) != null) {
            results.addAll(Arrays.asList(result));
        }
        return results;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static List<AnnotationAttributes> findRepeatAnnotationAttributes(Class<? extends Annotation> repeatTarget, Object target, ApplicationContext context, String beanName) {
        List<AnnotationAttributes> results;
        Annotation annotation;
        if (null != context && context.containsBean(beanName) && context instanceof ConfigurableApplicationContext) {
            results = getAnnotationValuesOnMethod(repeatTarget, ANNOTATION_FIELD_VALUE, (ConfigurableApplicationContext) context, beanName);
            if (!results.isEmpty()) {
                return results;
            }
        }

        annotation = AnnotationUtils.findAnnotation(target.getClass(), repeatTarget);
        Annotation[] result = getAnnotationField(annotation, ANNOTATION_FIELD_VALUE);
        if (null != result) {
            return Arrays.stream(result).map(a -> AnnotationUtils.getAnnotationAttributes(a, false, true)).collect(Collectors.toList());
        }

        Method method = ReflectionUtils.findMethod(repeatTarget, ANNOTATION_FIELD_VALUE);
        return Optional.ofNullable(method)
                .map(Method::getReturnType)
                .map(Class::getComponentType)
                .map(type -> findAnnotationAttributes((Class<? extends Annotation>) type, target, context, beanName))
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getAnnotationField(Annotation a, String field) {
        if (a == null) {
            return null;
        }
        Method m = ReflectionUtils.findMethod(a.getClass(), field);
        if (null != m) {
            return (T) ReflectionUtils.invokeMethod(m, a);
        }
        return null;
    }

}
