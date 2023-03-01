# Insight Blog

![Java](https://img.shields.io/badge/Java-1.8-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2-brightgreen) ![Vue](https://img.shields.io/badge/Vue-2-green) ![License](https://img.shields.io/badge/License-Apache%202-blue)

> 注意：此项目改写自 [原项目作者](https://github.com/X1192176811) 的开源博客项目（2023 年 2 月前的版本），非完全原创。项目部署与原项目有差异，如需部署可以联系我。

基于 SpringBoot + Vue 开发的前后端分离博客项目。采用 Spring Session 整合 Spring Security 进行用户认证管理和接口级授权，并支持 QQ、微博第三方登录。使用 Elasticsearch 结合 RabbitMQ 和 Maxwell 实现快速文章检索。用 Redis 实现项目功能，并整合 Spring Cache 添加缓存。使用 WebSocket 实现多人在线聊天室，支持语音、文本和表情发送。此外，还有相册、说说和留言等功能。

## 项目展示

- 前台博客地址：https://insightblog.cn

- 后台管理地址：https://admin.insightblog.cn

  测试账号

  同时允许 10 个会话，只能查看后台管理，没有修改权限。

  用户名：test@qq.com 密码：123456

- 接口文档地址：https://insightblog.apifox.cn

  没有检查接口文档，参数信息要求可能不准确。

## 改写内容

此项目在原项目的基础上进行了大量的优化、改动或纠错，前后端均有内容改写，总体上比原项目有更高的代码质量。由于涉及内容很多，这里仅列出有记录的主要内容，博客内文章也有部分改写内容的相关记录。此外，项目代码有详细的注释内容，代码应该具有足够的可读性。

- Spring Security 使用简化的 AuthorizationManager API，并确保了授权的线程安全。
- 会话管理基于 Spring Session 和 Redis，而不是服务器内存。
- 加入 Spring Cache 缓存层，提高了程序的响应速度。
- 完善参数校验、日志记录和异常处理，新增异常信息通知博主功能。
- 优化事务、多线程和定时任务的使用。
- 新增修改角色禁用状态和修改菜单隐藏状态功能。
- 优化前端文章的显示效果，使文章字体更具可读性。
- 前台博客保存登录状态，开启新的标签页或关闭浏览器不再需要重新登陆，直至用户手动注销或 30 分钟后会话过期。后台管理没有这一改动。