package com.ljt.exam.service;

import com.ljt.exam.model.Subject;

import java.util.List;
import java.util.Map;

public interface SubjectService {

    int addSubject(Subject subject);

    boolean updateSubject(Subject subject);

    Subject getSubjectById(int id);

    Map<String,Object> getSubjects(int pageNum,int pageSize);

    List<Subject> getSubjects();

    boolean deleteSubjectById(int id);
}
