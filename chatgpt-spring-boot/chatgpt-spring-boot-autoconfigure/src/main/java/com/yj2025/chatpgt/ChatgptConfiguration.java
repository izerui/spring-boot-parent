package com.yj2025.chatpgt;

import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.util.Proxys;

import java.net.Proxy;

public class ChatgptConfiguration {

    public static void main(String[] args) {
         //国内需要代理
      Proxy proxy = Proxys.http("127.0.0.1", 7890);
     //socks5 代理
//     Proxy proxy = Proxys.socks5("127.0.0.1", 7890);

      ChatGPT chatGPT = ChatGPT.builder()
                .apiKey("xxx")
                .proxy(proxy)
                .apiHost("https://api.openai.com/") //反向代理地址
                .build()
                .init();

        String res = chatGPT.chat("写一段七言绝句诗，题目是：火锅！");
        System.out.println(res);
    }
}
