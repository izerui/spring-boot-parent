package com.yj2025.commons.em;

public enum ModelApplication {
    NONE("当前版本"),//缺省值，每个版本中默认就对应的是VersionType自己业务
    ATTENDANCE("考勤系统"),
    CLOUD_FINANCE("财务系统"),
    PROOFING("开发打样"),
    THIRD_API("公共接口"),
    TY_MES("天音MES"),
    TY_SUPPLIER("天音供应商"),
    ;
    private String remark;

    ModelApplication(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }
}
