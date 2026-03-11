package com.yj2025.commons.em;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModelApplication {
    NONE("当前版本"),//缺省值，每个版本中默认就对应的是VersionType自己业务
    ATTENDANCE("考勤系统"),
    CLOUD_FINANCE("财务系统"),
    NEW_FINANCE("财务平台"),
    CODE_WORK("码上报工"),
    OUTSOURCE_WAREHOUSE("委外仓管理"),
    INQUIRY_QUOTATION("询盘报价"),
    ENTERPRISE_INTERCONNECT("企业互联"),
    SKD_CKD("SKD/CKD"),
    PROOFING("开发打样"),
    TRACE_BACK("追溯系统"),
    ESOP("ESOP"),
    SCM("接单宝"),
    SUPPLIER("供应商端口"),
    TY_SUPPLIER("天音供应商"),
    TY_MES("天音MES"),
    THIRD_API("公共接口"),
    AI("AI经营管理"),
    DROP_BOX("网盘"),
    EMAIL("企业邮箱"),
    ;
    private final String remark;
}
