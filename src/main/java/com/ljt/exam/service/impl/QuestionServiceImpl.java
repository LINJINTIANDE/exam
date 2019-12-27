package com.ljt.exam.service.impl;

import com.github.pagehelper.PageHelper;
import com.ljt.exam.common.LjtConst;
import com.ljt.exam.mapper.ContestMapper;
import com.ljt.exam.mapper.QuestionMapper;
import com.ljt.exam.model.Account;
import com.ljt.exam.model.Contest;
import com.ljt.exam.model.Post;
import com.ljt.exam.model.Question;
import com.ljt.exam.service.AccountService;
import com.ljt.exam.service.PostService;
import com.ljt.exam.service.QuestionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service("questionService")
public class QuestionServiceImpl implements QuestionService {
    private static Log LOG = LogFactory.getLog(QuestionServiceImpl.class);

    @Autowired
    QuestionMapper questionMapper;

    @Autowired
    PostService postService;

    @Autowired
    AccountService accountService;

    @Autowired
    ContestMapper contestMapper;

    @Override
    public int addQuestion(Question question) {
        if(question.getContestId() == 0){
            question.setState(1);
        }else{
            question.setState(0);
            Contest contest = contestMapper.getContestById(question.getContestId());
            contest.setTotalScore(contest.getTotalScore()+question.getScore());
            contestMapper.updateContestById(contest);
        }
        return  questionMapper.insertQuestion(question);
    }

    @Override
    public boolean updateQuestion(Question question) {
        if(question.getContestId() !=0){
            Contest contest = contestMapper.getContestById(question.getContestId());
            Question sourceQuestion = questionMapper.getQuestionById(question.getId());
            contest.setTotalScore(contest.getTotalScore()-sourceQuestion.getScore()+question.getScore());
            contestMapper.updateContestById(contest);
        }
        return questionMapper.updateQuestionById(question)>0;
    }

    @Override
    public List<Question> getQuestionsByContestId(int contestId) {
        return questionMapper.getQuestionByContestId(contestId);
    }

    @Override
    public Map<String, Object> getQuestionsByContent(int pageNum, int pageSize, String content) {
       Map<String,Object> data = new HashMap<>();
        int count = questionMapper.getCountByContent(content);
        if(count ==0){
            data.put("pageNum",0);
            data.put("pageSize",0);
            data.put("totalPageNum",1);
            data.put("totalPageSize",0);
            data.put("questionsSize",0);
            data.put("questions",new ArrayList<>());
            return  data;
        }
        int totalPageNum = count % pageSize ==0 ? count /pageSize :count/pageSize+1;
        if (pageNum > totalPageNum) {
            data.put("pageNum", 0);
            data.put("pageSize", 0);
            data.put("totalPageNum", totalPageNum);
            data.put("totalPageSize", 0);
            data.put("questionsSize", 0);
            data.put("questions", new ArrayList<>());
            return data;
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Question> questions = questionMapper.getQuestionsByContent(content);
        data.put("pageNum",pageNum);
        data.put("pageSize",pageSize);
        data.put("totalPageNum",totalPageNum);
        data.put("totalPageSize",count);
        data.put("questionsSize",questions.size());
        data.put("questions",questions);
        return data;
    }

    @Override
    public Map<String, Object> getQuestionsByProblemsetIdAndContentAndDiffculty(int pageNum, int pageSize, int problemsetId, String content, int difficulty) {
        Map<String,Object> data  = new HashMap<>();
        int count = questionMapper.getCountByProblemsetIdAndContentAndDiffculty(problemsetId,
                content, difficulty);
        if(count==0){
            data.put("pageNum",0);
            data.put("pageSize",0);
            data.put("totalPageNum",1);
            data.put("questionsSize",0);
            data.put("problemsetId",problemsetId);
            data.put("questions",new ArrayList<>());
            return  data;
        }
        int totalPageNum = count % pageSize ==0 ?count/pageSize:count/pageSize+1;
        if(pageNum > totalPageNum){
            data.put("pageNum",0);
            data.put("pageSize",0);
            data.put("totalPageNum",totalPageNum);
            data.put("totalPageSize",0);
            data.put("questionsSize",0);
            data.put("problemsetId",problemsetId);
            data.put("questions", new ArrayList<>());
            return data;
        }
        PageHelper.startPage(pageNum,pageSize);
        List<Question> questions = questionMapper.getQuestionsByProblemsetIdAndContentAndDiffculty(
                problemsetId, content, difficulty);
        data.put("pageNum", pageNum);
        data.put("pageSize", pageSize);
        data.put("totalPageNum", totalPageNum);
        data.put("totalPageSize", count);
        data.put("questionsSize", questions.size());
        data.put("problemsetId", problemsetId);
        data.put("questions", questions);
        return data;
    }

    @Override
    public boolean deleteQuestion(int id) {
        return questionMapper.deleteQuestion(id)>0;
    }

    @Override
    public boolean updateQuestionsStateByContestId(int contestId, int state) {
        return questionMapper.updateQuestionsStateByContestId(contestId,state)>0;
    }


}
