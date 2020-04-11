本系列文章都是基于SpringBoot2.2.5.RELEASE

# 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
</dependency>
```

# 配置文件

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

# 自动配置

SpringBoot的自动配置使得我们只需要设置少量的配置文件就可以直接使用。这里简单的分析一下SpringBoot是如何自动配置数据源DataSource、事物管理器、JdbcTemplate。

## 数据源的自动配置

SpringBoot提供了三种数据源：`HikariDataSource`、`org.apache.tomcat.jdbc.pool.DataSource`、`org.apache.commons.dbcp2.BasicDataSource`。默认使用的是`HikariDataSource`，可以使用`spring.datasource.type`来指定数据源。

查看数据源的自动配置类`DataSourceAutoConfiguration`

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@EnableConfigurationProperties(DataSourceProperties.class)
@Import({ DataSourcePoolMetadataProvidersConfiguration.class, DataSourceInitializationConfiguration.class })
public class DataSourceAutoConfiguration {
    // ......省略
	@Configuration(proxyBeanMethods = false)
	@Conditional(PooledDataSourceCondition.class)
	@ConditionalOnMissingBean({ DataSource.class, XADataSource.class })
    // 注入配置文件
	@Import({ 
        	 // HikariDataSource
             DataSourceConfiguration.Hikari.class,
        	 // org.apache.tomcat.jdbc.pool.DataSource`
             DataSourceConfiguration.Tomcat.class,
        	 // org.apache.commons.dbcp2.BasicDataSource
			 DataSourceConfiguration.Dbcp2.class, 
        	 // 通过设置 spring.datasource.type 自定义数据源
             DataSourceConfiguration.Generic.class, 
			DataSourceJmxConfiguration.class })
	protected static class PooledDataSourceConfiguration {

	}
    // ......省略
}
```

### 默认数据源

由于SpringBoot默认使用的是`HikariDataSource`，所以只看SpringBoot是如何自动注入`HikariDataSource`的。

```java
// DataSourceConfiguration.Hikari.class
@Configuration(proxyBeanMethods = false)
// 类路径下存在HikariDataSource.class
@ConditionalOnClass(HikariDataSource.class)
// 容器中不存在DataSource类型的Bean
@ConditionalOnMissingBean(DataSource.class)
// 当配置文件中存在spring.datasource.type
// matchIfMissing = true代表, 即使配置文件中缺少这个属性也会加载
@ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.zaxxer.hikari.HikariDataSource",
                       matchIfMissing = true)
static class Hikari {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    HikariDataSource dataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = createDataSource(properties, HikariDataSource.class);
        if (StringUtils.hasText(properties.getName())) {
            dataSource.setPoolName(properties.getName());
        }
        return dataSource;
    }
}
```

### 自定义数据源

```java
// DataSourceConfiguration.Generic.class
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(DataSource.class)
@ConditionalOnProperty(name = "spring.datasource.type")
static class Generic {
    @Bean
    DataSource dataSource(DataSourceProperties properties) {
        // 获取 spring.datasource.type 属性的值
        // 利用反射来生成生成数据源对象并绑定相关属性
        return properties.initializeDataSourceBuilder().build();
    }

}
```

## 事物自动配置

 查看自动配置类`TransactionAutoConfiguration `。这个类注入了事物管理器和开启事物的`@EnableTransactionManagemen`注解。

```java
// TransactionAutoConfiguration.class
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(PlatformTransactionManager.class)
// 注入配置文件
@AutoConfigureAfter({ 
    JtaAutoConfiguration.class, 
    HibernateJpaAutoConfiguration.class,
	DataSourceTransactionManagerAutoConfiguration.class, // 这里主要看这个类
    Neo4jDataAutoConfiguration.class })
@EnableConfigurationProperties(TransactionProperties.class)
public class TransactionAutoConfiguration {
    // 省略......
    
    // 这里注入了@EnableTransactionManagemen
    @Configuration(proxyBeanMethods = false)
	@ConditionalOnBean(TransactionManager.class)
	@ConditionalOnMissingBean(AbstractTransactionManagementConfiguration.class)
	public static class EnableTransactionManagementConfiguration {

