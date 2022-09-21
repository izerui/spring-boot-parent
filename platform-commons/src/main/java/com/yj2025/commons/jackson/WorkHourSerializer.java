package com.yj2025.commons.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.yj2025.commons.em.WorkHourEnum;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by serv on 2017/6/22.
 */
public class WorkHourSerializer extends JsonSerializer<BigDecimal>{
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        String outPut;
        if (value == null) {
            outPut = null;
        }else{
            //两位精度,向上取整
            outPut = Decimal2StringUtils.toPlainString(WorkHourEnum.WORK_HOUR.format(value));
        }
        gen.writeString(outPut);
    }
}
