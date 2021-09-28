package com.zhongy.community.annotation;

import com.alibaba.fastjson.JSONArray;
import com.zhongy.community.advice.TokenException;
import com.zhongy.community.pojo.Article;
import com.zhongy.community.pojo.Comment;
import com.zhongy.community.service.ArticleService;
import com.zhongy.community.service.CommentService;
import entity.JwtUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class TokenAuthAspect implements Ordered {
    @Autowired
    private ArticleService articleService;

    @Autowired
    private CommentService commentService;

    @Pointcut("execution(* com.zhongy.community.controller.CommentController.delete())")
    public void permissionTest() {}

    @Before("permissionTest()")
    public void beforePointcut(JoinPoint joinPoint) {
        String comName = null;
        String artName = null;
        //得到类名
        String simpleName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        if (simpleName.equals("ArticleController")) {
            Article article = articleService.findById((Integer) joinPoint.getArgs()[0]);
            artName = article.getName();
        }

        if (simpleName.equals("CommentController")) {
            Comment comment = commentService.findById((Integer) joinPoint.getArgs()[0]);
            Integer articleId = comment.getArticleId();
            Article article = articleService.findById(articleId);
            comName = comment.getName();
            artName = article.getName();
        }

        JSONArray auth = JwtUtil.getAuths();
        String username = JwtUtil.getUserInfo();
        //只有本人和管理员可以访问该接口
        //先判断是否为管理员
        if (!auth.contains("ROLE_ADMIN")) {
            //文章是否为本人操作
            if (!username.equals(artName) && simpleName.equals("ArticleController")) {
                //TODO 这里先做运行时异常，后面自定义异常并处理即可
                throw new TokenException("无权访问！！！");
            }
            //评论是否为文章本人操作
            if (!username.equals(artName) && simpleName.equals("CommentController")) {
                //是否为评论本人操作
                if (!username.equals(comName))
                    throw new TokenException("无权访问！！！");
            }
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
