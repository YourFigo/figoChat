package top.figo.hchat.service;

import top.figo.hchat.pojo.vo.FriendReq;
import top.figo.hchat.pojo.vo.User;

import java.util.List;

/**
 * @Author Figo
 * @Date 2020/11/26 23:29
 */
public interface FriendService {
    /**
     * 发送好友请求
     * @param fromUserid
     * @param toUserid
     */
    void sendRequest(String fromUserid, String toUserid);

    /**
     * 根据用户id查询他对应的好友请求
     * @param userId 当前登录的用户
     * @return 发起好友请求的用户信息
     */
    List<FriendReq> findFriendReqByUserid(String userId);

    /**
     * 接受好友请求
     * @param reqid
     */
    void acceptFriendReq(String reqid);

}
