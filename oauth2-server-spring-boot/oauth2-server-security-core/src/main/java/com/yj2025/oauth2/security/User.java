package com.yj2025.oauth2.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Created by serv on 2017/4/11.
 */
public class User extends org.springframework.security.core.userdetails.User {

    private final static long serialVersionUID = 1L;

    private String accountName;
    private String accountCode;
    private String entCode;
    private String entName;
    private String userCode;
    private String userName;
    private String additionalSalt;

    public User(String accountName, String password, String... authorities) {
        super(accountName, password, AuthorityUtils.createAuthorityList(Optional.ofNullable(authorities).orElse(new String[0])));
        this.accountName = accountName;
    }

    public User(String accountName, String password, Collection<? extends GrantedAuthority> authorities) {
        super(accountName, password, authorities);
        this.accountName = accountName;
    }

    public User(String accountName, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Set<String> authorities) {
        super(accountName, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, AuthorityUtils.createAuthorityList(authorities.toArray(new String[authorities.size()])));
        this.accountName = accountName;
    }

    public User(String accountName, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, String... authorities) {
        super(accountName, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, AuthorityUtils.createAuthorityList(Optional.ofNullable(authorities).orElse(new String[0])));
        this.accountName = accountName;
    }

    @JsonIgnore
    public String getAccountName() {
        return accountName;
    }

    public User setAccountName(String accountName) {
        this.accountName = accountName;
        return this;
    }

    @JsonIgnore
    public String getAccountCode() {
        return accountCode;
    }

    public User setAccountCode(String accountCode) {
        this.accountCode = accountCode;
        return this;
    }

    @JsonIgnore
    public String getEntCode() {
        return entCode;
    }

    public User setEntCode(String entCode) {
        this.entCode = entCode;
        return this;
    }

    @JsonIgnore
    public String getEntName() {
        return entName;
    }

    public User setEntName(String entName) {
        this.entName = entName;
        return this;
    }

    @JsonIgnore
    public String getUserCode() {
        return userCode;
    }

    public User setUserCode(String userCode) {
        this.userCode = userCode;
        return this;
    }

    @JsonIgnore
    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getAdditionalSalt() {
        return additionalSalt;
    }

    public User setAdditionalSalt(String additionalSalt) {
        this.additionalSalt = additionalSalt;
        return this;
    }
}
