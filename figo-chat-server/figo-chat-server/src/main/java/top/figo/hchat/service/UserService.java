package top.figo.hchat.service;

import org.springframework.web.multipart.MultipartFile;
import top.figo.hchat.pojo.TbUser;
import top.figo.hchat.pojo.vo.User;

import java.util.List;

/**
 * @Author Figo
 * @Date 2020/11/18 22:13
 */
public interface UserService {

    /**
     * 返回所有用户
     * @return
     */
    List<TbUser> findAll();

    /**
     * 用来登录检查用户名和密码是否匹配
     * @param username
     * @param password
     * @return 如果登录成功，返回用户对象否则返回null
     */
    User login(String username, String password);

    /**
     * 注册用户，将用户信息保存到数据库，如果抛出异常则注册失败
     * @param user 用户信息
     */
    void register(TbUser user);

    /**
     * 上传头像
     * @param file 客户端上传的文件
     * @param userId 用户id
     * @return 如果上传成功返回用户信息，否则返回null
     */
    User upload(MultipartFile file, String userId);

    /**
     * 更新昵称
     * @param id
     * @param nickname
     */
    void updateNickname(String id, String nickname);

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
    User findById(String userId);

    /**
     * 根据用户名搜索用户（好友搜索）
     * 在搜索用户的时候不需要校验
     * @param userId 要添加好友的用户id
     * @param friendUsername 要添加的好友
     * @return
     */
    User findByUsername(String userId, String friendUsername);
}
