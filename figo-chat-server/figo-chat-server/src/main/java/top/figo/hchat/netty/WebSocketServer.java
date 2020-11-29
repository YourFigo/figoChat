package top.figo.hchat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

@Component
public class WebSocketServer {

    /**
     * 主线程池
     */
    private EventLoopGroup bossGroup;
    /**
     * 工作线程池
     */
    private EventLoopGroup workerGroup;
    /**
     * 服务器
     */
    private ServerBootstrap server;
    /**
     * 回调
     */
    private ChannelFuture future;

    public void start() {
        future = server.bind(9001);
        System.out.println("netty server - 启动成功");
    }

    public WebSocketServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        server = new ServerBootstrap();
        server.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WebsocketInitializer());
    }
}
