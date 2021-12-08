package com.yj2025.doc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class LabelCommandLineRunner implements CommandLineRunner {

    private final static String LABEL_LINE_RUNNER =
            "\n----------------------------------------------------------\n\t" +
                    "Application '${application}' is running! Profile Active:[${profile}] Access URLs:\n\t" +
                    "Web: \t\thttp://${host}:${port}${contextPath}\n\t" +
                    "Docs: \t\thttp://${host}:${port}${contextPath}/api\n\t" +
                    "Xrebel: \thttp://${host}:${port}${contextPath}/xrebel\n\t" +
                    "Actuator: \thttp://${host}:${port}${contextPath}/actuator\n" +
                    "----------------------------------------------------------";

    private ApplicationContext applicationContext;

    public LabelCommandLineRunner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
        Environment env = applicationContext.getEnvironment();
        Map<String, Object> variables = new HashMap<>();
        variables.put("port", Optional.ofNullable(env.getProperty("server.port")).orElse("8080"));
        variables.put("contextPath", Optional.ofNullable(env.getProperty("server.servlet.context-path")).orElse(""));
        variables.put("host", InetAddress.getLocalHost().getHostAddress());
        variables.put("application", env.getProperty("spring.application.name"));
        variables.put("profile", Optional.ofNullable(env.getProperty("spring.profiles.active")).orElse("default"));
        log.info(new StringSubstitutor(variables).replace(LABEL_LINE_RUNNER));
    }
}
