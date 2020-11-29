package top.figo.hchat.netty;

import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于建立用户ID与通道的关联
 * @Author Figo
 * @Date 2020/11/29 16:58
 */
public class UserChannelMap {

    /**
     * 用于保存用户ID与通道的Map对象
     */
    private static Map<String, Channel> userChannelMap;

    static {
        userChannelMap = new HashMap<String, Channel>();
    }

    /**
     * 添加用户ID与channel的关联
     * @param userid
     * @param channel
     */
    public static void put(String userid, Channel channel){
        userChannelMap.put(userid, channel);
    }

    /**
     * 根据用户id移除用户ID与channel的关联
     * @param userid
     */
    public static void remove(String userid){
        userChannelMap.remove(userid);
    }

    /**
     * 根据通道id移除用户ID与channel的关联
     * @param channelId
     */
    public static void removeByChannelId(String channelId){
        if (StringUtils.isBlank(channelId)){
            return;
        }
        for (String s : userChannelMap.keySet()) {
            Channel channel = userChannelMap.get(s);
            if (channelId.equals(channel.id().asLongText())){
                userChannelMap.remove(s);
                System.out.println("客户端连接断开，取消用户：" + s + "与通道：" + channelId + "的关联");
                break;
            }
        }
    }

    /**
     * 打印
     */
    public static void print() {
        userChannelMap.keySet()
                .forEach(
                        userid ->
                                System.out.println("用户id：" + userid + "通道：" + userChannelMap.get(userid).id())
                );
    }
}
