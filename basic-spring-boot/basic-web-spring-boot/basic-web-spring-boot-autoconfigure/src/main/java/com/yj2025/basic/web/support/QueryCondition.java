package com.yj2025.basic.web.support;

import com.yj2025.jpa.impl.Conditions;

@Deprecated(since = "3.1", forRemoval = true)
public interface QueryCondition extends AuthAware {
    default Conditions intEntConditions() {
        return Conditions.where("entCode").is(this.getEntCode());
    }

    default Conditions intEntDeletedConditions() {
        return Conditions.where("entCode").is(this.getEntCode()).and("deleted").is(false);
    }

    Conditions getConditions();
}