        // JDK动态代理配置
		@Configuration(proxyBeanMethods = false)
		@EnableTransactionManagement(proxyTargetClass = false)
		@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "false",
				matchIfMissing = false)
		public static class JdkDynamicAutoProxyConfiguration {

		}

        // Cglib代理配置
		@Configuration(proxyBeanMethods = false)
		@EnableTransactionManagement(proxyTargetClass = true)
		@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true",
				matchIfMissing = true)
		public static class CglibAutoProxyConfiguration {

		}
	}
}

// 注入事物管理器
// DataSourceTransactionManagerAutoConfiguration.class
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ JdbcTemplate.class, PlatformTransactionManager.class })
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceTransactionManagerAutoConfiguration {

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnSingleCandidate(DataSource.class)
	static class DataSourceTransactionManagerConfiguration {
        // 往容器中注入事物管理器
		@Bean
		@ConditionalOnMissingBean(PlatformTransactionManager.class)
		DataSourceTransactionManager transactionManager(DataSource dataSource,
				ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
			DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
			transactionManagerCustomizers.ifAvailable((customizers) -> customizers.customize(transactionManager));
			return transactionManager;
		}

	}
}
```

## JdbcTemplate自动配置

查看自动`JdbcTemplate`自动配置类`JdbcTemplateAutoConfiguration`

```java
// JdbcTemplateAutoConfiguration.class
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ DataSource.class, JdbcTemplate.class })
@ConditionalOnSingleCandidate(DataSource.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(JdbcProperties.class)
@Import({ JdbcTemplateConfiguration.class, NamedParameterJdbcTemplateConfiguration.class })
public class JdbcTemplateAutoConfiguration {

}

// JdbcTemplateConfiguration.class
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(JdbcOperations.class)
class JdbcTemplateConfiguration {
    // 往容器中注入了 JdbcTemplate 组件
    // 方法上的 DataSource 会容器中自动注入进来
	@Bean
	@Primary
	JdbcTemplate jdbcTemplate(DataSource dataSource, JdbcProperties properties) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		JdbcProperties.Template template = properties.getTemplate();
		jdbcTemplate.setFetchSize(template.getFetchSize());
		jdbcTemplate.setMaxRows(template.getMaxRows());
		if (template.getQueryTimeout() != null) {
			jdbcTemplate.setQueryTimeout((int) template.getQueryTimeout().getSeconds());
		}
		return jdbcTemplate;
	}
}

