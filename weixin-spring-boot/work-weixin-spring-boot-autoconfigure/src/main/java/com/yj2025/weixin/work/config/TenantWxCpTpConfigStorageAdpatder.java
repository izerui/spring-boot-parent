package com.yj2025.weixin.work.config;

import me.chanjar.weixin.common.bean.WxAccessToken;
import me.chanjar.weixin.common.util.http.apache.ApacheHttpClientBuilder;
import me.chanjar.weixin.cp.bean.WxCpProviderToken;
import me.chanjar.weixin.cp.config.WxCpTpConfigStorage;

import java.io.File;
import java.util.concurrent.locks.Lock;

public class TenantWxCpTpConfigStorageAdpatder implements WxCpTpConfigStorage {
    @Override
    public void setBaseApiUrl(String baseUrl) {

    }

    @Override
    public String getApiUrl(String path) {
        return null;
    }

    @Override
    public String getSuiteAccessToken() {
        return null;
    }

    @Override
    public WxAccessToken getSuiteAccessTokenEntity() {
        return null;
    }

    @Override
    public boolean isSuiteAccessTokenExpired() {
        return false;
    }

    @Override
    public void expireSuiteAccessToken() {

    }

    @Override
    public void updateSuiteAccessToken(WxAccessToken suiteAccessToken) {

    }

    @Override
    public void updateSuiteAccessToken(String suiteAccessToken, int expiresInSeconds) {

    }

    @Override
    public String getSuiteTicket() {
        return null;
    }

    @Override
    public boolean isSuiteTicketExpired() {
        return false;
    }

    @Override
    public void expireSuiteTicket() {

    }

    @Override
    public void updateSuiteTicket(String suiteTicket, int expiresInSeconds) {

    }

    @Override
    public String getSuiteId() {
        return null;
    }

    @Override
    public String getSuiteSecret() {
        return null;
    }

    @Override
    public String getToken() {
        return null;
    }

    @Override
    public String getAesKey() {
        return null;
    }

    @Override
    public String getCorpId() {
        return null;
    }

    @Override
    public String getCorpSecret() {
        return null;
    }

    @Override
    public String getProviderSecret() {
        return null;
    }

    @Override
    public String getAccessToken(String authCorpId) {
        return null;
    }

    @Override
    public WxAccessToken getAccessTokenEntity(String authCorpId) {
        return null;
    }

    @Override
    public boolean isAccessTokenExpired(String authCorpId) {
        return false;
    }

    @Override
    public void expireAccessToken(String authCorpId) {

    }

    @Override
    public void updateAccessToken(String authCorpId, String accessToken, int expiredInSeconds) {

    }

    @Override
    public String getAuthCorpJsApiTicket(String authCorpId) {
        return null;
    }

    @Override
    public boolean isAuthCorpJsApiTicketExpired(String authCorpId) {
        return false;
    }

    @Override
    public void expireAuthCorpJsApiTicket(String authCorpId) {

    }

    @Override
    public void updateAuthCorpJsApiTicket(String authCorpId, String jsApiTicket, int expiredInSeconds) {

    }

    @Override
    public String getAuthSuiteJsApiTicket(String authCorpId) {
        return null;
    }

    @Override
    public boolean isAuthSuiteJsApiTicketExpired(String authCorpId) {
        return false;
    }

    @Override
    public void expireAuthSuiteJsApiTicket(String authCorpId) {

    }

    @Override
    public void updateAuthSuiteJsApiTicket(String authCorpId, String jsApiTicket, int expiredInSeconds) {

    }

    @Override
    public boolean isProviderTokenExpired() {
        return false;
    }

    @Override
    public void updateProviderToken(String providerToken, int expiredInSeconds) {

    }

    @Override
    public String getProviderToken() {
        return null;
    }

    @Override
    public WxCpProviderToken getProviderTokenEntity() {
        return null;
    }

    @Override
    public void expireProviderToken() {

    }

    @Override
    public String getHttpProxyHost() {
        return null;
    }

    @Override
    public int getHttpProxyPort() {
        return 0;
    }

    @Override
    public String getHttpProxyUsername() {
        return null;
    }

    @Override
    public String getHttpProxyPassword() {
        return null;
    }

    @Override
    public ApacheHttpClientBuilder getApacheHttpClientBuilder() {
        return null;
    }

    @Override
    public boolean autoRefreshToken() {
        return false;
    }

    @Override
    public File getTmpDirFile() {
        return null;
    }

    @Override
    public Lock getProviderAccessTokenLock() {
        return null;
    }

    @Override
    public Lock getSuiteAccessTokenLock() {
        return null;
    }

    @Override
    public Lock getAccessTokenLock(String authCorpId) {
        return null;
    }

    @Override
    public Lock getAuthCorpJsapiTicketLock(String authCorpId) {
        return null;
    }

    @Override
    public Lock getSuiteJsapiTicketLock(String authCorpId) {
        return null;
    }
}
