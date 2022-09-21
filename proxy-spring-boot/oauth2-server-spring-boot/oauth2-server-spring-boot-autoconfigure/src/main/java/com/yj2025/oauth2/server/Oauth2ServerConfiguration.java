package com.yj2025.oauth2.server;

import com.yj2025.oauth2.security.support.MappingUrls;
import com.yj2025.oauth2.server.advice.GlobalResponseBodyAdviceAdapter;
import com.yj2025.oauth2.server.controller.QrcodeController;
import com.yj2025.oauth2.server.controller.TokenController;
import com.yj2025.oauth2.server.security.Oauth2Configuration;
import com.yj2025.oauth2.server.security.SecurityConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * oauth2服务自动配置：
 * 1.  全局异常包装返回, 通过`ignoreWrapPathMatchers`控制返回原生内容
 * 2.  支持用户密码表单提交认证、二维码扫码登录认证
 * 3.  支持自定义登录成功回调、登出回调
 * 4.  支持自定义密码校验器
 * 5.  使用可通过`UserDetailsRemoteLoader`实现自定义加载用户
 * 6.  通过配置可选择token生成模式 不透明模式、jwt模式
 * 7.  支持刷新token的时候指定usercode用来切换用户
 * 8.  支持二维码登录的时候，按指定账套登录
 * 9.  支持用户权限authorities集合
 * 10. token获取每次获取新的access_token、refresh_token，防止一端注销，多处登出
 * 11. 非登录验证相关的请求一概拒绝
 */
@Configuration
@EnableConfigurationProperties(Oauth2Properties.class)
@Import({SecurityConfiguration.class, Oauth2Configuration.class, TokenController.class, QrcodeController.class})
public class Oauth2ServerConfiguration {

    private final static String[] ignoreWrapPathMatchers = {
            MappingUrls.OAUTH_CHECK_TOKEN_URL, // opaque token，通过rest请求方式进行token校验地址
            MappingUrls.JWT_RSA_KEY_URL, // jwt获取公钥用来校验token的地址
            "/actuator/**"
    };

    @Bean
    public GlobalResponseBodyAdviceAdapter globalResponseBodyAdviceAdapter(ErrorAttributes errorAttributes) {
        return new GlobalResponseBodyAdviceAdapter(errorAttributes, ignoreWrapPathMatchers);
    }
}
