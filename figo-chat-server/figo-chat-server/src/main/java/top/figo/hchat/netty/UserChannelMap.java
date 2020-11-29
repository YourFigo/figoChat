package top.figo.hchat.netty;

import io.netty.channel.Channel;

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
     * 移除用户ID与channel的关联
     * @param userid
     */
    public static void move(String userid){
        userChannelMap.remove(userid);
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
