package pers.project.blog.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import pers.project.blog.util.TimeUtils;

import java.time.LocalDateTime;

import static pers.project.blog.constant.DatabaseConst.CREATE_TIME;
import static pers.project.blog.constant.DatabaseConst.UPDATE_TIME;

/**
 * MyBatis-Plus 字段自动填充处理程序
 *
 * @author Luo Fei
 * @version 2022/12/23
 */
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, CREATE_TIME, LocalDateTime.class, TimeUtils.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, UPDATE_TIME, LocalDateTime.class, TimeUtils.now());
    }

}
