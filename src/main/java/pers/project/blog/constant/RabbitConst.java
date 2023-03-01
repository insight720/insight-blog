package pers.project.blog.constant;

/**
 * RabbitMQ 常量
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
public abstract class RabbitConst {

    /**
     * Maxwell 交换机
     */
    public static final String MAXWELL_EXCHANGE = "maxwell_exchange";

    /**
     * Maxwell 队列
     */
    public static final String MAXWELL_QUEUE = "maxwell_queue";

    /**
     * Email 交换机
     */
    public static final String EMAIL_EXCHANGE = "email_exchange";

    /**
     * Email 队列
     */
    public static final String EMAIL_QUEUE = "email_queue";

    /**
     * 路由键
     */
    public static final String ROUTING_KEY = "*";

}
