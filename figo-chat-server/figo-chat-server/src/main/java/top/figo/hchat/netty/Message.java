package top.figo.hchat.netty;

import top.figo.hchat.pojo.TbChatRecord;

/**
 * @Author Figo
 * @Date 2020/11/29 16:52
 */
public class Message {

    /**
     * 消息类型
     */
        private Integer type;
    /**
     * 聊天消息
     */
    private TbChatRecord chatRecord;
    /**
     * 扩展字段
     */
    private Object ext;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public TbChatRecord getChatRecord() {
        return chatRecord;
    }

    public void setChatRecord(TbChatRecord chatRecord) {
        this.chatRecord = chatRecord;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }
}
