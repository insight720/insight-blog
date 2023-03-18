package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.talk.AdminTalkDTO;
import pers.project.blog.dto.talk.TalkDTO;
import pers.project.blog.entity.Talk;

import java.util.List;

/**
 * 针对表【tb_talk】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @version 2023-01-11
 */
@Mapper
public interface TalkMapper extends BaseMapper<Talk> {

    /**
     * 按说说状态查询后台的分页说说数据
     */
    List<AdminTalkDTO> listAdminTalks(@Param("offset") long offset,
                                      @Param("size") long size,
                                      @Param("status") Integer status);

    /**
     * 根据 ID 获取后台说说数据
     */
    AdminTalkDTO getAdminTalk(@Param("talkId") Integer talkId);

    /**
     * 查询分页的公开说说数据
     */
    List<TalkDTO> listTalks(@Param("offset") long offset, @Param("size") long size);

    /**
     * 根据 ID 查询公开的前台说说数据
     */
    TalkDTO getTalk(@Param("talkId") Integer talkId);

}




