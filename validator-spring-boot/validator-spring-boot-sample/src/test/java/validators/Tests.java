package validators;

import com.yj2025.validator.Results;
import com.yj2025.validator.ValidatorContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by serv on 2016/12/1.
 */
@SpringBootTest(classes = Tests.class)
@SpringBootApplication
public class Tests {

    @Autowired
    ValidatorContext validatorContext;

    @Autowired
    MessageSource messageSource;

    private User user;

    @BeforeEach
    public void before() {
        user = new User();
        user.setName("红包");
        user.setName("我是中国人名解dddddddsdfdsfdsfSDfsdfwefwefsdfDSfdsfdsf的发生的范德萨发生地方微风威锋网范文芳违法dddddddddddddddd放军");
        user.setAge(100);
        user.setEmail("sdjf@sdjfdsf.com");
        user.setPhone("1333332322523452525");
        user.setDateFormat("1999-3 -30");
    }

    @Test
    public void test12() throws Exception {
        Results results = validatorContext.validate("ent_type_1", user);
        System.out.println(results.toString());

    }

}
