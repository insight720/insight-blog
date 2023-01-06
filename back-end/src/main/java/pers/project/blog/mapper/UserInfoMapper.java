package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.blog.entity.UserInfoEntity;

/**
 * 针对表【tb_user_info】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2022-12-25
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfoEntity> {

}




