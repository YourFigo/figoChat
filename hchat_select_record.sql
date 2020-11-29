-- 查发起好友请求的用户和请求到达的用户，以及请求状态
SELECT 
  fr.id AS friend_req_id,
  u1.`id` AS from_id,
  u1.`username` AS from_name,
  u2.`id` AS to_id,
  u2.`username` AS to_name,
  fr.`status` AS friend_req_status 
FROM
  tb_friend_req fr 
  LEFT JOIN tb_user u1 
    ON fr.`from_userid` = u1.`id` 
  LEFT JOIN tb_user u2 
    ON fr.`to_userid` = u2.`id` ;

