package jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class JksMd5Test {

    @Test
    public void md5() throws IOException {
        ClassPathResource resource = new ClassPathResource("jwt.jks");
        byte[] bytes = FileCopyUtils.copyToByteArray(resource.getFile());
        String md5Hex = DigestUtils.md5Hex(bytes);
        System.out.println(md5Hex);
        Assert.state("86cd85dc031dbeca037d74e9127c4420".equals(md5Hex), "jwt.jks被修改.");
        System.out.println(md5Hex);
    }


    @Test
    public void testFile() throws FileNotFoundException {
        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        KeyPair jwt = keyStoreKeyFactory.getKeyPair("jwt", "123456".toCharArray());
        System.out.println(jwt.getPublic().toString());
        System.out.println(jwt.getPrivate().toString());

//        keytool -genkey -alias jwt -keyalg RSA -keysize 1024 -keystore jwt.jks -validity 365 -keypass 123456 -storepass 123456
    }

    @Test
    public void md52() throws IOException {
        printMd5("open-spring-boot/open-oauth-server-spring-boot-autoconfigure/src/test/resources/jwt.jks");
        printMd5("open-spring-boot/open-oauth-server-spring-boot-autoconfigure/target/classes/jwt.jks");
    }

    private void printMd5(String filePath) throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(new File(filePath));
        String md5Hex = DigestUtils.md5Hex(bytes);
        System.out.println(md5Hex);
    }


    @Autowired
    private KeyPair keyPair;

    @Value("${classpath:public.crt}")
    private RSAPublicKey rsaPublicKey;

    @Test
    public void verify() throws ParseException, JOSEException {
        String s = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhbGwiXSwiZXhwIjoxNjQ3NjA5NDA4LCJqdGkiOiI5N2Y4NzI3YS0xODBiLTRmNDktODc2MC1jMzlkMzkzNGY1N2EiLCJjbGllbnRfaWQiOiJhcHAwMDEifQ.RP-FhiKtAvMZQnvFKaKIoh00sOMdfMAUI61-sATbWb1WYVDq4se2gRNmZWWRD__ljAt3LCI5YIVnEqRecv1G0Rl2z9g22zfLYZ4NeQEnO4jco8Z0s-Diou--JCHrqdZ1rwmbKEd_KgJbz3LL8-mAuDPTj0S4RGJ68nr07c97G1U_OhKXTNQKGS7StTPdT0MHbPAuiogxkdIS175iWVRcCHK40UPzq58JwN4dbSAvNP04gShx130e1MPXHtMoSyuoY0D-2VwpXdeWtaNXiHC4FSXd3UpaC8wqeMWd3hO0jxMmQvMSnxCF564fekuL75y6mPajK264H6AzG7sNlYGieg";
        JWSObject parse = JWSObject.parse(s);

        // 方式一验签
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        System.out.println(new JWKSet(key).toJSONObject());
        ;
        boolean verify1 = parse.verify(new RSASSAVerifier(key));
        System.out.println(verify1);

        // 方式二验签
        boolean verify2 = parse.verify(new RSASSAVerifier(rsaPublicKey));
        System.out.println(verify2);
    }

}
