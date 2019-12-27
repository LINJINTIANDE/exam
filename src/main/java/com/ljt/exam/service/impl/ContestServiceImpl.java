package com.ljt.exam.service.impl;

import com.github.pagehelper.PageHelper;
import com.ljt.exam.mapper.ContestMapper;
import com.ljt.exam.mapper.SubjectMapper;
import com.ljt.exam.model.Contest;
import com.ljt.exam.model.Subject;
import com.ljt.exam.service.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("contestService")
public class ContestServiceImpl implements ContestService {
    @Autowired
    ContestMapper contestMapper;

    @Autowired
    SubjectMapper subjectMapper;

    @Override
    public int addContest(Contest contest) {
        contest.setTotalScore(0);
        contest.setState(0);
        return contestMapper.insertContest(contest);
    }

    @Override
    public boolean updateContest(Contest contest) {
        return contestMapper.updateContestById(contest)>0;
    }

    @Override
    public Contest getContestById(int id) {
        return contestMapper.getContestById(id);
    }

    @Override
    public Map<String, Object> getContests(int pageNum, int pageSize) {
        Map<String,Object> data = new HashMap<>();
        int count = contestMapper.getCount();
        if(count==0){
            data.put("pageNum",0);
            data.put("pageSize",0);
            data.put("totalPageNum",1);
            data.put("totalPageSize",0);
            data.put("contests", new ArrayList<>());
            return data;
        }
        int totalPageNum =count%pageSize==0?count/pageSize:count/pageSize+1;
        if (pageNum > totalPageNum) {
            data.put("pageNum", 0);
            data.put("pageSize", 0);
            data.put("totalPageNum", totalPageNum);
            data.put("totalPageSize", 0);
            data.put("contests", new ArrayList<>());
            return data;
        }

        List<Subject> subjects =subjectMapper.getSubjects();
        PageHelper.startPage(pageNum,pageSize);
        List<Contest> contests =contestMapper.getContests();
        Map<Integer,String> subjectId2name = subjects.stream()
                .collect(Collectors.toMap(Subject::getId,Subject::getName));
        for (Contest contest:contests){
            contest.setSubjectName(subjectId2name.getOrDefault(contest.getSubjectId(),"未知科目"));
        }
        data.put("pageNum", pageNum);
        data.put("pageSize", pageSize);
        data.put("totalPageNum", totalPageNum);
        data.put("totalPageSize", count);
        data.put("contests", contests);
        return data;
    }

    @Override
    public boolean deleteContest(int id) {
        return contestMapper.deleteContest(id)>0;
    }

    @Override
    public boolean updateStateToStart() {
        return contestMapper.updateStateToStart(new Date())>0;
    }

    @Override
    public boolean updateStateToend() {
        return contestMapper.updateStateToEnd(new Date())>0;
    }

    @Override
    public List<Contest> getContestsByContestIds(Set<Integer> contestIds) {
        return contestMapper.getContestsByContestIds(contestIds);
    }
}
