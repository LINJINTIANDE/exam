package com.ljt.exam.service;

import com.ljt.exam.model.Contest;
import com.ljt.exam.model.Grade;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface GradeService {

    int addGrade(Grade grade);

    boolean updateGrade(Grade grade);

    Map<String,Object> getGradesByStudentId(int pageNum,int pageSize,int studentId);

    List<Grade> getGradesByContestId(int countestId);
}
