package com.tencent.community.service;

import com.tencent.community.util.CommunityConstant;
import com.tencent.community.util.HostHolder;
import com.tencent.community.util.LikeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstant {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    HostHolder hd;

    @Autowired
    UserService us;

    // 关注
    public void follow(int userId, int entityId, int entityType){
        String followeeKey = LikeUtils.getFolloweeKey(userId, entityType);
        String followerKey = LikeUtils.getFollowerKey(entityType, entityId);

        // 更新关注数和粉丝数/被关注数
        // ?????? SessionCallback的接口指定泛型了，重写方法改变不了泛型？？？？？？？？？
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                redisTemplate.multi();
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                return redisTemplate.exec();
            }
        });
    }

    // 取关
    public void unfollow(int userId, int entityId, int entityType){
        String followeeKey = LikeUtils.getFolloweeKey(userId, entityType);
        String followerKey = LikeUtils.getFollowerKey(entityType, entityId);

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                redisTemplate.multi();
                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);
                return redisTemplate.exec();
            }
        });

        // 指定泛型
//        redisTemplate.execute(new SessionCallback<Object>() {
//            @Override
//            public <String, Object> Object execute(RedisOperations<String, Object> operations) throws DataAccessException {
//                return null;
//            }
//        });



    }


    // 关注数量
    public long followeeCount(int userId, int entityType){
        String followeeKey = LikeUtils.getFolloweeKey(userId, entityType);
        Long aLong = redisTemplate.opsForZSet().zCard(followeeKey);
        long count = aLong == null ? 0 : aLong;
        return count;
    }

    // 粉丝/被关注数量
    public long followerCount(int entityType, int entityId){
        String followerKey = LikeUtils.getFollowerKey(entityType, entityId);
        Long aLong = redisTemplate.opsForZSet().zCard(followerKey);
        long count = aLong == null ? 0 : aLong;
        return count;
    }

    // 是否关注
    public boolean isFollowed(int entityType, int entityId){
        String followeeKey = LikeUtils.getFolloweeKey(hd.get().getId(), entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    // 查询某用户的关注用户
    public List<Map<String, Object>> getFolloweeUser(int userId, int offset, int limit){
        String followeeKey = LikeUtils.getFolloweeKey(userId, ENTITY_TYPE_USER);
        // redis返回的会自个实现set接口的具有有序性的实现类：
        Set<Integer> setId = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if(setId == null){
            return null;
        }
        List<Map<String, Object>> res = new ArrayList<>();
        for(Integer id : setId){
            Map<String, Object> map = new HashMap<>();
            map.put("followeeuser", us.findUserById(id));
            // Double.longvalue 可把小数转为long整数  double8个字节，其中有几个字节表示小数部分，long4个字节
            Double time = redisTemplate.opsForZSet().score(followeeKey, id);
            long longTime = time.longValue();
            map.put("time", longTime);
            res.add(map);
        }
        return res;
    }


    // 查询某用户的粉丝用户
    public List<Map<String, Object>> getFollowUser(int userId, int offset, int limit){
        String followKey = LikeUtils.getFollowerKey(ENTITY_TYPE_USER, userId);
        // redis返回的会自个实现set接口的具有有序性的实现类：
        Set<Integer> setId = redisTemplate.opsForZSet().reverseRange(followKey, offset, offset + limit - 1);
        if(setId == null){
            return null;
        }
        List<Map<String, Object>> res = new ArrayList<>();
        for(Integer id : setId){
            Map<String, Object> map = new HashMap<>();
            map.put("followuser", us.findUserById(id));
            // Double.longvalue 可把小数转为long整数  double8个字节，其中有几个字节表示小数部分，long4个字节
            Double time = redisTemplate.opsForZSet().score(followKey, id);
            long longTime = time.longValue();
            map.put("time", longTime);
            res.add(map);
        }
        return res;
    }
}
