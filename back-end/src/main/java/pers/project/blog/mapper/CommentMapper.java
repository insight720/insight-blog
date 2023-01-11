package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.AdminCommentDTO;
import pers.project.blog.entity.CommentEntity;
import pers.project.blog.vo.ConditionVO;

import java.util.List;
import java.util.Map;

/**
 * 针对表【tb_comment】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
@Mapper
public interface CommentMapper extends BaseMapper<CommentEntity> {

    /**
     * 统计后台评论数量
     *
     * @param conditionVO 条件
     * @return 评论数量
     */
    Integer countAdminComments(@Param("conditionVO") ConditionVO conditionVO);

    /**
     * 查询分页的后台评论
     *
     * @param offset      条数偏移量
     * @param size        页面最大条数
     * @param conditionVO 条件
     * @return {@code List<AdminCommentDTO>} 后台的评论列表
     */
    List<AdminCommentDTO> listAdminCategoryDTOs(@Param("offset") long offset,
                                                @Param("size") long size,
                                                @Param("conditionVO") ConditionVO conditionVO);

    /**
     * 获取评论主题 ID 与评论数量的映射
     *
     * @param talkIdList 说说 ID 列表
     * @return {@code Map<Integer, Integer>} 评论主题 ID 与评论数量的映射
     */
    @MapKey("topic_id")
    Map<Integer, Integer> getTopicIdCommentsCountMap(@Param("talkIdList") List<Integer> talkIdList);

}




