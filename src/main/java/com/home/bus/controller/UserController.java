package com.home.bus.controller;


import com.home.bus.entity.User;
import com.home.bus.factory.LogFactory;
import com.home.bus.model.IUserRole;
import com.home.bus.service.LogService;
import com.home.bus.service.LoginService;
import com.home.bus.service.UserService;
import com.home.bus.utils.EncryptUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xu.dm
 * @Date: 2018/8/11 15:54
 * @Description:
 */
@Controller
@RequestMapping("/user")
public class UserController {
    private String algorithmName = "md5";
    private int hashIterations = 2;
    private String salt = "8d78869f470951332959580424d4bf4f";

    @Resource
    UserService userService;
    @Resource
    LoginService loginService;
    @Resource
    LogService logService;
    /**
     * 用户查询.
     * @return
     */
    @RequestMapping("/ulist")
    public String userList(){
        return "/user/userList";
    }

    @RequestMapping(value="/getUserList")
    @RequiresPermissions("user:view")//权限管理;
    @ResponseBody
    public Object getUserList(HttpServletRequest request, HttpServletResponse response)
    {
        int pageSize = 10;
        try {
            pageSize =  Integer.parseInt(request.getParameter("pageSize"));
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        int pageNumber=0 ;
        try {
            pageNumber =  Integer.parseInt(request.getParameter("pageNumber"))-1;
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        Map<String, Object> map = new HashMap<>();

        String strUserName=request.getParameter("searchText")==null ? "": request.getParameter("searchText");

        String sortName=request.getParameter("sortName")==null ? "roleId": request.getParameter("sortName");
        String sortOrder=request.getParameter("sortOrder")==null ? "asc": request.getParameter("sortOrder");

        Sort sortLocal = new Sort(sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC: Sort.Direction.DESC,sortName);
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortLocal);
        Page<User> userPage = userService.findAllByUserNameContains(strUserName,pageable);
        map.put("total",userPage.getTotalElements());
        map.put("rows",userPage.getContent());

        return map;

//        ObjectMapper mapper=new ObjectMapper();
//        String jsonString="";
//        try {
//            jsonString=mapper.writeValueAsString(map);
////            System.out.print(jsonString);
//        } catch (JsonGenerationException e) {
//            e.printStackTrace();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return jsonString;

    }

    @RequestMapping(value = "/add",method = RequestMethod.GET)
    @RequiresPermissions("user:add")//权限管理;
    public String toUserAdd(User user){
        return "/user/userAdd";
    }


    /**
     * 用户添加;
     * @return
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @RequiresPermissions("user:add")//权限管理;
    @ResponseBody
    public String save(@Valid User user, BindingResult bindingResult, String password2){
        if(bindingResult.hasErrors())
        {
            return "0";
        }

        user.setSalt(this.salt);
        if(user.getUserId()==null) {
            user.setCreateTime(LocalDateTime.now());
            String encryptPwd = EncryptUtils.encrypt(user.getPassword(),user.getCredentialsSalt(),this.algorithmName,this.hashIterations);
            user.setPassword(encryptPwd);
        }
        else {
            if(!user.getPassword().equals(password2))
            {
                String encryptPwd = EncryptUtils.encrypt(user.getPassword(),user.getCredentialsSalt(),this.algorithmName,this.hashIterations);
                user.setPassword(encryptPwd);
            }
        }
        try {
            userService.save(user);
            logService.writeLog(LogFactory.createSysLog("新增或修改用户","用户："+user.getUserName()));
            return "/user/ulist";
        }catch (Exception e)
        {
            e.printStackTrace();
            return "0";
        }
    }

    @RequestMapping("/checkUserExists")
    @ResponseBody
    public Object checkRoleExists(@RequestParam String newUserName, @RequestParam(required = false) Integer userId, @RequestParam(required = false) String oldUserName)
    {
        Map<String,Boolean> map = new HashMap<>();
        if(userId==null)
        {
            boolean result = !userService.checkUserExists(newUserName);
            map.put("valid",result);
        }
        else
        {
            boolean result = !userService.checkUserExists2(oldUserName,newUserName);
            map.put("valid",result);
        }
        return map;
    }


    @RequestMapping(value = "/edit/{id}")
    @RequiresPermissions("user:edit")
    public String edit(@PathVariable("id")Integer id, Map<String,Object> map)
    {
        User user = userService.findUserById(id);
        map.put("user",user);
        return "/user/userAdd";
    }

    /**
     * 用户删除;
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    @RequiresPermissions("user:del")//权限管理;
    public Object userDel(@RequestParam String userIdList){
        if(userIdList==null || userIdList.isEmpty())
        {
            return "0";
        }
        String[] sList = userIdList.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String s:sList )
        {
            if(s.equalsIgnoreCase("1"))
                return "0";
            idList.add(Integer.parseInt(s));

        }
        Map<String,String> map = new HashMap<>();
        try {
            userService.deleteAllUserByUserIdList(idList);
            map.put("success","true");
            map.put("url","/user/ulist");
            logService.writeLog(LogFactory.createSysLog("删除用户","用户id："+userIdList));
        }catch (Exception e)
        {
            e.printStackTrace();
            map.put("error","true");
        }

        return map;
    }

    @RequestMapping(value="/toListUserRole/{userId}")
    @RequiresPermissions("user:roleGrant")
    public String listUserRole(@PathVariable("userId")Integer userId, Map<String, Object> map)
    {
        User user = userService.findUserById(userId);
        map.put("user",user);
        return "/user/userRole";
    }


    @RequestMapping(value = "/toGetUserRole/{userId}")
    @ResponseBody
    @RequiresPermissions("user:roleGrant")
    public Object getUserRole(@PathVariable("userId")Integer userId)
    {
        if(userId==null)
            return null;

        List<IUserRole> list = userService.findAllUserRoleByUserId(userId);
        return list;

//        ObjectMapper mapper=new ObjectMapper();
//        String jsonString="";
//        try {
//            jsonString=mapper.writeValueAsString(list);
////            System.out.print(jsonString);
//        } catch (JsonGenerationException e) {
//            e.printStackTrace();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return jsonString;
    }




    @RequestMapping(value="/toGrantUserRole")
    @ResponseBody
    @RequiresPermissions("user:roleGrant")
    public Object grantUserRole(Integer userId, String roleIdList)
    {
        if(userId==1) return 0;
        Map<String,String> map = new HashMap<>();
        if(roleIdList==null || roleIdList.isEmpty())
        {
            try {
                userService.deleteAllUserRoleByUserId(userId);
                map.put("success","true");
                map.put("url","/user/ulist");
                logService.writeLog(LogFactory.createSysLog("角色清除","用户ID："+userId));
                return map;
            }catch (Exception e)
            {
                e.printStackTrace();
                map.put("sucess","false");
                map.put("url","/user/ulist");
                return map;
            }
        }
        String[] sList = roleIdList.split(",");
        List<Integer> idList = new ArrayList<>();
        for (String s:sList )
        {
            idList.add(Integer.parseInt(s));
        }

        try {
            userService.grantUserRole(userId,idList);
            map.put("sucess","true");
            map.put("url","/user/ulist");
            logService.writeLog(LogFactory.createSysLog("角色分配","用户ID："+userId+" 角色id列表："+roleIdList));

            return map;
        }catch (Exception e)
        {
            e.printStackTrace();
            map.put("sucess","false");
            map.put("url","/user/ulist");
            return map;
        }
    }

    @RequestMapping(value="/toChangePassword")
    public String toChangePassword(String password,String newPassword,String newPassword2)
    {
        return "/user/changePwd";
    }

    @RequestMapping(value="/changePassword",method = RequestMethod.POST)
    @ResponseBody
    public Object changePassword(HttpServletRequest request)
    {
        Map<String,String> map = new HashMap<>();
        String password = request.getParameter("password");
        String newPassword = request.getParameter("newPassword");
        String newPassword2 = request.getParameter("newPassword2");

        if(newPassword==null)
        {
            map.put("success","false");
            map.put("result","密码不能为空");
            return map;
        }

        if(!newPassword.equals(newPassword2))
        {
            map.put("success","false");
            map.put("result","两次密码输入不一样");
            return map;
        }

        if(newPassword.length()<6)
        {
            map.put("success","false");
            map.put("result","密码长度必须大于6位");
            return map;
        }


        String userName = loginService.getCurrentUserName();
        if(userName==null || userName.isEmpty())
        {
            map.put("success","false");
            map.put("result","用户错误");
            return map;
        }
        User user = userService.findByUserName(userName);
        if(user==null)
        {
            map.put("success","false");
            map.put("result","用户错误");
            return map;
        }

        String encryptPwd = EncryptUtils.encrypt(password,user.getCredentialsSalt(),this.algorithmName,this.hashIterations);
        if(!encryptPwd.equals(user.getPassword()))
        {
            map.put("success","false");
            map.put("result","当前用户密码不正确");
            return map;
        }

        String encryptNewPwd =EncryptUtils.encrypt(newPassword,user.getCredentialsSalt(),this.algorithmName,this.hashIterations);
        user.setPassword(encryptNewPwd);
        userService.save(user);
        map.put("success","true");
        map.put("result","密码修改成功，请重新登录");
        map.put("url","/logout");
        logService.writeLog(LogFactory.createSysLog("修改密码","成功"));
        return map;
    }

    @RequestMapping(value="/toCheckPwd")
    @ResponseBody
    public Object checkCurrentPwd(@RequestParam String password)
    {
        Map<String,Boolean> map = new HashMap<>();
        if(password==null)
        {
            map.put("valid",false);
            return map;
        }

        String userName = loginService.getCurrentUserName();
        if(userName==null || userName.isEmpty())
        {
            map.put("valid",false);
            return map;
        }
        User user = userService.findByUserName(userName);
        if(user==null)
        {
            map.put("valid",false);
            return map;
        }

        String encryptPwd = EncryptUtils.encrypt(password,user.getCredentialsSalt(),this.algorithmName,this.hashIterations);
        if(!encryptPwd.equals(user.getPassword()))
        {
            map.put("valid",false);
            return map;
        }

        map.put("valid",true);
        return map;
    }


}
