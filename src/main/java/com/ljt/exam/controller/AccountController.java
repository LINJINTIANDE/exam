package com.ljt.exam.controller;

import com.ljt.exam.common.LjtConst;
import com.ljt.exam.dto.AjaxResult;
import com.ljt.exam.exception.LjtWebError;
import com.ljt.exam.model.Account;
import com.ljt.exam.model.Contest;
import com.ljt.exam.model.Grade;
import com.ljt.exam.model.Subject;
import com.ljt.exam.service.*;
import com.ljt.exam.util.MD5;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/account")
public class AccountController {

    private static Log LOG = LogFactory.getLog(AccountController.class);
    @Autowired
    AccountService accountService;

    @Autowired
    GradeService gradeService;

    @Autowired
    ContestService contestService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    PostService postService;

    /**
     * 验证登录
     * */
    @RequestMapping(value = "api/login",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult login(HttpServletRequest request, HttpServletResponse response) {
        AjaxResult ajaxResult = new AjaxResult();
        try {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Account current_account = accountService.getAccountByUsername(username);
        if (current_account != null) {
            String pwd = MD5.md5(LjtConst.MD5_SALT + password);
            if(pwd.equals(current_account.getPassword())){
                //设置单位为秒，设置为-1永不过期
                request.getSession().setAttribute(LjtConst.CURRET_ACCOUNT,current_account);
                ajaxResult.setData(current_account);
            }else {
                return AjaxResult.fixeError(LjtWebError.WRONG_PASSWORD);
            }


        }else {
            return AjaxResult.fixeError(LjtWebError.WRONG_USERNAME);
        } } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return AjaxResult.fixeError(LjtWebError.COMMON);
        }
        return ajaxResult;
    }

    /**
     * 个人页面
     * */
    @RequestMapping(value = "/profile",method = RequestMethod.GET)
    public String profile(HttpServletRequest request, Model model){
        Account currentAccount = (Account) request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        //TODO:拦截器过滤处理
        if(currentAccount == null){
            //用户为登录直接返回首页面
            return  "redirect:/";
        }
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        return "/user/profile";
    }

