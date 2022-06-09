package com.yj2025.tests;

import org.reflections.Reflections;
import org.springframework.security.core.AuthenticationException;

import java.util.Set;

public class SuperTypeOfClassTests {
    public static void main(String[] args) {
        Reflections reflections = new Reflections("org.springframework");
        Set<Class<? extends AuthenticationException>> subTypesOf = reflections.getSubTypesOf(AuthenticationException.class);
        for (Class<? extends AuthenticationException> aClass : subTypesOf) {
            String lines = "import " + aClass.getName() + ";";
            System.out.println(lines);
        }
        for (Class<? extends AuthenticationException> aClass : subTypesOf) {
            String lines = "protected void throw" + aClass.getSimpleName() + "Next(String message) {\n" +
                    "        throw new " + aClass.getSimpleName() + "(message);\n" +
                    "    }";
            System.out.println(lines);
        }
    }
}
