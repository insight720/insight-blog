package pers.project.blog.constant;

/**
 * RabbitMQ 常量
 *
 * @author Luo Fei
 * @version 2023/1/15
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
     * 死信交换机
     */
    public static final String DEAD_LETTER_EXCHANGE = "dead_letter_exchange";

    /**
     * 死信队列
     */
    public static final String DEAD_LETTER_QUEUE = "dead_letter_queue";

    /**
     * 队列绑定死信交换机 argument
     */
    public static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";

    /**
     * 队列的死信路由键 argument
     */
    public static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    /**
     * Fanout 路由键
     */
    public static final String ROUTING_KEY = "*";

}
