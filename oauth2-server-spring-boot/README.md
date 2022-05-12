## 生成jws证书密钥,密码:123456
```
keytool -genkey -alias jwt -keyalg RSA -keystore jwt.jks -validity 365
```

提取 public key
```
keytool -list -rfc --keystore jwt.jks | openssl x509 -inform pem -pubkey -out certificate.crt > public.crt
```

参考: https://zhuanlan.zhihu.com/p/150362634