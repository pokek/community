package com.tencent.community.service;

import com.tencent.community.util.LikeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {


    @Autowired
    RedisTemplate redisTemplate;

    // 未赞时候点赞就更新redis，已赞时候点赞就取消 删除redis数据   并且更新用户点赞数
    public void updateRedisLikeCount(int userId, int entityId, int entityType, int postUserId){

        String redisKey = LikeUtils.getKey(entityType, entityId);
        String postUserKey = LikeUtils.getLikeUserKey(postUserId);
        // set集合存入用户id，可显示被那些用户点赞
        // set 集合中有该用户 就删除，表示已取消点赞

        // 事务 处理 帖子，评论点赞数，和 用户点赞数
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                // redis没有事务，但将命令放入缓存队列之后，提交后才进行，且不保证事务的原子性，
                // 不存在事务内的查询要看到事务里的更新，事务外查询不能看到。
                Boolean isExists = operations.opsForSet().isMember(redisKey, userId);
                redisTemplate.multi();
                if(isExists){
                    operations.opsForSet().remove(redisKey, userId);
                    operations.opsForValue().decrement(postUserKey);
                }else{
                    operations.opsForSet().add(redisKey, userId);
                    operations.opsForValue().increment(postUserKey);
                }
                return redisTemplate.exec();
            }
        });

    }

    // 返回数量
    public Long likeCount(int entityId, int entityType){
        String redisKey = LikeUtils.getKey(entityType, entityId);
        return redisTemplate.opsForSet().size(redisKey);
    }

    // 判断用户对该帖子或评论的状态 -1 表示点踩
    public int likeStatus(int userId, int entityId, int entityType){
        String redisKey = LikeUtils.getKey(entityType, entityId);
        // 1 表示已点赞， 0 表示未点赞
        return redisTemplate.opsForSet().isMember(redisKey, userId) ? 1 : 0;
    }

    // 返回用户总的点赞数
    public int getUserLikeCount(int userId){
        String likeUserKey = LikeUtils.getLikeUserKey(userId);
        // 当在redis找不存在的key是 结果返回null
        Object o = redisTemplate.opsForValue().get(likeUserKey);
        return  o == null ? 0 : (Integer) o;
    }
}
