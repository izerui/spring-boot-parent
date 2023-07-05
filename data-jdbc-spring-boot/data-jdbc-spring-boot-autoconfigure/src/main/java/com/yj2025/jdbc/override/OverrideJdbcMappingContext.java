package com.yj2025.jdbc.override;

import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.mapping.InstanceCreatorMetadata;
import org.springframework.data.mapping.Parameter;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class OverrideJdbcMappingContext extends JdbcMappingContext {

    private static final String MISSING_PARAMETER_NAME = "A constructor parameter name must not be null to be used with Spring Data JDBC; Offending parameter: %s";

    public OverrideJdbcMappingContext() {
    }

    public OverrideJdbcMappingContext(NamingStrategy namingStrategy) {
        super(namingStrategy);
    }

    @Override
    protected <T> RelationalPersistentEntity<T> createPersistentEntity(TypeInformation<T> typeInformation) {
        OverrideRelationalPersistentEntityImpl<T> entity = new OverrideRelationalPersistentEntityImpl<>(typeInformation,
                this.getNamingStrategy());
        entity.setForceQuote(isForceQuote());

        InstanceCreatorMetadata<RelationalPersistentProperty> creator = entity.getInstanceCreatorMetadata();

        if (creator == null) {
            return entity;
        }

        for (Parameter<Object, RelationalPersistentProperty> parameter : creator.getParameters()) {
            Assert.state(StringUtils.hasText(parameter.getName()), () -> String.format(MISSING_PARAMETER_NAME, parameter));
        }

        return entity;
    }
}
