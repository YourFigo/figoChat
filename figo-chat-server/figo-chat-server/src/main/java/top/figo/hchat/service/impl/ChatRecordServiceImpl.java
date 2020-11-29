package top.figo.hchat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.figo.hchat.mapper.TbChatRecordMapper;
import top.figo.hchat.pojo.TbChatRecord;
import top.figo.hchat.pojo.TbChatRecordExample;
import top.figo.hchat.service.ChatRecordService;
import top.figo.hchat.utils.IdWorker;

import java.util.Date;
import java.util.List;

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

    @Override
    public List<TbChatRecord> findByUserIdAndFriendId(String userid, String friendid) {
        TbChatRecordExample tbChatRecordExample = new TbChatRecordExample();

        // 将userid -> friendid 的聊天记录查出来
        TbChatRecordExample.Criteria criteria1 = tbChatRecordExample.createCriteria();
        criteria1.andUseridEqualTo(userid);
        criteria1.andFriendidEqualTo(friendid);
        criteria1.andHasDeleteEqualTo(0);

        // 将friendid -> userid 的聊天记录查出来
        TbChatRecordExample.Criteria criteria2  = tbChatRecordExample.createCriteria();
        criteria2.andUseridEqualTo(friendid);
        criteria2.andFriendidEqualTo(userid);
        criteria2.andHasDeleteEqualTo(0);

        // 将发给userid的所有聊天记录设置为已读
        TbChatRecordExample exampleQuerySendToMe = new TbChatRecordExample();
        TbChatRecordExample.Criteria criteriaQuerySendToMe = exampleQuerySendToMe.createCriteria();
        criteriaQuerySendToMe.andFriendidEqualTo(userid);
        criteriaQuerySendToMe.andHasReadEqualTo(0);
        tbChatRecordMapper.selectByExample(exampleQuerySendToMe).stream().forEach(tbChatRecord -> {
            tbChatRecord.setHasRead(1);
            tbChatRecordMapper.updateByPrimaryKey(tbChatRecord);
        });

        tbChatRecordExample.or(criteria1);
        tbChatRecordExample.or(criteria2);
        return tbChatRecordMapper.selectByExample(tbChatRecordExample);
    }

    @Override
    public List<TbChatRecord> findUnreadByUserid(String userid) {
        TbChatRecordExample tbChatRecordExample = new TbChatRecordExample();
        TbChatRecordExample.Criteria criteria = tbChatRecordExample.createCriteria();

        criteria.andFriendidEqualTo(userid);
        criteria.andHasReadEqualTo(0);
        return tbChatRecordMapper.selectByExample(tbChatRecordExample);
    }

    @Override
    public void updateStatusHasRead(String id) {
        TbChatRecord tbChatRecord = tbChatRecordMapper.selectByPrimaryKey(id);
        tbChatRecord.setHasRead(1);
        tbChatRecordMapper.updateByPrimaryKey(tbChatRecord);
    }
}
