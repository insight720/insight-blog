package pers.project.blog.strategy.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.exception.FileUploadException;
import pers.project.blog.strategy.ArticleImportStrategy;

import java.util.Map;
import java.util.Optional;

/**
 * 文章上传上下文
 *
 * @author Luo Fei
 * @date 2023/1/8
 */
@Component
public class ArticleImportContext {

    private static Map<String, ArticleImportStrategy> importStrategyMap;

    private ArticleImportContext() {
    }

    @Autowired
    public void setImportStrategyMap(Map<String, ArticleImportStrategy> importStrategyMap) {
        ArticleImportContext.importStrategyMap = importStrategyMap;
    }

    /**
     * 执行文章导入策略
     *
     * @param multipartFile 多部分请求中收到的上传文件
     * @param strategyName  导入策略实现类 Bean 名
     *                      <P>（null 则执行普通文章导入策略）
     */
    public static void executeStrategy(MultipartFile multipartFile, String strategyName) {
        strategyName = Optional.ofNullable(strategyName).orElse("normal");
        Optional.of(strategyName).filter(importStrategyMap::containsKey)
                .orElseThrow(() -> new FileUploadException("策略名不存在"));
        importStrategyMap.get(strategyName).importArticle(multipartFile);
    }

}


