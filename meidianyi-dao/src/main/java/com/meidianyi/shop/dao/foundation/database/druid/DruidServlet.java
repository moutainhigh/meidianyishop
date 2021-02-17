package com.meidianyi.shop.dao.foundation.database.druid;

import com.alibaba.druid.support.http.StatViewServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * @author 孔德成
 * @date 2020/6/10 14:13
 */
@WebServlet(urlPatterns="/druid/*",
    initParams={
        @WebInitParam(name="allow",value=""),// IP白名单(没有配置或者为空，则允许所有访问)
        @WebInitParam(name="deny",value=""),// IP黑名单 (deny优先于allow)
        @WebInitParam(name="loginUsername",value="admin"),// 登录druid管理页面用户名
        @WebInitParam(name="loginPassword",value="admin")// 登录druid管理页面密码
    })
public class DruidServlet extends StatViewServlet {
}
