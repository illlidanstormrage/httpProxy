package handler.response;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * https代理responseHandler
 */
public class HttpProxyResponseHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(HttpProxyResponseHandler.class);
    private Channel clientChannel;

    public HttpProxyResponseHandler(Channel clientChannel) {
        this.clientChannel = clientChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            logger.debug("[channelRead][FullHttpResponse] 接收到远程的数据1 content:{}", response.content().toString(Charset.defaultCharset()));
        } else if (msg instanceof DefaultHttpResponse) {
            DefaultHttpResponse response = (DefaultHttpResponse) msg;
            logger.debug("[channelRead][FullHttpResponse] 接收到远程的数据 content:{}", response.toString());
        } else if (msg instanceof DefaultHttpContent) {
            DefaultHttpContent httpContent = (DefaultHttpContent) msg;
            logger.debug("[channelRead][DefaultHttpContent] 接收到远程的数据 content:{}", httpContent.content().toString(Charset.defaultCharset()));
        } else {
            logger.debug("[channelRead] 接收到远程的数据 " + msg.toString());
        }
        //发送给客户端
        clientChannel.writeAndFlush(msg);
    }
}
