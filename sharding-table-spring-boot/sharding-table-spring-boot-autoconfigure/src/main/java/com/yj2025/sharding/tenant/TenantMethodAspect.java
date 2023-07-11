package com.yj2025.sharding.tenant;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.reflect.Method;

@Aspect
public class TenantMethodAspect {
    private ExpressionParser parser = new SpelExpressionParser();
    private StandardReflectionParameterNameDiscoverer discoverer = new StandardReflectionParameterNameDiscoverer();

    private final ApplicationContext applicationContext;

    public TenantMethodAspect(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Pointcut(value = "@annotation(tenant)")
    public void pointcut(TenantThreadLocal tenant) {

    }

    @Around(value = "pointcut(tenant)")
    public Object around(ProceedingJoinPoint pjp, TenantThreadLocal tenant) throws Throwable {
        Method method = this.getInterfaceMethod(pjp);
//        String methodName = method.toString();
        // 获取方法的参数值
        Object[] args = pjp.getArgs();
        EvaluationContext context = this.bindParam(method, args);

        Expression expression = parser.parseExpression(tenant.value(), ParserContext.TEMPLATE_EXPRESSION);
        // 放入租户信息到本地线程
        String tenantId = expression.getValue(context, String.class);
        if (!StringUtils.hasText(tenantId)) {
            throw new AnnotationTypeMismatchException(method, "无法从参数中获取有效的tenantId值");
        }
        TenantThreadLocalHolder.setTenantId(tenantId);
        // return
        return pjp.proceed();
    }

    /**
     * 获取当前接口执行的方法
     *
     * @param pjp
     * @return
     * @throws NoSuchMethodException
     */
    private Method getInterfaceMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method interfaceMethod = methodSignature.getMethod();
//        Method targetImplMethod = pjp.getTarget().getClass().getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());
        return interfaceMethod;
    }


    /**
     * 将方法的参数名和参数值绑定
     *
     * @param method 方法，根据方法获取参数名
     * @param args   方法的参数值
     * @return
     */
    private EvaluationContext bindParam(Method method, Object[] args) {
        //获取方法的参数名
        String[] params = discoverer.getParameterNames(method);

        //将参数名与参数值对应起来
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.addPropertyAccessor(new BeanFactoryAccessor());
        context.setBeanResolver(new BeanFactoryResolver(applicationContext));
        context.setRootObject(applicationContext);

        for (int len = 0; len < params.length; len++) {
            context.setVariable(params[len], args[len]);

        }
        return context;
    }
}
