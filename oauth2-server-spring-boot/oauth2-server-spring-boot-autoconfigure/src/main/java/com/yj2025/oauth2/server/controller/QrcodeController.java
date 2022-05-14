package com.yj2025.oauth2.server.controller;

import com.yj2025.oauth2.security.support.MappingUrls;
import com.yj2025.oauth2.security.support.RespVo;
import com.yj2025.oauth2.server.security.provider.QrcodeService;
import com.yj2025.oauth2.server.security.provider.QrcodeStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.yj2025.oauth2.security.support.QrcodeConstants.QRCODE_TICKET_KEY;

/**
 * 二维码登录相关接口
 * Created by serv on 2016/12/20.
 */
@Configuration
@RestController
public class QrcodeController {

    @Autowired
    private QrcodeService qrcodeService;

    @PostMapping(MappingUrls.QRCODE_GENERATE_URL)
    public RespVo<Map<String, String>> generateQrCode(HttpServletResponse response) {
        String qrcode = qrcodeService.createQrcode();
        //二维码登录ticket 放入cookie中
        Cookie cookie = new Cookie(QRCODE_TICKET_KEY, qrcode);
        cookie.setPath("/");
        cookie.setMaxAge(5 * 60);
        response.addCookie(cookie);
        Map<String, String> map = new HashMap<>();
        map.put(QRCODE_TICKET_KEY, "https://api.yj2025.com/qrcode/redirect?q=" + qrcode);
        return RespVo.success(map);
    }


    @PostMapping(MappingUrls.QRCODE_VALIDATE_URL)
    public RespVo<QrcodeStatus> validateQrCode(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, QRCODE_TICKET_KEY);
        String qrCodeTicket = cookie.getValue();
        QrcodeStatus status = qrcodeService.getQrcodeStatus(qrCodeTicket);
        return RespVo.success(status);
    }


}
