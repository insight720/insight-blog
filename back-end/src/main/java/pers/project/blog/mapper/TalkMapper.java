package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.AdminTalkDTO;
import pers.project.blog.dto.TalkDTO;
import pers.project.blog.entity.TalkEntity;
import pers.project.blog.vo.ConditionVO;

import java.util.List;

/**
 * 针对表【tb_talk】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023-01-11
 */
@Mapper
public interface TalkMapper extends BaseMapper<TalkEntity> {

    /**
     * 查询后台的分页说说信息
     *
     * @param offset      条数偏移量
     * @param size        页面最大条数
     * @param conditionVO 条件
     * @return {@code List<AdminTalkDTO>}
     */
    List<AdminTalkDTO> listAdminTalkDTOs(@Param("offset") long offset,
                                         @Param("size") long size,
                                         @Param("conditionVO") ConditionVO conditionVO);

    /**
     * 根据 ID 获取后台的说说信息
     *
     * @param talkId 说说 ID
     * @return {@code AdminTalkDTO} 说说信息
     */
    AdminTalkDTO getAdminTalkDTO(@Param("talkId") Integer talkId);

    /**
     * 查询分页的公开说说信息
     *
     * @param offset 条数偏移量
     * @param size   页面最大条数
     * @return {@code List<TalkDTO>} 说说信息列表
     */
    List<TalkDTO> listTalkDTOs(@Param("offset") long offset, @Param("size") long size);

    /**
     * 根据 ID 查询说说信息
     *
     * @param talkId 说说 ID
     * @return {@link TalkDTO} 说说信息
     */
    TalkDTO getTalkDTO(@Param("talkId") Integer talkId);

}




