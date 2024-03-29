package com.ljt.exam.controller;

import com.ljt.exam.common.LjtConst;
import com.ljt.exam.model.*;
import com.ljt.exam.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/manage")
public class ManageController {
    private Log LOG = LogFactory.getLog(ManageController.class);


    @Autowired
    ContestService contestService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    QuestionService questionService;

    @Autowired
    AccountService accountService;

    @Autowired
    PostService postService;

    @Autowired
    CommentService commentService;

    @Autowired
    GradeService gradeService;

    /**
     * 管理员登录页面
     * */
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String login(HttpServletRequest request, Model model){
     Account currentAccount = (Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
     model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);

     if(currentAccount == null){
         return "/manage/manage-login";
     }else {
         return "redirect:/manage/contest/list";
     }
    }


    /**
     * 考试管理
     * */
    @RequestMapping(value = "/contest/list",method = RequestMethod.GET)
    public String contestList(HttpServletRequest request,
                              @RequestParam(value = "page",defaultValue = "1")int page,
                              Model model){
        Account currentAccount = (Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        //TODO::处理
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        if(currentAccount == null || currentAccount.getLevel()<1){
            return "/error/404";
        }else {
            Map<String,Object>data =contestService.getContests(page,LjtConst.contestPageSize);
            List<Subject> subjects = subjectService.getSubjects();
            data.put("subjects",subjects);
            model.addAttribute(LjtConst.DATA,data);
            return "/manage/manage-contestBoard";
        }

    }

    /**
     * 考试管理-查看试题
     * */
    @RequestMapping(value = "/contest/{contestId}/problems",method = RequestMethod.GET)
    public String contestProblemList(HttpServletRequest request,
                                     @PathVariable("contestId") Integer contestId,Model model){
      Account currentAccount = (Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
      //TODO::处理
      model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
      if(currentAccount == null || currentAccount.getLevel()<1){

          return "/error/404";
      }else {
          Map<String,Object> data = new HashMap<>();
          List<Question> questions = questionService.getQuestionsByContestId(contestId);
          Contest contest = contestService.getContestById(contestId);
          data.put("questionsSize",questions.size());
          data.put("questions",questions);
          data.put("contest",contest);
          model.addAttribute(LjtConst.DATA,data);


      }
        return "/manage/manage-editContestProblem";
    }
    /**
     * 题目管理
     * */
    @RequestMapping(value = "/question/list",method = RequestMethod.GET)
    public String questionList(HttpServletRequest request,
                               @RequestParam(value = "page",defaultValue = "1")int page,
                               @RequestParam(value = "content",defaultValue = "")String content,
                               Model model){
        Account currentAccount = (Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        //TODO::处理
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        if(currentAccount == null || currentAccount.getLevel()<1){
            System.out.println(currentAccount);
            return "/error/404";
        }else {
            Map<String,Object> data =questionService.getQuestionsByContent(page,
                    LjtConst.questionPageSize,content);
            List<Question> questions = (List<Question>) data.get("questions");
            List<Subject> subjects = subjectService.getSubjects();
            Map<Integer, String> subjectId2name = subjects.stream().collect(Collectors.toMap(Subject::getId, Subject::getName));
            for (Question question:questions){
                question.setSubjectName(subjectId2name.getOrDefault(question.getSubjectId(),"未知科目"));
            }
            data.put("subjects",subjects);
            data.put("content",content);
            model.addAttribute("data",data);
            return "/manage/manage-questionBoard";
        }

    }

    /**
     * 成绩管理-考试列表
     * */
    @RequestMapping(value = "/result/contest/list",method = RequestMethod.GET)
    public String resultContestList(HttpServletRequest request,
                                    @RequestParam(value = "page",defaultValue = "1") int page,
                                    Model model){
        Account currentAccount = (Account) request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        //TODO::处理
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        if(currentAccount == null||currentAccount.getLevel()<1){
            return "/error/404";
        }else {
            Map<String,Object> data =contestService.getContests(page,LjtConst.contestPageSize);
            List<Subject> subjects =subjectService.getSubjects();
            data.put("subjects",subjects);
            model.addAttribute(LjtConst.DATA,data);
            return "/manage/manage-resultContestBoard";
        }
    }

    /**
     * 成绩管理-考试列表-学生成绩列表
     * */
    @RequestMapping(value = "/result/contest/{contestId}/list",method =RequestMethod.GET )
    public String resultStudentList(HttpServletRequest request,
                                    @PathVariable("contestId") int contestId,
                                    @RequestParam(value = "page",defaultValue = "1") int page,
                                    Model model){
        Account currentAccount = (Account) request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        //TODO::处理
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        if(currentAccount == null || currentAccount.getLevel()<1){
            return  "/error/404";
        }else{
            Map<String,Object> data = new HashMap<>();
            List<Grade> grades = gradeService.getGradesByContestId(contestId);
            Contest contest = contestService.getContestById(contestId);
            List<Question> questions = questionService.getQuestionsByContestId(contestId);
            List<Integer> studentIds = grades.stream().map(Grade::getStudentId).collect(Collectors.toList());
            List<Account> students = accountService.getAccountsByStudentIds(studentIds);
            Map<Integer, Account> id2student = students.stream()
                    .collect(Collectors.toMap(Account::getId, account -> account));
            for (Grade grade :grades){
                Account student = id2student.get(grade.getStudentId());
                grade.setStudent(student);
            }
            data.put("grades", grades);
            data.put("contest", contest);
            data.put("questions", questions);
            model.addAttribute(LjtConst.DATA, data);
            return "/manage/manage-resultStudentBoard";


        }

    }

    /**
     * 课程管理
     * */
    @RequestMapping(value = "/subject/list",method = RequestMethod.GET)
    public String subjectList(HttpServletRequest request,
                              @RequestParam(value = "page",defaultValue = "1")int page,
                              Model model){
        Account currentAccount = (Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        //TODO::处理
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        if(currentAccount == null || currentAccount.getLevel()<1){
            return "/error/404";
        }else{
            Map<String, Object> data = subjectService.getSubjects(page, LjtConst.subjectPageSize);
            model.addAttribute(LjtConst.DATA,data);

            return "/manage/manage-subjectBoard";
        }
    }




    /**
     * 用户管理
     * */
    @RequestMapping(value = "/account/list",method = RequestMethod.GET)
    public String accountList(HttpServletRequest request,
                              @RequestParam(value = "page",defaultValue = "1")int page,
                              Model model){
        Account currentAccount=(Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        //TODO::处理
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        if(currentAccount == null || currentAccount.getLevel()<1){
            return "/error/404";
        }else {
            Map<String,Object> data = accountService.getAccounts(page,LjtConst.accountPageSize);
            model.addAttribute(LjtConst.DATA,data);
            return "/manage/manage-accountList";
        }
    }

    /**
     * 帖子管理
     * */
    @RequestMapping(value = "/post/list",method = RequestMethod.GET)
    public String postList(HttpServletRequest request,
                           @RequestParam(value = "page",defaultValue = "1")int page,
                           Model model){
        Account currentAccount=(Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        //TODO::处理
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        if(currentAccount==null||currentAccount.getLevel()<1){
            return "/error/404";
        }else{
            Map<String, Object> data = postService.getPosts(page, LjtConst.postPageSize);
            List<Post> posts = (List<Post>) data.get("posts");
            Set<Integer> authorIds = posts.stream().map(Post::getAuthorId).collect(Collectors.toCollection(HashSet::new));
            List<Account> authors = accountService.getAccountsByIds(authorIds);
            Map<Integer, Account> id2author = authors.stream().collect(Collectors.toMap(Account::getId, account -> account));

            for(Post post: posts){
                post.setAuthor(id2author.get(post.getAuthorId()));
            }
            model.addAttribute(LjtConst.DATA,data);
            return "/manage/manage-postBoard";
        }

    }

    /**
     * 评论管理
     * */
    @RequestMapping(value="/comment/list", method= RequestMethod.GET)
    public String commentList(HttpServletRequest request,
                              @RequestParam(value = "page",defaultValue = "1")int page,
                              Model model){
        Account currentAccount=(Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        //TODO::处理
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        if(currentAccount == null || currentAccount.getLevel()<1){
            return "/error/404";
        }else{
            Map<String, Object> data = commentService.getComments(page, LjtConst.commentPageSize);
            List<Comment> comments = (List<Comment>)data.get("comments");
            Set<Integer> userIds = comments.stream().map(Comment::getUserId).collect(Collectors.toCollection(HashSet::new));
            List<Account> users = accountService.getAccountsByIds(userIds);
            Map<Integer, Account> iduser = users.stream().collect(Collectors.toMap(Account::getId, account -> account));
            for (Comment comment : comments){
                comment.setUser(iduser.get(comment.getUserId()));
            }
            model.addAttribute(LjtConst.DATA,data);

            return "/manage/manage-commentBoard";

        }
    }

}
