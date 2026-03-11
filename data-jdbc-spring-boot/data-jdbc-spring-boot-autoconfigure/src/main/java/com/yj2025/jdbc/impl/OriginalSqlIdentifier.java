package com.yj2025.jdbc.impl;

import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.IdentifierProcessing;
import org.springframework.data.relational.core.sql.SqlIdentifier;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.UnaryOperator;

public class OriginalSqlIdentifier implements SqlIdentifier, Expression {

    private String name;

    public OriginalSqlIdentifier(String name) {
        this.name = name;
    }

    @Override
    public String getReference(IdentifierProcessing processing) {
        return name;
    }

    @Override
    public String toSql(IdentifierProcessing processing) {
        return processing.quote(name);
    }

    @Override
    public SqlIdentifier transform(UnaryOperator<String> transformationFunction) {
        return new OriginalSqlIdentifier(transformationFunction.apply(name));
    }

    @Override
	public Iterator<SqlIdentifier> iterator() {
		return Collections.<SqlIdentifier> singleton(this).iterator();
	}

    @Override
    public String toString() {
        return toSql(IdentifierProcessing.NONE);
    }
}
