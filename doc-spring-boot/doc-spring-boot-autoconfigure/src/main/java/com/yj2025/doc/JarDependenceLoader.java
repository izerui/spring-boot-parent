package com.yj2025.doc;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author liuyuhua
 */
@Slf4j
public class JarDependenceLoader implements InitializingBean {

    private final static List<String> LABEL_GROUP_IDS = Lists.newArrayList(
            "com.yj2025",
            "com.ecworking"
    );

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private Table<String, String, String> dependenceies = HashBasedTable.create();

    static {
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
    }

    public JarDependenceLoader(String labelGroups) {
        try {
            List<String> labelGroupIds = Arrays.asList(labelGroups.split(","));
            Resource[] resources = resourcePatternResolver.getResources("classpath*:**/pom.properties");
            for (Resource resource : resources) {
                Properties properties = new Properties();
                PropertiesLoaderUtils.fillProperties(properties, resource);
                String groupId = properties.getProperty("groupId");
                String artifactId = properties.getProperty("artifactId");
                String version = properties.getProperty("version");
                if (labelGroupIds.contains(groupId)) {
                    dependenceies.put(groupId, artifactId, version);
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if (!dependenceies.isEmpty()) {
            System.out.println("依赖列表: \t");
            System.out.println("----------------------------------------------------------\t");
            dependenceies.rowMap().forEach((groupId, artifactIdVersionMap) -> {
                artifactIdVersionMap.forEach((artifactId, version) -> {
                    System.out.println(
                            AnsiOutput.toString(AnsiColor.CYAN, groupId) +
                                    ":" + AnsiOutput.toString(AnsiColor.GREEN, artifactId) +
                                    ":" + AnsiOutput.toString(AnsiColor.MAGENTA, version) + "\t");
                });
            });
            System.out.println("----------------------------------------------------------");
        }
    }

}
