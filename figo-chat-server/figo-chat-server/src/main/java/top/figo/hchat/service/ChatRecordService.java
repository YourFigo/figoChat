package top.figo.hchat.service;

import top.figo.hchat.pojo.TbChatRecord;

import java.util.List;

/**
 * @Author Figo
 * @Date 2020/11/29 17:50
 */
public interface ChatRecordService {

    /**
     * 将聊天记录保存到数据库中
     * @param tbChatRecord
     */
    void insert(TbChatRecord tbChatRecord);

    /**
     * 根据用户id和好友id查询聊天记录
     * @param userid
     * @param friendid
     * @return
     */
    List<TbChatRecord> findByUserIdAndFriendId(String userid, String friendid);
}
