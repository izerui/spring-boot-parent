package com.yj2025.doc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author liuyuhua
 */
@Configuration
@ConditionalOnWebApplication
public class DocAutoConfiguration implements WebMvcConfigurer {

    @Value("${spring.application.name:#{null}}")
    private String applicationName;

    @Value("${doc.label.groups:com.yj2025,com.ecworking}")
    private String labelGroups;

//    @Bean
//    public JarDependenceLoader jarDependenceLoader() {
//        return new JarDependenceLoader(labelGroups);
//    }

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        String[] paths = {"/**"};
        return GroupedOpenApi.builder().group("default")
                .pathsToMatch(paths)
                .addOperationCustomizer((operation, handlerMethod) -> operation
                        .addParametersItem(new HeaderParameter().name("entCode").description("企业编码").required(false))
                        .addParametersItem(new HeaderParameter().name("userCode").description("用户编码").required(false))
                        .addParametersItem(new HeaderParameter().name("postCode").description("职能编码").required(false)))
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName)
                        .version("3.0")
                        .description(applicationName));
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
