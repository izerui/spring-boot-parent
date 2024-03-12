package com.yj2025.nacos.discovery;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Configuration
public class DiscoveryConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnNacosDiscoveryEnabled
    public NacosDiscoveryProperties nacosProperties() {
        NacosDiscoveryProperties nacosDiscoveryProperties = new NacosDiscoveryProperties();
        Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
        // 启动时间
        metadata.put("startup.time",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        // home path
        metadata.put("boot.path", String.valueOf(new ApplicationHome(this.getClass()).getSource()));
        // pid
        metadata.put("boot.pid", new ApplicationPid().toString());
        // app version
        String appVersion = System.getenv("APP_VERSION");
        if (appVersion != null && !"".equals(appVersion)) {
            metadata.put("version", appVersion);
        }
        // build user
        String buildUser = System.getenv("BUILD_USER");
        if (buildUser != null && !"".equals(buildUser)) {
            metadata.put("build.user", buildUser);
        }
        // add if in k8s
        ObjectProvider<BuildProperties> beanProvider = applicationContext.getBeanProvider(BuildProperties.class);
        beanProvider.ifAvailable(build -> {
            metadata.put("build.version", build.getVersion());
            metadata.put("build.time", String.valueOf(build.getTime()));
        });
        // git branch
        String gitBranch = System.getenv("GIT_BRANCH");
        if (gitBranch != null && !"".equals(gitBranch)) {
            metadata.put("git.branch", gitBranch);
        }
        return nacosDiscoveryProperties;
    }

}
