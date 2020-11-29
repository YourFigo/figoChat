package top.figo.hchat.controller;

import org.omg.PortableServer.LIFESPAN_POLICY_ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.figo.hchat.pojo.TbFriendReq;
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

    @RequestMapping("/findFriendReqByUserid")
    public List<User> findFriendReqByUserid(String userId){
        return friendService.findFriendReqByUserid(userId);
    }
}
