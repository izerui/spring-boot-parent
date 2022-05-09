package com.yj2025.oauth2.server.security;

import com.yj2025.oauth2.security.RespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@Configuration
@RestController
public class TokenController {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    @RequestMapping(value = "/oauth/revoke_token", method = RequestMethod.POST)
    public RespVo revokeToken(@RequestParam("access_token") String accessToken) {
        if (accessToken != null) {
            boolean revokeToken = consumerTokenServices.revokeToken(accessToken);
            if (revokeToken) {
                return RespVo.success("登出成功!");
            }
        }
        return RespVo.error("logout_error", "token注销失败");
    }

    @GetMapping("/userinfo")
    public RespVo userInfo(Principal principal) {
        return RespVo.success(principal);
    }
}