```

# 使用Jdbc模板

新建一个User对象

```java
public class User {
    private Integer id;
    private String username;
    private Integer age;
    // 省略get、set方法
}
```

新建UserService接口

```java
public interface UserService {
    // 获取所有用户信息
    public List<User> getUserList();
    // 通过用户id查询用户信息
    public User getUserById(Integer id);
    // 新增用户信息
    public void addUser(User user);
    // 根据用户id删除用户
    public int deleteUserById(Integer id);
}
```

实现UserService接口

```java
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // 获取所有用户信息
    @Override
    public List<User> getUserList() {
        String sql = "select id,username,age from user";
        BeanPropertyRowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        List<User> users = jdbcTemplate.query(sql, rowMapper);
        return users;
    }
    
    // 通过用户id查询用户信息
    @Override
    public User getUserById(Integer id) {
        String sql = "select id,username,age from user where id = ?";
        BeanPropertyRowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);
        User user = jdbcTemplate.queryForObject(sql, rowMapper,id);
        return user;
    }
    
    // 新增用户信息
    @Transactional
    @Override
    public void addUser(User user) {
        String insertSql = "insert into user (username,age) values (?,?)";
        String updateSql = "update user set username = ?,age = ? where id = ?";
        if (user.getId() != null && !user.getId().equals("")) {
            jdbcTemplate.update(updateSql, new Object[]{user.getUsername(), user.getAge(), user.getId()});
        } else {
            jdbcTemplate.update(insertSql, new Object[]{user.getUsername(), user.getAge()});
        }
    }
    
    // 根据用户id删除用户
    @Override
    public int deleteUserById(Integer id) {
        String sql = "delete from user where id=?";
        int delete = jdbcTemplate.update(sql, new Object[]{id});
        return delete;
    }
}
```

引入thymeleaf依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

新建HomeController, 用来渲染页面

```java
@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @GetMapping("/home")
    public String home(Model model) {
        List<User> userList = userService.getUserList();
        model.addAttribute("userList",userList);
        return "home";
    }
}
```

在resources/templates目录下新建 home.html, 内容如下 

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <title>Spring MVC案例</title>
    <!-- 新 Bootstrap4 核心 CSS 文件 -->
    <link rel="stylesheet" href="https://cdn.staticfile.org/twitter-bootstrap/4.1.0/css/bootstrap.min.css">
    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="https://cdn.staticfile.org/jquery/3.2.1/jquery.min.js"></script>
    <!-- popper.min.js 用于弹窗、提示、下拉菜单 -->
    <script src="https://cdn.staticfile.org/popper.js/1.12.5/umd/popper.min.js"></script>
    <!-- 最新的 Bootstrap4 核心 JavaScript 文件 -->
    <script src="https://cdn.staticfile.org/twitter-bootstrap/4.1.0/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
    <button type="button" class="btn btn-primary addUser">
        新增用户
    </button>
    <!-- 模态框 -->
    <div class="modal fade" id="myModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <!-- 模态框头部 -->
                <div class="modal-header">
                    <h4 class="modal-title">新增用户</h4>
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>
                <!-- 模态框主体 -->
                <div class="modal-body">
                    <form class="userForm">
                        <input type="hidden" class="form-control" id="id" placeholder="">
                        <div class="form-group">
                            <label for="username">姓名</label>
                            <input type="username" class="form-control" id="username" placeholder="">
                        </div>
                        <div class="form-group">
                            <label for="age">年龄</label>
                            <input type="age" class="form-control" id="age" placeholder="">
                        </div>
                    </form>
                </div>
                <!-- 模态框底部 -->
                <div class="modal-footer">
                    <button type="button" class="btn btn-info saveUser">保存</button>
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <table class="table table-bordered">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>姓名</th>
                    <th>年龄</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                    <tr th:each="user : ${userList}">
                        <td th:text="${user.id}"></td>
                        <td th:text="${user.username}"></td>
                        <td th:text="${user.age}"></td>
                        <td>
                            <button th:data="${user.id}" type="button" class="btn btn-danger deleteUser">删除</button>
                            <button th:data="${user.id}" type="button" class="btn btn-info updateUser">编辑</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    // 点击新增按钮
    $(".addUser").click(function () {
        $('#myModal').modal('show');
        $("#id").val("");
        $("#username").val("");
        $("#age").val("");
    });
    // 点击删除按钮
    $(".deleteUser").click(function () {
        var id = $(this).attr("data");
        $.ajax({
            url:"/api/UserController/user/" + id,
            method:'delete',
            dateType:"json",
            success:function(result){
                alert("删除成功, " + result);
                location.reload();
            },
            error: function(result) {
                alert("删除失败, " + result);
            }
        });
    });
    // 点击编辑按钮
    $(".updateUser").click(function () {
        var id = $(this).attr("data");
        $.ajax({
            url:"/api/UserController/user/" + id,
            method:'get',
            dateType:"json",
            success:function(result){
                $("#id").val(result.id);
                $("#username").val(result.username);
                $("#age").val(result.age);
                $('#myModal').modal('show');
            },
            error: function(result) {
                alert("获取用户失败, " + result);
            }
        });
    });
    // 点击保存按钮
    $(".saveUser").click(function () {
        var data = {
           id: $("#id").val(),
           username: $("#username").val(),
           age: $("#age").val()
        };
        $.ajax({
            url:"/api/UserController/user",
            method:'post',
            dataType: "json",
            contentType: "application/json;charset=UTF-8",
            data: JSON.stringify(data),
            success:function(result){
                alert("保存成功");
                location.reload();
            },
            error: function(result) {
                alert("保存失败, " + result)
            }
        });
    });
</script>
</body>
</html>
```

# 效果

效果图

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020031510062042.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpemNfbGl6Yw==,size_16,color_FFFFFF,t_70)



![在这里插入图片描述](https://img-blog.csdnimg.cn/2020031510073236.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpemNfbGl6Yw==,size_16,color_FFFFFF,t_70)