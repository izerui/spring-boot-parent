# spring 相关的自定义扩展机制 spi

## 修改spring的已加载的bean定义 参考： PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors
两种方式： 

方式一：（进行bean定义替换，因为是按名字替换，故对其他bean定义的影响为0）

```java
@Bean
@Bean
public BeanDefinitionRegistryCustomizer originalBeanCustomizer() {
    return (registry, applicationContext) -> {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(CustomBean.class);
        beanDefinitionBuilder.setInitMethodName("init");
        registry.removeBeanDefinition("originalBean");
        registry.registerBeanDefinition("originalBean", beanDefinitionBuilder.getBeanDefinition());
    };
}
```

方式二：(该扩展可以修改已经加载的bean定义列表，按自身需求指定相应的bean进行处理，需要注意修饰的bean定义的范围。否则可能导致相应的bean都被重新修饰。)

```java
@Bean
public static BeanDefinitionCustomizer customizer() {
    return bd -> {
        if (OriginalBean.class.getName().equals(bd.getBeanClassName())) {
            bd.setBeanClassName(CustomBean.class.getName());
            bd.setInitMethodName("init");
        }
    };
}
```

> 以上两种方式都支持在bean初始化之前修改bean的定义，并按照修改后的bean定义执行初始化动作，
> 需要注意的是，因为该扩展机制是在spring bean工厂初始化之前，故尽量避免定义使用依赖注入，或者声明bean方法为`static`
