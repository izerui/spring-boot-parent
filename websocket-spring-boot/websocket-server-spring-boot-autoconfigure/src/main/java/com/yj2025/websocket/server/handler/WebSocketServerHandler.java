package com.yj2025.websocket.server.handler;

import com.yj2025.websocket.server.impl.UserChannelService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by serv on 2015/4/20.
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketServerHandler.class);

    private UserChannelService userService;

    public WebSocketServerHandler(UserChannelService userService) {
        this.userService = userService;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        userService.disconnect(ctx.channel());
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.info(cause.getMessage(), cause);
        try {
            userService.disconnect(ctx.channel());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        ctx.close();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            ctx.close();
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            userService.activeConnect(ctx.channel());
            return;
        }
        if (frame instanceof PongWebSocketFrame) {
            userService.activeConnect(ctx.channel());
            return;
        }
        if (frame instanceof TextWebSocketFrame) {
            String text = ((TextWebSocketFrame) frame).text();
            if (StringUtils.equals(text, "ping")) {
                ctx.writeAndFlush(new TextWebSocketFrame("pong"));
                return;
            }
            userService.connect(ctx.channel(), text);
            return;
        }
    }
}
