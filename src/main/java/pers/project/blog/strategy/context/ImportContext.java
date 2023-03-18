package pers.project.blog.strategy.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.strategy.ImportStrategy;

import java.util.Map;
import java.util.Optional;

import static pers.project.blog.constant.ImportConst.NORMAL;

/**
 * 文章上传上下文
 *
 * @author Luo Fei
 * @version 2023/1/8
 */
@Component
public final class ImportContext {

    private static Map<String, ImportStrategy> importStrategyMap;

    @Autowired
    public void setImportStrategyMap(Map<String, ImportStrategy> importStrategyMap) {
        ImportContext.importStrategyMap = importStrategyMap;
    }

    private ImportContext() {
    }

    /**
     * 执行文章导入策略
     *
     * @param multipartFile 多部分请求中收到的上传文件
     * @param strategyName  导入策略实现类 Bean 名
     *                      <P>（null 则执行普通文章导入策略）
     */
    public static void executeStrategy(MultipartFile multipartFile, String strategyName) {
        strategyName = Optional.ofNullable(strategyName).orElse(NORMAL);
        Optional.of(strategyName).filter(importStrategyMap::containsKey)
                .orElseThrow(() -> new RuntimeException("文章导入策略名不存在"));
        importStrategyMap.get(strategyName).importArticle(multipartFile);
    }

}


