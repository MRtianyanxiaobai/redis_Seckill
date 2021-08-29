package com.atguigu;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

public class secKillServlet extends HttpServlet
{
    private static final  long serialVersionUID = 1L;
    public  secKillServlet(){
        super();
    }

    //post 方法发送秒杀请求
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //假设当前登录的 userid
        String userid = new Random().nextInt(50000)+"";
        //默认 1101
        String proid = req.getParameter("prodid");

        //进行秒杀
//        boolean isSuccess = Seckill_redis.doSecKill(userid,proid);
        //利用 lua 进行redis 操作
        boolean isSuccess = Seckill_redisScript.doSecKill(userid,proid);

        //写回页面
        resp.getWriter().print(isSuccess);

    }
}
