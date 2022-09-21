package com.yj2025.commons.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.yj2025.commons.em.MoneyEnum;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by serv on 2017/6/22.
 */
public class ExchangeRateSerializer extends JsonSerializer<BigDecimal>{
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        BigDecimal decimal = MoneyEnum.EXCHANGE_RATE.format(value);
        gen.writeString(Decimal2StringUtils.toPlainString(decimal));
    }
}
