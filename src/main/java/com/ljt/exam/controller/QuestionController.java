package com.ljt.exam.controller;

import com.ljt.exam.dto.AjaxResult;
import com.ljt.exam.model.Question;
import com.ljt.exam.service.QuestionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/question")
public class QuestionController {
    private static Log LOG = LogFactory.getLog(QuestionController.class);

    @Autowired
    private QuestionService questionService;

    /**
     * 添加题目
     * */
    @RequestMapping(value = "api/addQuestion",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult addQuestion(@RequestBody Question question){
        AjaxResult ajaxResult = new AjaxResult();
        int questionId = questionService.addQuestion(question);
        return new AjaxResult().setData(questionId);
    }

    /**
     * 更新题目信息
     * */
    @RequestMapping(value = "/api/updateQuestion",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult updateQuestion(@RequestBody Question question){
        AjaxResult ajaxResult = new AjaxResult();
        boolean result = questionService.updateQuestion(question);
        return new AjaxResult().setData(result);

    }

    /**
     * 删除题目信息
     * */
    @DeleteMapping("/api/deleteQuestion/{id}")
    public AjaxResult deleteContest(@PathVariable("id") int id){
        AjaxResult ajaxResult = new AjaxResult();
        boolean result = questionService.deleteQuestion(id);
        return  new AjaxResult().setData(result);
    }
}
