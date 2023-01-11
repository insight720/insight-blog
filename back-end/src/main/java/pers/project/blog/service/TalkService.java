package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.AdminTalkDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.TalkDTO;
import pers.project.blog.entity.TalkEntity;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.TalkVO;

import java.util.List;

/**
 * 针对表【tb_talk】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-01-11
 */
public interface TalkService extends IService<TalkEntity> {

    /**
     * 查看后台的分页说说列表
     *
     * @param conditionVO 条件
     * @return {@code  PageDTO<AdminTalkDTO>} 分页说说列表
     */
    PageDTO<AdminTalkDTO> listAdminTalks(ConditionVO conditionVO);

    /**
     * 保存或修改说说
     *
     * @param talkVO 说说信息
     */
    void saveOrUpdateTalk(TalkVO talkVO);

    /**
     * 根据 ID 查看后台说说
     *
     * @param talkId 说说 ID
     * @return {@code  AdminTalkDTO} 后台说说信息
     */
    AdminTalkDTO getAdminTalk(Integer talkId);

    /**
     * 获取首页说说列表
     *
     * @return {@code  List<String>} 说说 URL 列表
     */
    List<String> listHomePageTalks();

    /**
     * 获取分页的说说列表
     *
     * @return {@code  PageDTO<TalkDTO>} 分页的说说列表
     */
    PageDTO<TalkDTO> listTalks();

    /**
     * 根据 ID 查看说说
     *
     * @param talkId 说说 ID
     * @return {@code  TalkDTO} 说说信息
     */
    TalkDTO getTalk(Integer talkId);

    /**
     * 点赞说说
     *
     * @param talkId 说说 ID
     */
    void saveTalkLike(Integer talkId);

}
