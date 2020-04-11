本系列文章都是基于SpringBoot2.2.5.RELEASE

# 依赖

```xml
<dependency>
     <groupId>com.alibaba</groupId>
     <artifactId>druid-spring-boot-starter</artifactId>
     <version>1.1.17</version>
</dependency>
```

# 配置文件

官方文档：

https://github.com/alibaba/druid/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98

https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter

```properties
# 数据库属性配置
spring.datasource.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# 连接池配置
# 初始化连接大小
spring.datasource.druid.initial-size=5
# 最小空闲连接数
spring.datasource.druid.min-idle=5
# 最大连接数
spring.datasource.druid.max-active=20

# 监控配置
# StatViewServlet配置，说明请参考Druid Wiki，配置_StatViewServlet配置
#是否启用StatViewServlet（监控页面）默认值为false
# （考虑到安全问题默认并未启动，如需启用建议设置密码或白名单以保障安全）
spring.datasource.druid.stat-view-servlet.enabled=true
# 登录监控页面用户名
spring.datasource.druid.stat-view-servlet.login-username=123456
# 登录监控页面密码
spring.datasource.druid.stat-view-servlet.login-password=123456

# WebStatFilter配置，说明请参考Druid Wiki，配置_配置WebStatFilter
# WebStatFilter用于采集web-jdbc关联监控的数据。
#是否启用StatFilter默认值false
spring.datasource.druid.web-stat-filter.enabled=true
# 排除一些不必要的url
spring.datasource.druid.web-stat-filter.exclusions=*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*
# 日志记录JDBC执行的SQL
spring.datasource.druid.filters=stat
```

# 启动项目

在浏览器输入：http://localhost:8080/druid/login.html

# druid自动配置原理

我们的配置文件并没有设置` spring.datasource.type`来指定`DruidDataSource`，但是`DruidDataSource`却被注入到容器中了。查看`DruidDataSourceAutoConfigure`可以发现，`DruidDataSourceAutoConfigure`比`DataSourceAutoConfiguration`先执行并往容器中注入`DataSource`组件。

```java
@Configuration
// 类路径上需要存在 DruidDataSource.class
@ConditionalOnClass({DruidDataSource.class})
// 这个配置类在 DataSourceAutoConfiguration.class 之前执行
@AutoConfigureBefore({DataSourceAutoConfiguration.class})
@EnableConfigurationProperties({DruidStatProperties.class, DataSourceProperties.class})
@Import({
    DruidSpringAopConfiguration.class, 
    DruidStatViewServletConfiguration.class, 
    DruidWebStatFilterConfiguration.class, 
    DruidFilterConfiguration.class})
public class DruidDataSourceAutoConfigure {
    private static final Logger LOGGER = LoggerFactory.getLogger(DruidDataSourceAutoConfigure.class);

    public DruidDataSourceAutoConfigure() {
    }
    // 注入 DruidDataSource 组件
    @Bean(
        initMethod = "init"
    )
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        LOGGER.info("Init DruidDataSource");
        return new DruidDataSourceWrapper();
    }
}
```

