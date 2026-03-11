/*
 * Copyright 2022-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yj2025.jdbc.override;

import org.springframework.data.relational.core.mapping.*;

/**
 * The default naming strategy used by Spring Data Relational. Names are in {@code SNAKE_CASE}.
 *
 * @author Jens Schauder
 * @since 3.0
 */
public class OverrideDefaultNamingStrategy implements NamingStrategy {

    private ForeignKeyNaming foreignKeyNaming = ForeignKeyNaming.APPLY_RENAMING;

    @Override
    public String getTableName(Class<?> type) {
        return NamingStrategy.super.getTableName(type);
    }

    @Override
    public String getReverseColumnName(RelationalPersistentProperty property) {

        return getColumnNameReferencing(property.getOwner());
    }

    @Override
    public String getReverseColumnName(PersistentPropertyPathExtension path) {

        RelationalPersistentEntity<?> leafEntity = path.getIdDefiningParentPath().getRequiredLeafEntity();

        return getColumnNameReferencing(leafEntity);
    }

    private String getColumnNameReferencing(RelationalPersistentEntity<?> leafEntity) {

        if (foreignKeyNaming == ForeignKeyNaming.IGNORE_RENAMING) {
            return getTableName(leafEntity.getType());
        }

        return leafEntity.getTableName().getReference();
    }
}
