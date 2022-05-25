import handler.proxy.HttpProxyHandler;
import handler.proxy.HttpsProxyHandler;
import handler.proxy.SocksProxyHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 单例模式，启动代理服务器
 */
public class HttpProxyServer {

    private Logger logger = LoggerFactory.getLogger(HttpProxyServer.class);
    private static HttpProxyServer instance = new HttpProxyServer();

    public static HttpProxyServer getInstance() {
        if (instance == null) {
            instance = new HttpProxyServer();
        }
        return instance;
    }

    /**
     * 启动服务器
     * @param port 监听端口号
     */
    public void start(int port) {
        // 创建两个线程组 bossGroup、workerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 设置线程组
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 设置服务端通道实现类型
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置线程队列得到的连接个数
                    .option(ChannelOption.TCP_NODELAY, true) // 控制开启Nagle算法，该算法要求一个TCP连接上最多只能有一个未被确认的小分组，在该小分组的确认到来之前，不能发送其他小分组
//                    .handler(new LoggingHandler(LogLevel.INFO)) // 记录所有日志
                    .childHandler(new ChannelInitializer<>() { // 通过匿名内部类初始化通道对象

                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            //接收客户端请求，将客户端的请求内容解码
                            channel.pipeline().addLast("httpRequestDecoder", new HttpRequestDecoder());
                            //发送响应给客户端，并发送内容编码
                            channel.pipeline().addLast("httpResponseEncoder", new HttpResponseEncoder());
                            channel.pipeline().addLast("httpAggregator", new HttpObjectAggregator(65536));
                            channel.pipeline().addLast("httpProxyHandler", new HttpProxyHandler());
                            channel.pipeline().addLast("httpsProxyHandler", new HttpsProxyHandler());
                            channel.pipeline().addLast("socksProxyHandler", new SocksProxyHandler());
                        }
                    });
            logger.info("[HttpProxyServer] proxy server start on {} port", port);
            ChannelFuture channelFuture = bootstrap.bind(port).sync(); // 绑定端口号，进行监听
            channelFuture.channel().closeFuture().sync(); // 对关闭通道监听
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        System.out.println("server start");
        int port = 6667;
        if(args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new HttpProxyServer().start(port);
    }

}