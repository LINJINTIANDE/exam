package com.ljt.exam.controller;

import com.ljt.exam.common.LjtConst;
import com.ljt.exam.dto.AjaxResult;
import com.ljt.exam.model.*;
import com.ljt.exam.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/")
public class DefaultController {
    private static Log log = LogFactory.getLog(DefaultController.class);


    @Autowired
    ContestService contestService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    QuestionService questionService;

    @Autowired
    PostService postService;

    @Autowired
    AccountService accountService;

    @Autowired
    ReplyService replyService;

    @Autowired
    CommentService commentService;
    /**
     * 首页
     * */
    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String home(HttpServletRequest request, Model model){
        Account currentAccount =(Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        model.addAttribute(LjtConst.CURRET_ACCOUNT, currentAccount);
        return "/home";
    }

    /**
     * 在线考试列表页
     * */
    @RequestMapping(value="/contest/index", method= RequestMethod.GET)
    public String contestIndex (HttpServletRequest request, @RequestParam(value = "page",defaultValue = "1")int page,
                                Model model){

        contestService.updateStateToStart();
        contestService.updateStateToend();
        Account currentAccount = (Account) request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        Map<String,Object> data =contestService.getContests(page,LjtConst.contestPageSize);
        model.addAttribute(LjtConst.DATA,data);
        return "/contest/index";
    }


    /**
     * 在线考试页
     * */
    @RequestMapping(value = "/contest/{contestId}",method = RequestMethod.GET)
    public String contestDetail (HttpServletRequest request,
                                 @PathVariable("contestId") int contestId,
                                 Model model){
        Account currentAccount = (Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        Contest contest = contestService.getContestById(contestId);
        if(currentAccount == null || contest.getStartTime().getTime()>System.currentTimeMillis()
                ||contest.getEndTime().getTime()<System.currentTimeMillis()){
            return "rediret:/contest/index";
        }
        List<Question> questions = questionService.getQuestionsByContestId(contestId);
        for (Question question :questions){
            question.setAnswer("");
        }
        Map<String,Object> data = new HashMap<>();
        data.put("contest",contest);
        data.put("questions",questions);
        model.addAttribute(LjtConst.DATA,data);
        return "/contest/detail";
    }

    /**
     *题库中心
     * */
    @RequestMapping(value = "/problemset/list",method = RequestMethod.GET)
    public String problemSet(HttpServletRequest request,@RequestParam(value = "page",defaultValue = "1")int page,Model model){
        Account currentAccount=(Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        Map<String, Object> data = subjectService.getSubjects(page, LjtConst.subjectPageSize);

        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        model.addAttribute(LjtConst.DATA,data);
        return "/problem/problemset";
    }

    /**
     * 题目列表项
     * */
    @RequestMapping(value = "/problemset/{problemsetId}/problems",method = RequestMethod.GET)
    public String problemList(HttpServletRequest request,
                              @PathVariable("problemsetId") Integer problemsetId,
                              @RequestParam(value = "page" ,defaultValue = "1") int page,
                              @RequestParam(value = "content",defaultValue = "")String content,
                              @RequestParam(value = "difficulty",defaultValue = "0")int difficulty,
                              Model model){
        Account currentAccout = (Account) request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        Map<String,Object> data = questionService.getQuestionsByProblemsetIdAndContentAndDiffculty(page,LjtConst.questionPageSize,
                problemsetId,content,difficulty);
        Subject subject = subjectService.getSubjectById(problemsetId);
        data.put("subject",subject);
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccout);
        model.addAttribute(LjtConst.DATA,data);
        return  "/problem/problemlist";
    }


    /**
     * 讨论区首页
     * */
    @RequestMapping(value = "/discuss",method = RequestMethod.GET)
    public String discuss(HttpServletRequest request, @RequestParam(value = "page",defaultValue = "1")int page, Model model){
        Account currentAccount = (Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);

        Map<String, Object> data = postService.getPosts(page, LjtConst.postPageSize);
        List<Post> posts = (List<Post>)data.get("posts");
        Set<Integer> authorIds = posts.stream().map(Post::getAuthorId).collect(Collectors.toCollection(HashSet::new));
        List<Account> authors = accountService.getAccountsByIds(authorIds);
        Map<Integer, Account> id2author = authors.stream().
                collect(Collectors.toMap(Account::getId, account -> account));
        for (Post post : posts){
            post.setAuthor(id2author.get(post.getAuthorId()));
        }
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        model.addAttribute(LjtConst.DATA,data);
        return "/discuss/discuss";
    }

    /**
     * 帖子详情页
     * */
    @RequestMapping(value="/discuss/{postId}", method= RequestMethod.GET)
    public String discussDetail(HttpServletRequest request, @PathVariable("postId") Integer postId, Model model) {
        Account currentAccount = (Account) request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);

        Map<String, Object> data = new HashMap<>();
        Post post = postService.getPostById(postId);
        Account author = accountService.getAccountById(post.getAuthorId());
        post.setAuthor(author);
        data.put("post", post);

        List<Comment> comments = commentService.getCommentsByPostId(postId);
        List<Reply> replies = replyService.getReliesByPostId(postId);
        Set<Integer> userIds = new HashSet<>();
        for (Comment comment : comments) {
            comment.setReplies(new ArrayList<>());
            userIds.add(comment.getUserId());
        }
        for (Reply reply : replies) {
            userIds.add(reply.getUserId());
            userIds.add(reply.getAtuserId());
        }
        List<Account> users = accountService.getAccountsByIds(userIds);
        Map<Integer, Account> id2user = users.stream().
                collect(Collectors.toMap(Account::getId, account -> account));
        for (Comment comment : comments) {
            comment.setUser(id2user.get(comment.getUserId()));
        }
        for (Reply reply : replies) {
            reply.setUser(id2user.get(reply.getUserId()));
            if (reply.getAtuserId() != 0) {
                reply.setAtuser(id2user.get(reply.getAtuserId()));
            }
        }
        Map<Integer, Comment> id2comment = comments.stream().
                collect(Collectors.toMap(Comment::getId, comment -> comment));
        for (Reply reply : replies) {
            Comment comment = id2comment.get(reply.getCommentId());
            comment.getReplies().add(reply);
        }
        data.put("comments", comments);
        if (currentAccount != null){
            data.put("userId", currentAccount.getId());
        } else {
            data.put("userId", 0);
        }
        model.addAttribute(LjtConst.CURRET_ACCOUNT, currentAccount);
        model.addAttribute(LjtConst.DATA, data);
        return "/discuss/discussDetail";
    }

    /**
     * 发布帖子页
     * */
    @RequestMapping(value = "discuss/post",method = RequestMethod.GET)
    public String postDiscuss(HttpServletRequest request,Model model){
        Account currentAccount = (Account) request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        Map<String,Object> data = new HashMap<>();
        data.put("authorId",currentAccount.getId());
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        model.addAttribute(LjtConst.DATA,data);
        return "/discuss/postDiscuss";
    }

    /**
     * 获取服务器端时间，防止用户篡改客户端时间提前参与考试
     * */
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult time(){
        LocalDateTime localDateTime =LocalDateTime.now();
        return new AjaxResult().setData(localDateTime);
    }

    @RequestMapping(value = "/uid", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResult uid(HttpSession session) {
        UUID uid = (UUID) session.getAttribute("uid");
        if (uid == null) {
            uid = UUID.randomUUID();
        }
        session.setAttribute("uid", uid);
        return new AjaxResult().setData(session.getId());
    }
}
