package com.yj2025.validator;

import com.yj2025.validator.parser.Form;
import com.yj2025.validator.parser.Parser;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

/**
 * Created by serv on 2016/12/3.
 */
@Configuration
public class ValidatorConfiguration {

    @Bean
    public ValidatorContext validatorContext(@Autowired ObjectProvider<ValidatorCustomizer> validatorCustomizers) throws Exception {
        ClassPathResource resource = new ClassPathResource("validation.xml");
        if (!resource.exists()) {
            throw new RuntimeException("该工程使用了validator组件,但是在当前类路径下未找到[validation.xml]文件,请添加该文件或者移除[validator-spring-boot-starter]依赖");
        }
        List<Form> forms = new Parser().parser(resource.getInputStream());
        ValidatorContextImpl validatorContext = new ValidatorContextImpl(forms);
        validatorCustomizers.ifAvailable(validatorCustomizer -> {
            validatorCustomizer.customize(validatorContext);
        });
        return validatorContext;
    }

}
