package com.ljt.exam.service;

import com.ljt.exam.model.Contest;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ContestService {

    int addContest(Contest contest);

    boolean updateContest(Contest contest);

    Contest getContestById(int id);

    Map<String,Object> getContests(int pageNum,int pageSize);

    boolean deleteContest(int id);

    boolean updateStateToStart();

    boolean updateStateToend();

    List<Contest> getContestsByContestIds(Set<Integer> contestIds);
}
