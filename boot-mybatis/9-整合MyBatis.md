本系列文章都是基于SpringBoot2.2.5.RELEASE

# 依赖

```xml
 <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    </dependency>
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.1.2</version>
</dependency>
```

# 配置文件

```properties
# 数据库属性
spring.datasource.url=jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# MyBatis设置
# 开启自动驼峰命名规则
mybatis.configuration.map-underscore-to-camel-case=true
# 设置xml文件位置
mybatis.mapper-locations=classpath*:/mapper/**/*.xml
# 打印SQL语句
# mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
# 设置mapper的日志级别, 将SQL打印出来
logging.level.com.lizhencheng.mybatis.mapper=debug
```

# 编写代码

新建User对象

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

## 编写Mapper

### 基于注解

新建UserMapper接口

```java
@Mapper
public interface UserMapper {
    @Select("select * from user")
    public List<User> getUserList();
    @Select("select id,username,age from user where id = #{id}")
    public User getUserById(Integer id);
    @Insert("insert into user (username,age) values (#{username},#{age})")
    public void addUser(User user);
    @Update("update user set username = #{username},age = #{age} where id = #{id}")
    public void updateUser(User user);
    @Delete("delete from user where id=#{id}")
    public int deleteUserById(Integer id);
}
```

### 基于xml

UserMapper接口

```java
@Mapper
public interface UserMapper {
    public List<User> getUserList();
    
    public User getUserById(Integer id);
    
    public void addUser(User user);
    
    public void updateUser(User user);
    
    public int deleteUserById(Integer id);
}
```

在resources/mapper目录下新建UserMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.lizhencheng.mybatis.mapper.UserMapper">
    <select id="getUserList"
            resultType="com.lizhencheng.mybatis.bean.User">
        select * from user
    </select>
    <select id="getUserById" resultType="com.lizhencheng.mybatis.bean.User">
        select * from user
        where id = #{id}
    </select>
    <insert id="addUser" useGeneratedKeys="true" keyProperty="id">
        insert into user
        (id, username, age)
        values (#{id}, #{username}, #{age})
    </insert>
    <update id="updateUser">
        update user
        set username = #{username},
        age = #{age}
        where id = #{id}
    </update>
    <delete id="deleteUserById">
        delete from user
        where id = #{id}
    </delete>
</mapper>
```

## 实现类

UserServiceImpl

```java
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getUserList() {
        List<User> users = userMapper.getUserList();
        return users;
    }
    @Override
    public User getUserById(Integer id) {
        User user = userMapper.getUserById(id);
        return user;
    }
    @Transactional
    @Override
    public void addUser(User user) {
        if (user.getId() != null && !user.getId().equals("")) {
            userMapper.updateUser(user);
        } else {
            userMapper.addUser(user);
        }
    }
    @Override
    public int deleteUserById(Integer id) {
        return userMapper.deleteUserById(id);
    }
}
```

# 整合分页插件

## 用法

导入依赖

```xml
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.2.13</version>
</dependency>
```

官方文档：

https://github.com/abel533/MyBatis-Spring-Boot

https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md

例如：

```java
@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @GetMapping("/home")
    public String home(Model model,
                       @RequestParam(name = "page", defaultValue = "1")Integer page,
                       @RequestParam(name = "size", defaultValue = "5")Integer size) {
        // 设置分页
        PageHelper.startPage(page, size);
        PageInfo<User> userPageInfo = new PageInfo<>(userService.getUserList());
        model.addAttribute("userList",userPageInfo.getList());
        return "home";
    }
}
```

## 自动配置原理

查看`PageHelperAutoConfiguration`自动配置类可以发现，这个配置类就是往`SqlSessionFactory`中注入了一个`PageInterceptor`拦截器，这个拦截器来实现分页操作。

```java
@Configuration
@ConditionalOnBean({SqlSessionFactory.class})
@EnableConfigurationProperties({PageHelperProperties.class})
@AutoConfigureAfter({MybatisAutoConfiguration.class})
public class PageHelperAutoConfiguration {
    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;
    @Autowired
    private PageHelperProperties properties;

    public PageHelperAutoConfiguration() {
    }

    @Bean
    @ConfigurationProperties(
        prefix = "pagehelper"
    )
    public Properties pageHelperProperties() {
        return new Properties();
    }

    @PostConstruct
    public void addPageInterceptor() {
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.putAll(this.pageHelperProperties());
        properties.putAll(this.properties.getProperties());
        interceptor.setProperties(properties);
        Iterator var3 = this.sqlSessionFactoryList.iterator();

        while(var3.hasNext()) {
            SqlSessionFactory sqlSessionFactory = (SqlSessionFactory)var3.next();
            sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
        }

    }
}
```



