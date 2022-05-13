package com.yj2025.gateway;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class DefaultPathMatcherAuthoritiesLoader implements PathMatcherAuthoritiesLoader {
    @Override
    public Map<String, Set<String>> getPathMatcherAuthoritiesMap() {
        Map<String, Set<String>> authorities = new HashMap<>();
        authorities.put("GET:/test", Sets.newHashSet("postCode001", "postCode003"));
        return authorities;
    }
}
