package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.tag.ManageTagDTO;
import pers.project.blog.dto.tag.TagDTO;
import pers.project.blog.entity.Tag;
import pers.project.blog.vo.tag.TagVO;

import java.util.Collection;
import java.util.List;

/**
 * 针对表【tb_tag】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2022-12-30
 */
public interface TagService extends IService<Tag> {

    /**
     * 获取文章列表标签数据
     */
    List<TagDTO> listArticleTags(String keywords);

    /**
     * 获取分页的后台标签管理数据
     */
    PageDTO<ManageTagDTO> listManageTags(String keywords);

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

    /**
     * 保存并获取新标签
     * <p>
     * <b>注意：此方法没有检查标签名是否会重复。</b>
     *
     * @param newTagNames 新标签名集合
     */
    List<Tag> saveAndGetNewTags(Collection<String> newTagNames);

    /**
     * 获取文章的所有标签名
     */
    List<String> listArticleTagNames(Integer articleId);

}
