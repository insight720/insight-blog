package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.entity.PageEntity;
import pers.project.blog.vo.PageVO;

import java.util.List;

/**
 * 针对表【tb_page】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-01-06
 */
public interface PageService extends IService<PageEntity> {

    /**
     * 获取页面列表
     *
     * @return {@code  List<PageVO>} 页面列表
     */
    List<PageVO> listPages();

    /**
     * 保存或更新页面
     *
     * @param pageVO 页面信息
     */
    void saveOrUpdatePage(PageVO pageVO);

    /**
     * 删除页面
     *
     * @param pageId 页面 ID
     */
    void removePage(Integer pageId);

}
