package com.yj2025.commons.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.yj2025.commons.em.MoneyEnum;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by serv on 2017/6/22.
 */
public class TaxRateDeserializer extends JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        if(p.getText()==null||"".equals(p.getText())){
            return null;
        }
        return MoneyEnum.TAX_RATE.format(new BigDecimal(p.getText()));
    }
}
