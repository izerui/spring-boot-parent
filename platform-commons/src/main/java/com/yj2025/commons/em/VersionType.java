package com.yj2025.commons.em;

import com.google.common.collect.Lists;

import java.util.List;

public enum VersionType {
    MANUFACTURE("智造版", Lists.newArrayList("new-dev", "dev", "test", "uat", "yunji")),
    TRADE("星链版", Lists.newArrayList("ec-dev", "ec-test", "ec-yunji")),
    MACHINE_WORK("数控版", Lists.newArrayList("mlf-dev", "mlf-test", "mlf-yunji")),
    P3("P3", Lists.newArrayList("p3-dev", "p3-test", "p3-yunji")),
    CONNECTION("联通版", List.of()),
    ;
    private String remark;
    private List<String> profiles;

    VersionType(String remark, List<String> profiles) {
        this.remark = remark;
        this.profiles = profiles;
    }

    public String getRemark() {
        return remark;
    }

    public List<String> getProfiles() {
        return profiles;
    }

    public static VersionType getVersionType(String profiles) {
        for (VersionType versionType : VersionType.values()) {
            long count = versionType.getProfiles().stream().filter(v -> v.contains(profiles)).count();
            if (count > 0) {
                return versionType;
            }
        }
        return null;
    }
}
