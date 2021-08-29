package com.atguigu;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.List;

public class Seckill_redis {
    public static void main(String[] args) {
        JedisPool jedisPool = JedisPoolUtill.getJedisPoolInstance();
        System.out.println(jedisPool);
        Jedis jedis = jedisPool.getResource();
        System.out.println(jedis.ping());
    }
    public static Jedis getJeids() {
        Jedis jedis = new Jedis("81.68.133.61",6379);
        jedis.auth("yq194358");
        System.out.println(jedis.ping());
        return jedis;
    }

    //真正的秒杀函数
    public static boolean doSecKill(String userid, String proid) {
        // 1、uid 和 prodid 非空的判断
        if (userid==null || proid==null){
            return false;
        }

        // 2、连接redis
//        Jedis jedis = new Jedis("81.68.133.61",6379);
//        jedis.auth("yq194358");
//        System.out.println(jedis.ping());
        //通过连接池得到jedis对象
        JedisPool jedisPool = JedisPoolUtill.getJedisPoolInstance();
        Jedis jedis = jedisPool.getResource();
        //  3、拼接
        // 3.1 库存的key
        // 3.2 成功名单的key
        String KcKey= "sk:"+proid+":qt";
        String userKey="sk:"+proid+":user";

        //添加乐观锁的秒杀过程
        jedis.watch(KcKey);

        //4. 获取库存，如果库存本身为 null，那么秒杀还没有开始
        String kc = jedis.get(KcKey);
        if(kc==null){
            System.out.println("秒杀没有开始");
            jedis.close();
            return  false;
        }
        //5. 判断用户是否重复秒杀
        //从set中能够去除userid则表示已经秒杀了，不能在秒杀了
        if(jedis.sismember(userKey,userid)){
            System.out.println("已经秒杀成功了，不能重复秒杀");
            jedis.close();
            return false;
        }

        //6. 判断如果商品数量：库存量小于1：秒杀结束
        Integer intkc = Integer.parseInt(kc);
        if(intkc<=0){
            System.out.println("秒杀已经结束，库存已经清零！！");
            jedis.close();
            return  false;
        }

        //7. 秒杀的过程
        // 7.1 把秒杀成功的用户添加到成功表中
        // 7.2 库存减一
//        jedis.decr(KcKey);
//        jedis.sadd(userKey,userid);


        //开启事务
        Transaction mutil= jedis.multi();
        mutil.decr(KcKey);
        mutil.sadd(userKey,userid);
        List<Object> res = mutil.exec();//执行事务，结果以list返回
        if(res==null || res.size()==0){
            System.out.println("秒杀失败了");
            jedis.close();
            return false;
        }

        System.out.println("秒杀成功了。。。");
        jedis.close();

        return  true;

    }
}
