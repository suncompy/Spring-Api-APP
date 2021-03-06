package com.icy9.controller;

import com.icy9.api_entity.UserLoginOrCreate;
import com.icy9.entity.Response;
import com.icy9.entity.User;
import com.icy9.service.LoginService;
import com.icy9.token.TokenUtil;
import com.icy9.token.TokenValidate;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 授权认证API
 * Created by f00lish on 2017/7/4.
 */

@RestController
@RequestMapping(value = "/api", name = "授权认证API")
public class LoginController {

    private Response response = new Response();
    @Autowired
    private LoginService loginService;
    @Autowired
    private TokenUtil tokenUtil;

    @ApiOperation(value="登录API", notes="传用户名和密码，密码请用加密后的，返回token",response = Response.class)
    @RequestMapping(value = "login", name = "登录API", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody UserLoginOrCreate userCreate)
    {
        final User user  = loginService.login(userCreate.getUsername(), userCreate.getPassword());
        // Return the token
        if (user == null || user.getToken() == null)
        {
            response.setMsg(Response.FAIL,"验证失败，请检查账号密码",user);
            return ResponseEntity.ok(response);
        }
        response.setMsg(Response.OK,"验证成功",user);
        return ResponseEntity.ok(response);

    }

    @ApiOperation(value="异常测试", notes="用来测试异常状态下接口返回",response = Response.class)
    @RequestMapping(value = "exception_test", name = "异常测试", method = RequestMethod.POST)
    public ResponseEntity<?> exceptionTest() throws Exception
    {
        throw new Exception("出现异常啦");//强行出异常，检验全局返回

    }

    @TokenValidate
    @ApiOperation(value="刷新Token", notes="传user信息进来",response = Response.class)
    @RequestMapping(value = "refresh", method = RequestMethod.POST)
    public ResponseEntity<Response> refreshToken(@RequestBody User user)
    {
        String token = tokenUtil.refreshToken(user.getToken());
        String message = "刷新Token失败";
        if (token != null)
        {
            user.setToken(token);
            message = "刷新Token成功";
        }else {
            user = null;
        }
        response.setMsg(Response.OK, message,user);
        return new ResponseEntity<Response>(response, HttpStatus.OK);
    }

}
