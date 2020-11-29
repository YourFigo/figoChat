package top.figo.hchat.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.figo.hchat.mapper.TbFriendMapper;
import top.figo.hchat.mapper.TbFriendReqMapper;
import top.figo.hchat.mapper.TbUserMapper;
import top.figo.hchat.pojo.*;
import top.figo.hchat.pojo.vo.User;
import top.figo.hchat.service.FriendService;
import top.figo.hchat.utils.IdWorker;

import javax.jws.soap.SOAPBinding;
import java.nio.channels.UnresolvedAddressException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Figo
 * @Date 2020/11/26 23:34
 */
@Service
@Transactional
public class FriendServiceImpl implements FriendService {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private TbFriendMapper friendMapper;
    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private TbFriendReqMapper friendReqMapper;

    @Override
    public void sendRequest(String fromUserid, String toUserid) {
        TbUser friend = userMapper.selectByPrimaryKey(toUserid);
        // 检查是否允许添加好友
        checkAllowToAddFriend(fromUserid, friend);

        // 发送添加好友请求
        TbFriendReq friendReq = new TbFriendReq();
        friendReq.setId(idWorker.nextId());
        friendReq.setFromUserid(fromUserid);
        friendReq.setToUserid(toUserid);
        friendReq.setCreatetime(new Date());
        friendReq.setStatus(0);

        friendReqMapper.insert(friendReq);
    }

    /**
     * 检查是否允许添加好友
     * @param userId 要添加好友的用户id
     * @param friend 要添加的好友
     */
    private void checkAllowToAddFriend(String userId, TbUser friend){
        // 用户不能添加自己为好友
        if (friend.getId().equals(userId)){
            throw new RuntimeException("不能添加自己为好友");
        }

        // 用户不能重复添加
        TbFriendExample friendExample = new TbFriendExample();
        TbFriendExample.Criteria friendCriteria = friendExample.createCriteria();
        friendCriteria.andUseridEqualTo(userId);
        friendCriteria.andFriendsIdEqualTo(friend.getId());
        List<TbFriend> friendList = friendMapper.selectByExample(friendExample);
        if (friendList != null && friendList.size() > 0){
            throw new RuntimeException(friend.getUsername() + "已经是您的好友");
        }

        // 判断是否已经提交好友申请，如果已经提交好友申请，就直接抛出
        TbFriendReqExample friendReqExample = new TbFriendReqExample();
        TbFriendReqExample.Criteria friendReqCriteria = friendReqExample.createCriteria();
        // 当前用户发送的好友请求
        friendReqCriteria.andFromUseridEqualTo(userId);
        // 请求目标为指定好友
        friendReqCriteria.andToUseridEqualTo(friend.getId());
        // 并且这个好友请求没有被处理
        friendReqCriteria.andStatusEqualTo(0);
        List<TbFriendReq> friendReqList = friendReqMapper.selectByExample(friendReqExample);
        if (friendReqList != null && friendReqList.size() > 0){
            throw new RuntimeException("已经申请过了");
        }
    }

    @Override
    public List<User> findFriendReqByUserid(String userId) {
        // 根据用户id查询对应的好友请求
        TbFriendReqExample friendReqExample = new TbFriendReqExample();
        TbFriendReqExample.Criteria friendReqCriteria = friendReqExample.createCriteria();
        friendReqCriteria.andToUseridEqualTo(userId);
        friendReqCriteria.andStatusEqualTo(0);
        List<TbFriendReq> friendReqList = friendReqMapper.selectByExample(friendReqExample);

        // 根据好友请求，将发起好友请求的用户信息返回
        List<User> friendUserList = friendReqList.stream()
                .map(friendReq -> {
                    TbUser tbUser = userMapper.selectByPrimaryKey(friendReq.getFromUserid());
                    User user = new User();
                    BeanUtils.copyProperties(tbUser, user);
                    return user;
                }).collect(Collectors.toList());

        return friendUserList;
    }
}