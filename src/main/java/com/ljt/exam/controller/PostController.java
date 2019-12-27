package com.ljt.exam.controller;

import com.ljt.exam.dto.AjaxResult;
import com.ljt.exam.model.Post;
import com.ljt.exam.service.PostService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/post")
public class PostController {
    private static Log LOG = LogFactory.getLog(PostController.class);

    @Autowired
    PostService postService;

    /**
     * 添加帖子
     */
    @RequestMapping(value = "/api/addPost",method = RequestMethod.POST)
    public AjaxResult addPost(@RequestBody Post post){
        int postId = postService.addPost(post);
        return  new AjaxResult().setData(postId);
    }

    /**
     * 更新帖子
     * */
    @RequestMapping(value = "/api/updatePost",method = RequestMethod.POST)
    public AjaxResult updatePost(@RequestBody Post post){
        boolean result = postService.updatePostById(post);
        return  new AjaxResult().setData(result);
    }

    /**
     * 删除帖子
     * */
    @DeleteMapping("/api/deletePost/{id}")
    public AjaxResult deletePost(@PathVariable int id){
        boolean result = postService.deletePostById(id);
        return  new AjaxResult().setData(result);
    }

}
