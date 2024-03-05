package com.yj2025.jdbc.dialect.flag;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Slf4j
public class QueryFlagMethodAspect {

    private ExpressionParser parser = new SpelExpressionParser();
    private StandardReflectionParameterNameDiscoverer discoverer = new StandardReflectionParameterNameDiscoverer();


    private final ApplicationContext applicationContext;

    public QueryFlagMethodAspect(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Pointcut(value = "@annotation(queryFlag)")
    public void pointcut01(QueryFlagAfterTable queryFlag) {

    }

    @Pointcut(value = "@annotation(queryFlags)")
    public void pointcut02(QueryFlagAfterTables queryFlags) {

    }

    @Around(value = "pointcut01(queryFlag)")
    public Object around01(ProceedingJoinPoint pjp, QueryFlagAfterTable queryFlag) throws Throwable {
        Method method = this.getInterfaceMethod(pjp);
//        String methodName = method.toString();
        // 获取方法的参数值
        Object[] args = pjp.getArgs();
        EvaluationContext context = this.bindParam(method, args);
        // 放入租户信息到本地线程
        String value = parser.parseExpression(queryFlag.value(), ParserContext.TEMPLATE_EXPRESSION).getValue(context, String.class);
        QueryFlagThreadLocalHolder.setQueryFlags(Lists.newArrayList(
                new QueryFlag(queryFlag.tablePrefix(), queryFlag.isComment(), value)
        ));
        // return
        return pjp.proceed();
    }

    @Around(value = "pointcut02(queryFlags)")
    public Object around02(ProceedingJoinPoint pjp, QueryFlagAfterTables queryFlags) throws Throwable {
        Method method = this.getInterfaceMethod(pjp);
//        String methodName = method.toString();
        // 获取方法的参数值
        Object[] args = pjp.getArgs();
        EvaluationContext context = this.bindParam(method, args);
        List<QueryFlag> list = new ArrayList<>();
        for (QueryFlagAfterTable queryFlag : queryFlags.value()) {
            // 放入租户信息到本地线程
            String value = parser.parseExpression(queryFlag.value(), ParserContext.TEMPLATE_EXPRESSION).getValue(context, String.class);
            list.add(new QueryFlag(queryFlag.tablePrefix(), queryFlag.isComment(), value));
        }
        list.sort((o1, o2) -> o2.getTablePrefix().compareTo(o1.getTablePrefix()));
        QueryFlagThreadLocalHolder.setQueryFlags(list);
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
