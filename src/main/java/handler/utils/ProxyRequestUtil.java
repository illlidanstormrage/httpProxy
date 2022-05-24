package handler.utils;

import bean.ClientRequest;
import bean.Const;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.Attribute;

import static bean.Const.CLIENT_REQUEST_ATTRIBUTE_KEY;

/**
 * 代理请求工具类
 */
public class ProxyRequestUtil {
    private ProxyRequestUtil() {}

    /**
     * 获取代理请求
     *
     * @param httpRequest http请求
     */
    public static ClientRequest getClientRequest(HttpRequest httpRequest) {
        //从header中获取出host
        String host = httpRequest.headers().get("host");
        //从host中获取出端口
        String[] hostStrArr = host.split(":");
        int port = 80;
        if (hostStrArr.length > 1) {
            port = Integer.parseInt(hostStrArr[1]);
        } else if (httpRequest.uri().startsWith(Const.HTTPS_PROTOCOL_NAME)) {
            port = 443;
        }
        return new ClientRequest(hostStrArr[0], port);
    }

    /**
     * 从channel中获取clientRequest
     */
    public static ClientRequest getClientRequest(Channel channel) {
        //将clientRequest保存到channel中
        Attribute<ClientRequest> clientRequestAttribute = channel.attr(CLIENT_REQUEST_ATTRIBUTE_KEY);
        return clientRequestAttribute.get();
    }
}
