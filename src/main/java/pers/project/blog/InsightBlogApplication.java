package pers.project.blog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.Charset;
import java.time.ZoneId;

/**
 * 主启动类
 *
 * @author Luo Fei
 * @version 2022-12-22
 */
@Slf4j
@SpringBootApplication
public class InsightBlogApplication {

    public static void main(String[] args) {
        log.info("系统默认时区 {}，可使用 JVM 参数 -Duser.timezone=GMT+08:00 修改",
                ZoneId.systemDefault());
        log.info("系统默认字符集 {}，可使用 JVM 参数 -Dfile.encoding=UTF-8 修改",
                Charset.defaultCharset());
        SpringApplication.run(InsightBlogApplication.class, args);
    }

}
