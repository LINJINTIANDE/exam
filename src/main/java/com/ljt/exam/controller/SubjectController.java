package com.ljt.exam.controller;

import com.ljt.exam.common.LjtConst;
import com.ljt.exam.dto.AjaxResult;
import com.ljt.exam.model.Subject;
import com.ljt.exam.service.SubjectService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/subject")
public class SubjectController {
    private static Log LOG = LogFactory.getLog(SubjectController.class);

    @Autowired
    private SubjectService subjectService;

    /**
     * 添加课程
     * */
    @RequestMapping(value = "/api/addSubject",method = RequestMethod.POST)
    public AjaxResult addSubject(@RequestBody Subject subject){
        AjaxResult ajaxResult = new AjaxResult();
        subject.setImgUrl(LjtConst.DEFAULT_SUBJECT_IMG_URL);
        subject.setQuestionNum(0);
        int subjectId = subjectService.addSubject(subject);
        return  new AjaxResult().setData(subjectId);
    }

    /**
     * 更新课程
     * */
    @RequestMapping(value = "/api/updateSubject",method = RequestMethod.POST)
    public AjaxResult updateSubject(@RequestBody Subject subject){

        boolean result = subjectService.updateSubject(subject);
        return  new AjaxResult().setData(result);
    }

    /**
     * 删除课程
     * */
    @DeleteMapping("/api/deleteSubject/{id}")
    public AjaxResult deleteSubject(@PathVariable("id") int id){
        boolean result = subjectService.deleteSubjectById(id);
        return new AjaxResult().setData(result);
    }
}

