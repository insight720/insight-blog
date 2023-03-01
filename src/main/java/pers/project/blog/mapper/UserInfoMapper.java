package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.userinfo.OnlineUserDTO;
import pers.project.blog.entity.UserInfo;

import java.util.Set;

/**
 * 针对表【tb_user_info】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2022-12-25
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 获取分页的在线用户数据列表
     *
     * @param keywords    搜索关键词
     * @param usernameSet 所有在线用户用户名集合
     */
    IPage<OnlineUserDTO> listOnlineUsers(@Param("keywords") String keywords,
                                         @Param("usernameSet") Set<String> usernameSet,
                                         @Param("page") IPage<OnlineUserDTO> page);

}




