package com.yj2025.amazonaws;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;

/**
 * @author liuyuhua
 * @date 2022/4/15
 */
@Data
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {

    private Region region;
    private String accessKeyId;
    private String secretAccessKey;
}
