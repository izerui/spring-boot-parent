package com.yj2025.basic.command.component;

import com.yj2025.basic.support.Context;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

public interface BasicComponent {

    /**
     * 获取bean
     *
     * @param beanClass
     * @param <T>
     * @return
     */
    default  <T> T $(Class<T> beanClass) {
        T bean = Context.getBean(beanClass);
        Service annotation = AnnotationUtils.findAnnotation(beanClass, Service.class);
        Assert.isNull(annotation, "cmd命令内部不允许使用@Service注释的bean");
        return bean;
    }
}
