# 开放平台

## 生成jws证书密钥,密码:123456 (有效期10年)

```
keytool -genkey -alias jwt -keyalg RSA -keystore jwt.jks -validity 3650
```

## 提取 public key

```
keytool -list -rfc --keystore jwt.jks | openssl x509 -inform pem -pubkey -out certificate.crt > public.crt
```
> 参考: https://zhuanlan.zhihu.com/p/150362634

## 获取token

```
curl --location --request POST 'https://open-dev.yj2025.com/oauth/token?grant_type=client_credentials&client_id=app001&client_secret=123456' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
```

## 附带client_id和tenant_id请求资源

```
curl --location --request GET 'https://open-dev.yj2025.com/api/test' \
--header 'Accept: application/json' \
--header 'Content-Type: application/json'
--header 'Authorization: Bearer {access_token}'
--header 'UnixTimestamp: 1647685480348'
--header 'Sign: 7D70663568CAC5AF684503681E3A4D41'
```
