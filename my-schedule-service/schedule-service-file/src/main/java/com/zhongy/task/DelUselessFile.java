package com.zhongy.task;

import com.zhongy.user.feign.UserFeign;
import com.zhongy.user.pojo.User;
import com.zhongy.util.FastDFSUtil;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DelUselessFile {

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 每次上传的图片不一定是用户最终用到的图片，为了节省空间  做一个定时任务，定时清理没有用到的图片
     * 调用userFeign老报错，暂时解决不了 TODO
     * @return
     */
//    0 0 0 */1 * ?
    @Scheduled(cron = "0 0 0 */1 * ?")
    public void DelFileFromRedis() throws Exception {
        /**
         * 读取redis的数据，与user集合对比，没有被使用的图片会被直接清除
         */
        Result<List<User>> all = userFeign.findAll();
        List<User> users = all.getData();
        Set keys = redisTemplate.boundHashOps("uploadFile").keys();
        //将所有匹配的key从keys中删除
        for (User user : users) {
            if (keys.contains(user.getHeadPicUrl())) {
                keys.remove(user.getHeadPicUrl());
            }
        }
        //将剩余的keys中的数据分别从redis 和 服务器中删除即可
        for (Object key : keys) {
            redisTemplate.boundHashOps("uploadFile").delete(key);
            String[] split = String.valueOf(key).split("/");
            FastDFSUtil.deleteFile(split[3], split[4] + "/" + split[5] + "/" + split[6] + "/" + split[7]);
        }
    }
}
