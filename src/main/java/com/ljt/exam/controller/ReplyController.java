package com.ljt.exam.controller;


import com.ljt.exam.dto.AjaxResult;
import com.ljt.exam.model.Reply;
import com.ljt.exam.service.ReplyService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reply")
public class ReplyController {
    private static Log LOG = LogFactory.getLog(ReplyController.class);

    @Autowired
    ReplyService replyService;

    @RequestMapping(value = "/api/addReply",method = RequestMethod.POST)
    public AjaxResult addReply(@RequestBody Reply reply){
        int result = replyService.addReply(reply);
        return  new AjaxResult().setData(result);
    }
}
