package com.yj2025.doc;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * knife4j
 * @link https://doc.xiaominfo.com/knife4j/
 */
@EnableSwagger2
@Configuration
public class DocAutoConfiguration implements WebMvcConfigurer {


    @Value("${spring.application.name:null}")
    private String applicationName;

    private List<Parameter> parameter() {
        List<Parameter> params = new ArrayList<>();
        params.add(new ParameterBuilder().name("entCode")
                .description("企业编码")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(true).build());
        params.add(new ParameterBuilder().name("userCode")
                .description("用户编码")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false).build());
        params.add(new ParameterBuilder().name("postCode")
                .description("职能编码")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false).build());
        return params;
    }

    @ConditionalOnMissingBean(Docket.class)
    @Bean(name = "defaultDocket")
    public Docket petApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build().globalOperationParameters(parameter());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(applicationName)
                .version("2.1")
                .build();
    }

    @Bean
    public LabelCommandLineRunner labelCommandLineRunner(ApplicationContext applicationContext) {
        return new LabelCommandLineRunner(applicationContext);
    }


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/api.html", "/doc.html");
        registry.addRedirectViewController("/api", "/doc.html");
    }

}
