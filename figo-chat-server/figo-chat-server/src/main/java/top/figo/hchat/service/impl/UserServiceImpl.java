package top.figo.hchat.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import top.figo.hchat.mapper.TbFriendMapper;
import top.figo.hchat.mapper.TbFriendReqMapper;
import top.figo.hchat.mapper.TbUserMapper;
import top.figo.hchat.pojo.*;
import top.figo.hchat.pojo.vo.User;
import top.figo.hchat.service.UserService;
import top.figo.hchat.utils.FastDFSClient;
import top.figo.hchat.utils.IdWorker;
import top.figo.hchat.utils.QRCodeUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Author Figo
 * @Date 2020/11/18 22:14
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private FastDFSClient fastDFSClient;
    @Autowired
    private Environment env;
    @Autowired
    private QRCodeUtils qrCodeUtils;
    @Autowired
    private TbFriendMapper friendMapper;
    @Autowired
    private TbFriendReqMapper friendReqMapper;

    @Override
    public List<TbUser> findAll() {
        return userMapper.selectByExample(null);
    }

    @Override
    public User login(String username, String password) {
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)){
            TbUserExample example = new TbUserExample();
            TbUserExample.Criteria criteria = example.createCriteria();
            criteria.andUsernameEqualTo(username);

            List<TbUser> userList = userMapper.selectByExample(example);

            if (userList != null && userList.size() ==1){
                // 对密码进行校验
                String encodingPassword = DigestUtils.md5DigestAsHex(password.getBytes());
                if (encodingPassword.equals(userList.get(0).getPassword())){
                    User user = new User();
                    BeanUtils.copyProperties(userList.get(0),user);
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public void register(TbUser user) {
        try {
            // 判断用户名是否存在
            TbUserExample example = new TbUserExample();
            TbUserExample.Criteria criteria = example.createCriteria();
            criteria.andUsernameEqualTo(user.getUsername());
            List<TbUser> userList = userMapper.selectByExample(example);
            if (userList != null && userList.size() > 0){
                throw new RuntimeException("用户已存在");
            }
            // 将用户信息保存到数据库
            // 使用雪花算法生成唯一ID
            user.setId(idWorker.nextId());
            // 对密码进行MD5加密
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
            user.setPicSmall("");
            user.setPicNormal("");
            user.setNickname(user.getUsername());

            // 生成二维码，并将二维码的路径保存到数据库中
            // 二维码中要携带的信息
            String qrCodeStr = "hichat://" + user.getUsername();
            // 临时目录用来临时保存生成的二维码
            String tmpDir = env.getProperty("hcat.tmpdir");
            String qrCodeFilePath = tmpDir + user.getUsername() + ".png";
            qrCodeUtils.createQRCode(qrCodeFilePath, qrCodeStr);
            String qrCodeUrl = env.getProperty("fdfs.httpurl")
                    + fastDFSClient.uploadFile(new File(qrCodeFilePath));
            user.setQrcode(qrCodeUrl);

            user.setCreatetime(new Date());

            userMapper.insert(user);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("注册失败");
        }
    }

    @Override
    public User upload(MultipartFile file, String userId) {

        try {
            // 返回在FastDFS中的url路径，这个路径不带主机
            String url = fastDFSClient.uploadFace(file);
            // 在FastDFS上传的时候，自动生成缩略图，文件名_150x150.后缀
            String[] fileNameList = url.split("\\.");
            String fileName = fileNameList[0];
            String ext = fileNameList[1];

            String picSmallUrl = fileName + "_150x150." + ext;
            String prefix = env.getProperty("fdfs.httpurl");

            TbUser tbUser = userMapper.selectByPrimaryKey(userId);
            tbUser.setPicNormal(prefix + url);
            tbUser.setPicSmall(prefix + picSmallUrl);
            userMapper.updateByPrimaryKey(tbUser);

            User user = new User();
            BeanUtils.copyProperties(tbUser,user);

            return user;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateNickname(String id, String nickname) {
        if (StringUtils.isNotBlank(nickname)){
            TbUser tbUser = userMapper.selectByPrimaryKey(id);
            tbUser.setNickname(nickname);
            userMapper.updateByPrimaryKey(tbUser);
        }
        else {
            throw new RuntimeException("昵称不能为空");
        }
    }

    @Override
    public User findById(String userId) {
        TbUser tbUser = userMapper.selectByPrimaryKey(userId);
        User user = new User();
        BeanUtils.copyProperties(tbUser,user);
        return user;
    }

    @Override
    public User findByUsername(String userId, String friendUsername) {
        // 先查找用户信息
        TbUserExample userExample = new TbUserExample();
        TbUserExample.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andUsernameEqualTo(friendUsername);
        List<TbUser> userList = userMapper.selectByExample(userExample);
        TbUser friend = userList.get(0);

        User friendUser = new User();
        BeanUtils.copyProperties(friend,friendUser);
        return friendUser;
    }

}
