package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.entity.Menu;

import java.util.List;

/**
 * 针对表【tb_menu】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2022-12-28
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据用户信息 ID 查询菜单
     *
     * @param userInfoId 用户信息 ID
     * @return 菜单列表
     */
    List<Menu> listMenusByUserInfoId(@Param("userInfoId") Integer userInfoId);

}




