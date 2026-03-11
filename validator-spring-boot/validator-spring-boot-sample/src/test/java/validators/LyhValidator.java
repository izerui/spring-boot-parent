package validators;

import com.yj2025.validator.ValidatorException;
import com.yj2025.validator.validation.Property;
import com.yj2025.validator.validation.Validator;

/**
 * Created by serv on 2016/12/5.
 */
public class LyhValidator implements Validator<String> {
    @Override
    public boolean isValid(Object obj, Property<String> property) throws ValidatorException {
        return property.getValue().equals("刘玉华");
    }

    @Override
    public String name() {
        return "lyh";
    }

    @Override
    public Class<String> pType() {
        return String.class;
    }
}
