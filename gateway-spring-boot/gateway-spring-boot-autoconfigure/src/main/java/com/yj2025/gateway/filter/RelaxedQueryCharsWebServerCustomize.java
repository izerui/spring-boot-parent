package com.yj2025.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import reactor.netty.ConnectionObserver;
import reactor.netty.NettyPipeline;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RelaxedQueryCharsWebServerCustomize implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

    /**
     * 需要encode的特殊字符
     */
    private final List<Character> charList = new ArrayList<>() {
        {
            this.add('{');
            this.add('}');
            this.add('[');
            this.add(']');
        }
    };


    @Override
    public void customize(NettyReactiveWebServerFactory factory) {
        factory.addServerCustomizers(httpServer ->
                httpServer.observe((conn, state) -> {
                    if (state == ConnectionObserver.State.CONNECTED) {
                        conn.channel().pipeline().addAfter(NettyPipeline.HttpCodec, "relaxedQueryChars", new QueryHandler());
                    }
                }));
    }


    class QueryHandler extends ChannelInboundHandlerAdapter {

        public QueryHandler() {
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            if (msg instanceof HttpRequest) {
                HttpRequest request = (HttpRequest) msg;
                String url = request.uri();
                // fix url
                log.debug("url: {}", url);
                String[] split = url.split("\\?");
                StringBuilder fixUrl = new StringBuilder(split[0]);
                if (split.length > 1) {
                    fixUrl.append("?");
                    char[] chars = split[1].toCharArray();
                    for (char aChar : chars) {
                        if (charList.contains(aChar)) {
                            fixUrl.append(URLEncoder.encode(String.valueOf(aChar), StandardCharsets.UTF_8));
                        } else {
                            fixUrl.append(aChar);
                        }
                    }
                }
                log.debug("fixUrl: {}", fixUrl);
                request.setUri(fixUrl.toString());
            }
            ctx.fireChannelRead(msg);
        }
    }
}

