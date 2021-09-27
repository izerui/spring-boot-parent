package com.yj2025.doc;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * knife4j
 *
 * @link https://doc.xiaominfo.com/knife4j/
 */
@Slf4j
@EnableSwagger2
@Configuration
public class DocAutoConfiguration implements WebMvcConfigurer {

    @Value("${spring.application.name:null}")
    private String applicationName;

    @Value("${doc.head.wrap.enabled:true}")
    private Boolean headWrapEnabled = true;

    private List<RequestParameter> parameter() {
        if (!headWrapEnabled) {
            return null;
        }
        List<RequestParameter> params = new ArrayList<>();
        params.add(new RequestParameterBuilder().name("entCode")
                .description("企业编码")
                .in(ParameterType.HEADER)
                .query(s -> s.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(true)
                .build());
        params.add(new RequestParameterBuilder().name("userCode")
                .description("用户编码")
                .in(ParameterType.HEADER)
                .query(s -> s.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(false)
                .build());
        params.add(new RequestParameterBuilder().name("postCode")
                .description("职能编码")
                .in(ParameterType.HEADER)
                .query(s -> s.model(m -> m.scalarModel(ScalarType.STRING)))
                .required(false)
                .build());
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
                .build()
                .globalRequestParameters(parameter());
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
