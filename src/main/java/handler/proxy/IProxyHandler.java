package handler.proxy;

import bean.ClientRequest;
import io.netty.channel.ChannelHandlerContext;

/**
 * 代理handler
 */
public interface IProxyHandler {
    /**
     * 发送到server
     *
     * @param clientRequest 客户端请求
     * @param ctx           ChannelHandlerContext
     * @param msg           数据
     */
    void sendToServer(ClientRequest clientRequest, final ChannelHandlerContext ctx, final Object msg);

    /**
     * 发送到client
     */
    void sendToClient(ClientRequest clientRequest, final ChannelHandlerContext ctx, final Object msg);
}
