package top.figo.hchat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.figo.hchat.mapper.TbChatRecordMapper;
import top.figo.hchat.pojo.TbChatRecord;
import top.figo.hchat.service.ChatRecordService;
import top.figo.hchat.utils.IdWorker;

import java.util.Date;

/**
 * @Author Figo
 * @Date 2020/11/29 17:50
 */
@Service
@Transactional
public class ChatRecordServiceImpl implements ChatRecordService {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private TbChatRecordMapper tbChatRecordMapper;

    @Override
    public void insert(TbChatRecord tbChatRecord) {
        tbChatRecord.setId(idWorker.nextId());
        tbChatRecord.setHasRead(0);
        tbChatRecord.setCreatetime(new Date());
        tbChatRecord.setHasDelete(0);
        tbChatRecordMapper.insert(tbChatRecord);
    }
}
