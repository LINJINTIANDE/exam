package com.ljt.exam.controller;

import com.ljt.exam.dto.AjaxResult;
import com.ljt.exam.model.Comment;
import com.ljt.exam.service.CommentService;
import com.ljt.exam.service.PostService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springsource.loaded.C;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private static Log LOG = LogFactory.getLog(CommentController.class);

    @Autowired
    CommentService commentService;

    @Autowired
    PostService postService;

    /**
     * 添加评论
     * */
    @RequestMapping(value = "/api/addComment",method = RequestMethod.POST)
    public AjaxResult addComment(@RequestBody Comment comment){
        postService.updateReplyNumById(comment.getPostId());
        int commentId = commentService.addComment(comment);
        return  new AjaxResult().setData(commentId);
    }

    /**
     * 删除评论
     * */
    @DeleteMapping("/api/deleteComment/{id}")
    public AjaxResult deleteComment(@PathVariable int id){
        boolean result = commentService.deleteCommentById(id);
        return  new AjaxResult().setData(result);
    }
}