    /**
     * 更新个人信息
     * */
    @RequestMapping(value = "/api/updateAccount",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult updateAccount(HttpServletRequest request,HttpServletResponse response){
        AjaxResult ajaxResult = new AjaxResult();
        try{
            String phone = request.getParameter("phone");
            String qq = request.getParameter("qq");
            String email =request.getParameter("email");
            String description = request.getParameter("description");
            String avatarImgUrl = request.getParameter("avatarImgUrl");

            Account currentAccount=(Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
            currentAccount.setPhone(phone);
            currentAccount.setQq(qq);
            currentAccount.setEmail(email);
            currentAccount.setDescription(description);
            currentAccount.setAvatarImgUrl(avatarImgUrl);
            boolean result = accountService.updateAccount(currentAccount);
            ajaxResult.setSuccess(result);
        }catch (Exception e){
            LOG.error(e.getMessage(),e);
            return AjaxResult.fixeError(LjtWebError.COMMON);
        }
        return ajaxResult;
    }

    /**
     * 头像上传
     * */
    @RequestMapping(value = "/api/uploadAvatar",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> uploadAvatar(HttpServletRequest request, @RequestParam("file")MultipartFile file){
        AjaxResult ajaxResult = new AjaxResult();
        try{
            //原始名称
            String oldFileName=file.getOriginalFilename();//获取上传文件的原名
            //存储图片的物理路径
            String file_path=LjtConst.UPLOAD_FILE_IMAGE_PATH;
            LOG.info("file_path =" + file_path);
            //上传图片
            if(file!=null &&oldFileName!=null&&oldFileName.length()>0){
                //新的图片名称
                String newFileName = UUID.randomUUID()+oldFileName.substring(oldFileName.lastIndexOf("."));
                //新图片
                File newFile=new File(file_path+newFileName);
                //将内存中的数据写入磁盘
                file.transferTo(newFile);
                //将新图片名称返回前端
                ajaxResult.setData(newFileName);
            }else {
                return AjaxResult.fixeError(LjtWebError.UPLOAD_FILE_IMAGE_NOT_QUALFIED);}
         }catch (IOException e) {
            LOG.error(e.getMessage(),e);
           return AjaxResult.fixeError(LjtWebError.UPLOAD_FILE_IMAGE_ANALYZE_ERROR);
        }
        return  ajaxResult;
    }


    /**
     * 更改密码页面
     * */
    @RequestMapping(value = "/password",method = RequestMethod.GET)
    public String password(HttpServletRequest request,Model model){
        Account currentAccount=(Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        //TODO::拦截器过滤处理
        if(currentAccount == null){
            //用户为登录直接返回首页面
            return "redirect:/";
        }
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        return "/user/password";

    }

    /**
     * 更新密码
     * */
    @RequestMapping(value = "/api/updatePassword",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult updatePassword(HttpServletRequest request,HttpServletResponse response){
        AjaxResult ajaxResult = new AjaxResult();
        try{
            String oldPassword = request.getParameter("oldPassword");
            String newPassword = request.getParameter("newPassword");
            String confimnewPassword = request.getParameter("confirmNewPassword");
            String mdOldPassword = MD5.md5((LjtConst.MD5_SALT+oldPassword));
            String md5NewPassword =MD5.md5(LjtConst.MD5_SALT+newPassword);
            if(StringUtils.isNotEmpty(newPassword)&&StringUtils.isNotEmpty(confimnewPassword)
              &&!newPassword.equals(confimnewPassword)){
                return AjaxResult.fixeError(LjtWebError.NOT_EQUALS_CONFIRM_PASSWORD);
            }
            Account currentAccount = (Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
            if(!currentAccount.getPassword().equals(mdOldPassword)){
                return AjaxResult.fixeError(LjtWebError.WRONG_PASSWORD);
            }
            currentAccount.setPassword(md5NewPassword);
            boolean result = accountService.updateAccount(currentAccount);
            ajaxResult.setSuccess(result);
        }catch (Exception e){
            LOG.error(e.getMessage(),e);
            return AjaxResult.fixeError(LjtWebError.COMMON);
        }
        return ajaxResult;
    }

    /**
     * 考试记录页面
     */
    @RequestMapping(value="/myExam", method= RequestMethod.GET)
    public String myExam(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int page, Model model) {
        Account currentAccount = (Account) request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        //TODO::拦截器过滤处理
        if (currentAccount == null) {
            //用户未登录直接返回首页面
            return "redirect:/";
        }
        Map<String, Object> data = gradeService.getGradesByStudentId(page, LjtConst.gradePageSize, currentAccount.getId());
        List<Grade> grades = (List<Grade>) data.get("grades");
        Set<Integer> contestIds = grades.stream().map(Grade::getContestId).collect(Collectors.toCollection(HashSet::new));
        List<Contest> contests = contestService.getContestsByContestIds(contestIds);
        List<Subject> subjects = subjectService.getSubjects();
        Map<Integer, String> subjectId2name = subjects.stream().
                collect(Collectors.toMap(Subject::getId, Subject::getName));
        for (Contest contest : contests) {
            contest.setSubjectName(subjectId2name.
                    getOrDefault(contest.getSubjectId(), "未知科目"));
        }
        Map<Integer, Contest> id2contest = contests.stream().
                collect(Collectors.toMap(Contest::getId, contest -> contest));
        for (Grade grade : grades) {
            grade.setContest(id2contest.get(grade.getContestId()));
        }
        model.addAttribute(LjtConst.DATA, data);
        model.addAttribute(LjtConst.CURRET_ACCOUNT, currentAccount);
        return "/user/myExam";
    }

    /**
     * 我的发帖页面
     * */
    @RequestMapping(value = "/myDiscussPost",method = RequestMethod.GET)
    public String myDiscussPost(HttpServletRequest request,@RequestParam(value = "page",defaultValue = "1") int page,Model model){
        Account currentAccount =(Account)request.getSession().getAttribute(LjtConst.CURRET_ACCOUNT);
        //TODO::拦截器过滤处理
        if(currentAccount == null){
            //用户未登录直接返回首页面
            return  "redirect:/";
        }
        Map<String,Object> data= postService.getPostsByAuthorId(page,LjtConst.postPageSize,currentAccount.getId());
        model.addAttribute(LjtConst.DATA,data);
        model.addAttribute(LjtConst.CURRET_ACCOUNT,currentAccount);
        return  "user/myDiscussPost";
    }

    /**
     * 用户退出
     * */
    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String logout(HttpServletRequest request){
        request.getSession().setAttribute(LjtConst.CURRET_ACCOUNT,null);
        String url =request.getHeader("Referer");
        LOG.info("url="+url);
        return "redirect:"+url;

    }

    /**
     * api:添加用户
     * */
    @RequestMapping(value = "/api/addAccount",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult addAccount(@RequestBody Account account){
        Account existAccount = accountService.getAccountByUsername(account.getUsername());
        if(existAccount == null){//检测该用户是否已经注册过
            account.setPassword(MD5.md5(LjtConst.MD5_SALT+account.getPassword()));
            account.setAvatarImgUrl(LjtConst.DEFAULT_AVATAR_IMG_URL);
            account.setDescription("");
            int accountId = accountService.addAccount(account);
            return  new AjaxResult().setData(accountId);
        }
        return AjaxResult.fixeError(LjtWebError.AREADY_EXIST_USERNAME);
    }

    /**
     * API：更新用户
     * */
    @RequestMapping(value = "/api/updateManegeAccount",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult updateAccout(@RequestBody Account account){
        account.setPassword(MD5.md5(LjtConst.MD5_SALT+account.getPassword()));
        boolean result = accountService.updateAccount(account);
        return  new AjaxResult().setData(result);
    }

    /**
     * API:删除用户
     * */
    @DeleteMapping("/api/deleteAccount/{id}")
    @ResponseBody
    public AjaxResult deleteAccount(@PathVariable int id){
        boolean result = accountService.deleteAccount(id);
        return  new AjaxResult().setData(result);
    }

    /**
     * API::禁用账号
     * */
    @RequestMapping(value = "/api/disabledAccount/{id}",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult disabledAccount(@PathVariable int id){
        boolean result = accountService.disabledAccount(id);
        return  new AjaxResult().setData(result);
    }

    /**
     * API::解禁账号
     * */
    @RequestMapping(value = "/api/abledAccount/{id}",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult adbledAccount(@PathVariable   int id){
        boolean result = accountService.ableAccount(id);
        return  new AjaxResult().setData(result);
    }

}

