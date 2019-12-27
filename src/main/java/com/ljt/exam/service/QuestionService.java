package com.ljt.exam.service;

import com.ljt.exam.model.Question;

import java.util.List;
import java.util.Map;

public interface QuestionService {

    int addQuestion(Question question);

    boolean updateQuestion(Question question);

    List<Question> getQuestionsByContestId(int contestId);

    Map<String,Object> getQuestionsByContent(int pageNUm,int pageSize,String content);

    Map<String,Object> getQuestionsByProblemsetIdAndContentAndDiffculty(int pageNum,int pageSize,
                                                                        int problemsetId,
                                                                        String content,
                                                                        int diffcult);

    boolean deleteQuestion(int id);

    boolean updateQuestionsStateByContestId(int contestId,int state);
}
