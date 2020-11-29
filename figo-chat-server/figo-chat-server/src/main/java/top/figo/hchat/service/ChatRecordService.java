package top.figo.hchat.service;

import top.figo.hchat.pojo.TbChatRecord;

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
}
