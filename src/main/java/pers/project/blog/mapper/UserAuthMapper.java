package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.userauth.UserDTO;
import pers.project.blog.entity.UserAuth;
import pers.project.blog.vo.userauth.UserSearchVO;

import java.util.List;

/**
 * 针对表【tb_user_auth】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2022-12-23
 */
@Mapper
public interface UserAuthMapper extends BaseMapper<UserAuth> {

    /**
     * 按条件查询用户数量
     */
    Long countUsers(@Param("userSearchVO") UserSearchVO userSearchVO);

    /**
     * 按条件查询分页的用户列表数据
     */
    List<UserDTO> listUsers(@Param("offset") long offset,
                            @Param("size") long size,
                            @Param("userSearchVO") UserSearchVO userSearchVO);

}




