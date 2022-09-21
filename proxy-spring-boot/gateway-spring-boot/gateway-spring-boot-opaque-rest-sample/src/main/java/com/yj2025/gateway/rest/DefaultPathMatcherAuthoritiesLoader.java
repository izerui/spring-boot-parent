package com.yj2025.gateway.rest;

import com.google.common.collect.Sets;
import com.yj2025.gateway.proxy.PathMatcherAuthoritiesLoader;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class DefaultPathMatcherAuthoritiesLoader implements PathMatcherAuthoritiesLoader {
    @Override
    public Map<String, Set<String>> getInitializePathMatcherAuthoritiesMap() {
        Map<String, Set<String>> authorities = new HashMap<>();
        authorities.put("GET:/authenrity", Sets.newHashSet("postCode001", "postCode003"));
        return authorities;
    }
}
