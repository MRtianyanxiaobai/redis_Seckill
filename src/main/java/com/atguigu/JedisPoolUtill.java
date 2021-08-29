package com.atguigu;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtill {
    //通过volatile声明，实现线程安全的延迟初始化。
    private  static volatile JedisPool jedisPool = null;
    private JedisPoolUtill() {
    }
    public  static JedisPool getJedisPoolInstance(){
        if (jedisPool==null){
//            System.out.printl;
            synchronized (JedisPoolUtill.class){
                if (jedisPool==null){
                    JedisPoolConfig poolConfig = new JedisPoolConfig();
                    poolConfig.setMaxTotal(200);
                    poolConfig.setMaxIdle(32);
                    poolConfig.setMaxWaitMillis(100*1000);
                    poolConfig.setBlockWhenExhausted(true);
                    poolConfig.setTestOnBorrow(true);  // ping  PONG

                    jedisPool = new JedisPool(poolConfig, "81.68.133.61",6379, 60000,"yq194358" );


                }
            }
        }
        return  jedisPool;
    }


    public static void release(Jedis jedis) {
        if (null != jedis) {
            jedis.close();
        }
    }
}
