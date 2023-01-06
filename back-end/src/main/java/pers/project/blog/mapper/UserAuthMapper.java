package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.AdminUserDTO;
import pers.project.blog.entity.UserAuthEntity;
import pers.project.blog.vo.ConditionVO;

import java.util.List;

/**
 * 针对表【tb_user_auth】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2022-12-23
 */
@Mapper
public interface UserAuthMapper extends BaseMapper<UserAuthEntity> {

    /**
     * 按条件获取当前页后台用户列表
     *
     * @param offset      当前分页偏移量
     * @param size        分页大小
     * @param conditionVO 条件
     * @return 符合条件的当前页后台用户列表
     */
    List<AdminUserDTO> listBackgroundUserDTOs(@Param("offset") Long offset,
                                              @Param("size") Long size,
                                              @Param("conditionVO") ConditionVO conditionVO);

    /**
     * 按条件查询后台用户数量
     *
     * @param conditionVO 条件
     * @return 符合条件的后台用户数量
     */
    Integer countBackgroundUsers(@Param("conditionVO") ConditionVO conditionVO);

}




