package com.yj2025.jackson;
import com.yj2025.jackson.InvCategoryVo;
import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Application {

    @GetMapping("/test")
    public InvCategoryVo getInvCategory() {
        InvCategoryVo vo = new InvCategoryVo();
        vo.setAutoCode("234");
        vo.setCode("sdaf");
        vo.setParentCode("gsdg");
        vo.setName("asdg34rwg3rg");
        vo.setCodes(new ArrayList<String>());
        vo.setInventoryTypeCode("f23f2");
        vo.setInventoryTypeName("asdfsd");
        vo.setRemark("g34rwg");
        vo.setChildren(new ArrayList<InvCategoryVo>());
        return vo;
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
