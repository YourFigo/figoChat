package top.figo.hchat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.figo.hchat.pojo.TbFriendReq;
import top.figo.hchat.pojo.vo.FriendReq;
import top.figo.hchat.pojo.vo.Result;
import top.figo.hchat.pojo.vo.User;
import top.figo.hchat.service.FriendService;

import java.util.List;

/**
 * @Author Figo
 * @Date 2020/11/26 23:26
 */
@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;

    /**
     * 发送添加好友的请求
     * @param friendReq
     * @return
     */
    @RequestMapping("/sendRequest")
    public Result sendRequest(@RequestBody TbFriendReq friendReq){
        try {
            friendService.sendRequest(friendReq.getFromUserid(), friendReq.getToUserid());
            return new Result(true, "已申请");
        } catch (RuntimeException e) {
            return new Result(false, e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return new Result(false, "申请好友失败");
        }
    }

    /**
     * 展示添加好友的信息
     * @param userId
     * @return
     */
    @RequestMapping("/findFriendReqByUserid")
    public List<FriendReq> findFriendReqByUserid(String userId){
        return friendService.findFriendReqByUserid(userId);
    }

    /**
     * 接受好友请求
     * @param reqid
     * @return
     */
    @RequestMapping("/acceptFriendReq")
    public Result acceptFriendReq(String reqid){
        try {
            friendService.acceptFriendReq(reqid);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    /**
     * 忽略好友请求
     * @param reqid
     * @return
     */
    @RequestMapping("/ignoreFriendReq")
    public Result ignoreFriendReq(String reqid){
        try {
            friendService.ignoreFriendReq(reqid);
            return new Result(true,"忽略添加好友成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"忽略添加好友失败");
        }
    }

    /**
     * 获取通讯录信息
     * @param userid
     * @return
     */
    @RequestMapping("/findFriendByUserid")
    public List<User> findFriendByUserid(String userid){
        return friendService.findFriendByUserid(userid);
    }

}
