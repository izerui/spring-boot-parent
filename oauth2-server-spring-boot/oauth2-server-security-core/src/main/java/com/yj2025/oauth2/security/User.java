package com.yj2025.oauth2.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;
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

    public User(String accountName, String password, Collection<? extends GrantedAuthority> authorities) {
        super(accountName, password, authorities);
        this.accountName = accountName;
    }

    public User(String accountName, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Set<String> authorities) {
        super(accountName, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, AuthorityUtils.createAuthorityList(authorities.toArray(new String[authorities.size()])));
        this.accountName = accountName;
    }

    @JsonIgnore
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @JsonIgnore
    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    @JsonIgnore
    public String getEntCode() {
        return entCode;
    }

    public void setEntCode(String entCode) {
        this.entCode = entCode;
    }

    @JsonIgnore
    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    @JsonIgnore
    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @JsonIgnore
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
