package com.ljt.exam.mapper;

import com.ljt.exam.model.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface GradeMapper {

    int insertGrade(@Param("grade") Grade grade);

    int getCountByStudentId(@Param("studentId") int studentId);

    int updateGradeById(@Param("grade") Grade grade);

    List<Grade> getGradesByStudentId(@Param("studentId") int studentId);

    List<Grade> getGradesByContestId(@Param("contestId") int contestId);
}
