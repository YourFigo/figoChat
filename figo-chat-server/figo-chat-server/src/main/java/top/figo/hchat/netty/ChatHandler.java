package top.figo.hchat.netty;

import com.alibaba.fastjson.JSON;;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;
import top.figo.hchat.pojo.TbChatRecord;
import top.figo.hchat.service.ChatRecordService;
import top.figo.hchat.utils.IdWorker;
import top.figo.hchat.utils.SpringUtil;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 处理消息的handler
 * TextWebSocketFrame: 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 用来保存所有的客户端连接
     */
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:MM");

    /**
     * 当Channel中有新的事件消息会自动调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 当接收到数据后会自动调用

        // 获取客户端发送过来的文本消息
        String text = msg.text();
        System.out.println("接收到消息数据为：" + text);

        Message message = JSON.parseObject(text, Message.class);

        // 通过SpringUtil工具类获取Spring上下文容器
        ChatRecordService chatRecordService = SpringUtil.getBean(ChatRecordService.class);
        IdWorker idWorker = SpringUtil.getBean(IdWorker.class);

        switch (message.getType()){
            case 0:
                // 建立用户与通道的关联
                String userid = message.getChatRecord().getUserid();
                UserChannelMap.put(userid, ctx.channel());
                System.out.println("建立用户：" + userid + "与通道" + ctx.channel().id() + "的关联");
                UserChannelMap.print();
                break;
            case 1:
                System.out.println("接收到用户消息");
                // 将聊天消息保存到数据库
                TbChatRecord tbChatRecord = message.getChatRecord();
                chatRecordService.insert(tbChatRecord);

                Channel channel = UserChannelMap.get(tbChatRecord.getFriendid());
                // 如果好友在线，可以直接将消息发送给好友
                if (channel != null){
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
                } else {
                    // 如果好友不在线，暂时不发送
                    System.out.println("用户：" + tbChatRecord.getFriendid() + "不在线");
                }
                break;
            case 2:
                // 处理客户端的签收信息
                // 将消息记录设置为已读
                chatRecordService.updateStatusHasRead(message.getChatRecord().getId());
                break;

        }
    }

    /**
     * 当有新的客户端连接服务器之后，会自动调用这个方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 将新的通道加入到clients
        clients.add(ctx.channel());
    }

    /**
     * 连接异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        UserChannelMap.removeByChannelId(ctx.channel().id().asLongText());
        ctx.channel().close();
    }

    /**
     * 连接断开处理
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("关闭通道");
        UserChannelMap.removeByChannelId(ctx.channel().id().asLongText());
        UserChannelMap.print();
    }

}
