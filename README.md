# Angel
一款适合快速开发的垃圾项目
项目使用框架:Spring5+myBatis
项目结构
```
├─src
│  ├─main
│  │  ├─java
│  │  │  └─com
│  │  │      └─mood
│  │  │          ├─angelenum 项目使用枚举类
│  │  │          ├─annotation 项目注解类
│  │  │          ├─applicationcode 系统错误码
│  │  │          ├─aspect 切面{数据源切面,权限切面}
│  │  │          ├─base  项目运行必须的基类，使用者可以自行继承
│  │  │          ├─constact 项目运行的常量
│  │  │          ├─example 示例demo
│  │  │          │  ├─dao
│  │  │          │  ├─model
│  │  │          │  └─service
│  │  │          │      └─impl
│  │  │          ├─exception 项目自定义异常
│  │  │          ├─feignclient 服务调用目前主要操作Elasticsearch
│  │  │          ├─messagebus 消息总线 使用中间件Kafka
│  │  │          └─utils 工具类
│  │  └─resources
│  │      └─mapper mybatis配置文件
│  └─test
│      └─java
│          └─com
│              ├─mood 单元测试
```
适用于快速开发，让你更加关注业务代码的处理，开箱即用，无需考虑各组件的依赖管理，让你免除配置各种中间件，数据源的配置，应为在上次需求开发中有特别多的导出excel，特增加了导出工具类，在POJO类增加注解之后调用`ExportUtil#exportExcel`,即可生成，彻底纵享丝滑，让你无视poi的痛点。
增加Spring5的Webflux,让你成为代码的弄潮儿.增加`SwaggerConfig`，安排你的接口明明白白的。基于本版本持续改进中。最近研究Kafka，在messagebus包中增加了一篇博文，持续更新中。。。

HelloWorld