package com.yj2025.oauth2.server.controller;

import com.yj2025.oauth2.security.RespVo;
import com.yj2025.oauth2.server.security.provider.QrcodeService;
import com.yj2025.oauth2.server.security.provider.QrcodeStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.yj2025.oauth2.server.security.provider.QrcodeConstants.QRCODE_TICKET_KEY;

/**
 * 二维码登录相关接口
 * Created by serv on 2016/12/20.
 */
@Controller
public class QrcodeController {

    @Autowired
    private QrcodeService qrcodeService;

//    @RequestMapping("/qrcode/redirect")
//    public String redirect(HttpServletResponse response) throws IOException {
//        //判断user Agent 做相应的跳转
//        response.sendRedirect("http://www.yunji2025.com");
//        return null;
//    }

    @ResponseBody
    @PostMapping("/qrcode/generate")
    public RespVo<Map<String, String>> generateQrCode(HttpServletRequest request, HttpServletResponse response) {
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


    @ResponseBody
    @PostMapping("/qrcode/validate")
    public RespVo<QrcodeStatus> validateQrCode(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, QRCODE_TICKET_KEY);
        String qrCodeTicket = cookie.getValue();
        QrcodeStatus status = qrcodeService.getQrcodeStatus(qrCodeTicket);
        return RespVo.success(status);
    }


}
