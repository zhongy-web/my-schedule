package com.zhongy.community.job;

import com.zhongy.community.pojo.Article;
import com.zhongy.community.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class LikeRedis2MySQL {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ArticleService articleService;

    /**
     * 定时将redis中记录的点赞数存储到MySQL中  2小时
     * @return
     */
    @Scheduled(cron = "0 0 */1 * * ?")
    public void redis2MySQL() throws Exception {
        Set keys = redisTemplate.opsForHash().keys("articleCount");
        for (Object key : keys) {
            Article article = articleService.findById((Integer) key);
            Integer articleCount = (Integer) redisTemplate.opsForHash().get("articleCount", key);
            //将数据写入，存储
            article.setLikeNum(articleCount + article.getLikeNum());
            redisTemplate.opsForHash().put("articleCount", key, 0);
            articleService.update(article);
        }
    }
}
