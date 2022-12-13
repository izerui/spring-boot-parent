package com.yj2025.validator;

import com.google.common.collect.Lists;
import com.yj2025.validator.parser.Field;
import com.yj2025.validator.parser.Form;
import com.yj2025.validator.parser.ValidatorExec;
import com.yj2025.validator.validation.*;
import org.springframework.util.Assert;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by serv on 2016/12/3.
 */
public class ValidatorContextImpl implements ValidatorContext {

    private final static List<Validator<?>> DEFAULT_VALIDATORS = Lists.newArrayList(
            new AfterNowValidator(),
            new BeforeNowValidator(),
            new BlankValidator(),
            new CreditCardValidator(),
            new DatePatternFormatValidator(),
            new EmailValidator(),
            new MaxDoubleValidator(),
            new MaxFloatValidator(),
            new MaxIntValidator(),
            new MaxLongValidator(),
            new MinDoubleValidator(),
            new MinFloatValidator(),
            new MinIntValidator(),
            new MinLongValidator(),
            new NotBlankValidator(),
            new NotNullValidator(),
            new NullValidator(),
            new RangeDoubleValidator(),
            new RangeFloatValidator(),
            new RangeIntValidator(),
            new RangeLongValidator(),
            new RangeShortValidator(),
            new RegexpValidator(),
            new UrlValidator()
    );
    private List<Form> forms;

    public ValidatorContextImpl(List<Form> forms) {
        this.forms = forms;
    }

    /**
     * 添加新的form
     *
     * @param form
     */
    public void addForm(Form form) {
        forms.add(form);
    }

    //根据验证表单名字查找表单
    protected Form getForm(String name) throws ValidatorException {
        if (forms == null) {
            throw new ValidatorException("没有配置验证表单");
        }
        Optional<Form> any = forms.stream().filter(form -> form.getName().equals(name)).findAny();
        if (any.isPresent()) {
            return any.get();
        }
        throw new ValidatorException("未找到名字为 [" + name + "] 的验证表单");
    }


    @Override
    public Results validate(String form, Object obj, Validator<?>... customizeValidators) {

        Assert.notNull(obj);

        Form validationForm = getForm(form);

        List<Result> resultList = new ArrayList<>();

        List<Validator<?>> validators = Lists.newArrayList(customizeValidators);
        validators.addAll(DEFAULT_VALIDATORS);

        for (Field field : validationForm.getFields()) {
            Result result = null;


            //初始化property
            Property property = getProperty(obj, field.getName(), validationForm.getName());

            exec:
            for (ValidatorExec exec : field.getValidators()) {

                //放入变量
                property.setVar(exec.getVar());

                //查找对应验证器名字的多个验证器
                List<Validator> execs = validators.stream().filter(validator -> validator.name().equals(exec.getName())).collect(Collectors.toList());
                //找到属性的类型一致的验证器
                Optional<Validator> first = execs.stream().filter(validator -> validator.pType().isAssignableFrom(property.getType())).findFirst();
                if (!first.isPresent()) {
                    if (execs == null || execs.size() < 0) {
                        throw new ValidatorException(property.getForm() + "中未找名字为 [" + exec.getName() + "]的验证器");
                    } else {
                        throw new ValidatorException(property.getForm() + "中未找到对应验证属性类型并且名字为 [" + exec.getName() + "]的验证器");
                    }
                }
                Validator validator = first.get();
                boolean valid = validator.isValid(obj, property);
                result = new Result();
                result.setProperty(field.getName());
                result.setValid(valid);
                //如果验证失败,则不执行当前字段的下一个验证
                if (!valid) {
                    result.setMsg(exec.getMsg());
                    break exec;
                }
            }
            resultList.add(result);
        }

        return new Results(resultList);
    }

    @Override
    public void validateAndThrowFirst(String form, Object obj, Validator<?>... customizeValidators) {
        Results results = validate(form, obj, customizeValidators);
        if (!results.validAll()) {
            throw new ValidatorException(results.getFirstErrorMsg());
        }
    }

    public List<Form> getForms() {
        return forms;
    }

    private Property getProperty(Object obj, String fieldName, String form) throws ValidatorException {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                if (descriptor.getName().equals(fieldName)) {
                    Class type = descriptor.getPropertyType();
                    Method readMethod = descriptor.getReadMethod();
                    Object value = readMethod.invoke(obj);

                    Property<Object> property = new Property<>();
                    property.setName(fieldName);
                    property.setType(type);
                    property.setValue(value);
                    property.setForm(form);
                    return property;
                }
            }
            throw new ValidatorException(form + " 未找到名字为" + fieldName + "的属性 ");
        } catch (Exception e) {
            throw new ValidatorException(form + " 无法获取" + fieldName + "的属性信息 " + e.getMessage(), e);
        }
    }

}
