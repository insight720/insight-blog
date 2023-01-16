package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.AdminTagDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.TagDTO;
import pers.project.blog.entity.TagEntity;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.TagVO;

import java.util.List;

/**
 * 针对表【tb_tag】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2022-12-30
 */
public interface TagService extends IService<TagEntity> {

    /**
     * 搜索文章标签
     *
     * @param conditionVO 条件
     * @return 标签列表
     */
    List<TagDTO> listTagsBySearch(ConditionVO conditionVO);

    /**
     * 查询分页的后台标签
     *
     * @param conditionVO 条件
     * @return {@code  PageDTO<AdminTagDTO>} 分页标签列表
     */
    PageDTO<AdminTagDTO> listAdminTags(ConditionVO conditionVO);

    /**
     * 保存或更新标签
     *
     * @param tagVO 标签
     */
    void saveOrUpdateTag(TagVO tagVO);

    /**
     * 删除标签
     *
     * @param tagIdList 标签 ID 列表
     */
    void removeTags(List<Integer> tagIdList);

    /**
     * 查询标签列表
     *
     * @return {@code PageDTO<TagDTO>} 标签数据列表
     */
    PageDTO<TagDTO> listTags();

}
