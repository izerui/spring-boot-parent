package com.yj2025.doc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class LabelCommandLineRunner implements CommandLineRunner {

    private final static String LABEL_LINE_RUNNER =
            "\n----------------------------------------------------------\n\t" +
                    "Os: \t\t${osName} ${osArch} ${osVersion}\n\t" +
                    "Java: \t\t${jVendor} ${jVersion}\n\t" +
                    "Application [${application}] is running! Profile Active:[${profile}] Access URLs:\n\t" +
                    "Web: \t\thttp://${host}:${port}${contextPath}\n\t" +
                    "Docs: \t\thttp://${host}:${port}${contextPath}/api\n\t" +
                    "Actuator: \thttp://${host}:${port}${contextPath}/actuator\n" +
                    "----------------------------------------------------------";

    private Map<String, Object> variables_;

    private ApplicationContext applicationContext;

    public LabelCommandLineRunner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Map<String,Object> getVariables() throws UnknownHostException {
        if(this.variables_ == null) {
            Environment env = applicationContext.getEnvironment();
            this.variables_ = new HashMap<>();
            this.variables_.put("port", Optional.ofNullable(env.getProperty("server.port")).orElse("8080"));
            this.variables_.put("contextPath", Optional.ofNullable(env.getProperty("server.servlet.context-path")).orElse(""));
            this.variables_.put("host", InetAddress.getLocalHost().getHostAddress());
            this.variables_.put("application", env.getProperty("spring.application.name"));
            this.variables_.put("profile", Optional.ofNullable(env.getProperty("spring.profiles.active")).orElse("default"));
            this.variables_.put("osName", System.getProperty("os.name"));
            this.variables_.put("osArch", System.getProperty("os.arch"));
            this.variables_.put("osVersion", System.getProperty("os.version"));
            this.variables_.put("jVersion", System.getProperty("java.version"));
            this.variables_.put("jVendor", System.getProperty("java.vendor"));
        }
        return this.variables_;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info(new StringSubstitutor(getVariables()).replace(LABEL_LINE_RUNNER));
    }

    public String getWebUrl() throws UnknownHostException {
        return new StringSubstitutor(getVariables()).replace("http://${host}:${port}${contextPath}");
    }
}
