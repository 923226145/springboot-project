本系列文章都是基于SpringBoot2.2.5.RELEASE

# 依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>1.2.59</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
</dependencies>
```

# 编写代码

**GetHttpSessionConfigurator**

```java
/**
 * Created by lzc
 * 2020/4/11 16:59
 * 用来获取HttpSession
 */
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {
    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        // 设置 HttpSession ，以便后续取出
        config.getUserProperties().put(HttpSession.class.getName(), httpSession);
    }
}
```

**WebSocketConfig**

```java
@Configuration
public class WebSocketConfig {
    // 这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
```

MyWebSocket

```java
/**
 * Created by lzc
 * 2020/4/11 16:52
 */
@ServerEndpoint(value = "/websocket", configurator = GetHttpSessionConfigurator.class)
@Component
public class MyWebSocket {
    private Session session;
    private String username;
    private static Set<MyWebSocket> webSockets = new HashSet<>();
    /**
     * 建立连接
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        // 设置WebSocket的会话信息
        this.session = session;
        // 设置当前登录用户的HttpSession
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        if (httpSession.getAttribute("username") != null) {
            this.username = (String) httpSession.getAttribute("username");
            // 判断用户是否已经在线
            if (!userIsOnline(username)) {
                webSockets.add(this);
                System.out.println(username + "加入！ 当前在线人数：" + getOnlineUsers().size());
                // 组装消息
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("type","2");
                messageMap.put("fromName",username);
                messageMap.put("content",username + "已上线");
                messageMap.put("users",getOnlineUsers());
                pushMessage(messageMap, null);
            } else {
                // 用户已经在线的
                webSockets.add(this);
                System.out.println(username + "加入！ 当前在线人数：" + getOnlineUsers().size());
                // 此时只给当前链接发送消息
                // 组装消息
                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("type","2");
                messageMap.put("fromName",username);
                messageMap.put("content",username + "已上线");
                messageMap.put("users",getOnlineUsers());
                try {
                    session.getBasicRemote().sendText(JSONObject.toJSONString(messageMap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 收到客户端的消息
     * @param message 消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        // 组装消息
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("type","1");
        messageMap.put("fromName",this.username);
        messageMap.put("content",message);
        pushMessage(messageMap, null);
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose() {
        // 此时只给当前链接发送消息
        // 组装消息
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("type","2");
        messageMap.put("fromName",username);
        messageMap.put("content",username + "已下线");
        Set<String> users = getOnlineUsers();
        users.remove(username);
        messageMap.put("users",users);

        webSockets.remove(this);
        // 发送消息给其他在线用户
        for (MyWebSocket socket : webSockets) {
            try {
                if (!username.equals(socket.username)) {
                    socket.session.getBasicRemote().sendText(JSONObject.toJSONString(messageMap));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(username + "已下线！ 当前在线人数" + getOnlineUsers().size());
    }

    private boolean userIsOnline(String username) {
        boolean isOnline = false;
        for (MyWebSocket socket : webSockets) {
            if (socket.username.equals(username)) {
                isOnline = true;
                break;
            }
        }
        return isOnline;
    }

    /**
     * 获取当前用户列表
     * @return
     */
    private Set<String> getOnlineUsers() {
        Set<String> users = new HashSet<>();
        for (MyWebSocket socket : webSockets) {
            users.add(socket.username);
        }
        return users;
    }

    private void pushMessage(Map<String, Object> message, String toName) {
        // 发送所有人
        if (toName == null || "".equals(toName)) {
            for (MyWebSocket socket : webSockets) {
                try {
                    socket.session.getBasicRemote().sendText(JSONObject.toJSONString(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {  // 发送一个人
            for (MyWebSocket socket : webSockets) {
                try {
                    if (socket.username.equals(toName)) {
                        socket.session.getBasicRemote().sendText(JSONObject.toJSONString(message));
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

ChatController

```java
@Controller
public class ChatController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @PostMapping("/doLogin")
    @ResponseBody
    public Object doLogin(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Map<String, Object> result = new HashMap<>();
        if ("123456".equals(password)) {
            result.put("code","000000");
            result.put("msg","登录成功");
            request.getSession(true).setAttribute("username",username);
        } else {
            result.put("code","000001");
            result.put("msg","账号或密码错误");
        }
        return result;
    }
    @GetMapping("/chat")
    public String chat(HttpServletRequest request, Model model) {
        String username = (String) request.getSession(true).getAttribute("username");
        if (username == null) {
            return "login";
        }
        model.addAttribute("username", username);
        return "chat";
    }
}
```

login.html

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>登录</title>
    <!-- 新 Bootstrap4 核心 CSS 文件 -->
    <link rel="stylesheet" href="https://cdn.bootcss.com/twitter-bootstrap/4.4.1/css/bootstrap.min.css">
    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="https://cdn.bootcss.com/jquery/3.4.1/jquery.js"></script>
    <!-- bootstrap.bundle.min.js 用于弹窗、提示、下拉菜单，包含了 popper.min.js -->
    <script src="https://cdn.bootcss.com/twitter-bootstrap/4.4.1/js/bootstrap.bundle.min.js"></script>
    <!-- 最新的 Bootstrap4 核心 JavaScript 文件 -->
    <script src="https://cdn.bootcss.com/twitter-bootstrap/4.4.1/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col-md-6 offset-md-3">
            <form class="form-signin">
                <div class="text-center mb-4">
                    <h1 class="h3 mb-3 font-weight-normal">登录</h1>
                </div>

                <div class="form-label-group">
                    <input type="text" id="username" class="form-control" placeholder="用户名">
                    <label for="username">账号</label>
                </div>

                <div class="form-label-group">
                    <input type="password" id="password" class="form-control" placeholder="密码">
                    <label for="password">密码</label>
                </div>
                <button class="btn btn-lg btn-primary btn-block" type="button" id="loginBtn">登录</button>
            </form>
        </div>
    </div>
</div>
<script>
    $("#loginBtn").click(function () {
        var username = $("#username").val();
        var password = $("#password").val();
        $.ajax({
            url:"/doLogin",
            method:'post',
            dateType:"json",
            data:{
                "username":username,
                "password":password,
            },
            success:function(result){
                console.log(result);
                console.log(result.code);
                if (result.code == "000000") {
                    window.location.href='/chat';
                } else {
                    alert(result.msg);
                }
            },
            error: function(result) {
                alert("登录失败");
            }
        });
    });
</script>
</body>
</html>
```

chat.html

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>登录</title>
    <!-- 新 Bootstrap4 核心 CSS 文件 -->
    <link rel="stylesheet" href="https://cdn.bootcss.com/twitter-bootstrap/4.4.1/css/bootstrap.min.css">
    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="https://cdn.bootcss.com/jquery/3.4.1/jquery.js"></script>
    <!-- bootstrap.bundle.min.js 用于弹窗、提示、下拉菜单，包含了 popper.min.js -->
    <script src="https://cdn.bootcss.com/twitter-bootstrap/4.4.1/js/bootstrap.bundle.min.js"></script>
    <!-- 最新的 Bootstrap4 核心 JavaScript 文件 -->
    <script src="https://cdn.bootcss.com/twitter-bootstrap/4.4.1/js/bootstrap.min.js"></script>
</head>
<body>
<div id="app" class="container">
    <div class="row">
        <div class="col-md-6">
            <div class="card">
                <h5 class="card-header">聊天窗口</h5>
                <div id="chatBox" style="height: 300px;overflow-y: scroll;">
                    <ul class="list-group">
                        <div v-for="data in userDatas">
                            <li
                                    v-if="currentUser != data.fromName"
                                    class="list-group-item" style="border: 1px;">
                                {{data.fromName}}:{{data.content}}
                            </li>
                            <li
                                    v-else
                                    class="list-group-item" style="border: 1px;text-align: right;">
                                {{data.content}} : {{data.fromName}}
                            </li>
                        </div>
                    </ul>
                </div>
            </div>
            <form>
                <div class="form-group">
                    <label for="content">内容:</label>
                    <textarea class="form-control" rows="4" id="content"></textarea>
                    <button style="margin-top: 10px;" type="button" class="btn btn-primary" id="sendBtn">发送</button>
                </div>
            </form>
        </div>
        <div class="col-md-6">
            <div class="card">
                <h5 class="card-header">在线列表</h5>
                <ul class="list-group">
                    <li v-for="data in sysUsers" class="list-group-item" style="border: 1px;">
                        {{data}}
                    </li>
                </ul>
            </div>

            <div class="card" style="margin-top: 10px;">
                <h5 class="card-header">系统消息</h5>
                <ul class="list-group">
                    <li v-for="data in sysDatas" class="list-group-item" style="border: 1px;">
                        {{data.content}}
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.bootcss.com/vue/2.6.11/vue.min.js"></script>
<script th:inline="javascript">
    var username = [[${username}]];
    var ws;

    var vue = new Vue({
        el: '#app',
        data: {
            currentUser: username,
            userDatas:[],
            sysDatas:[],
            sysUsers:[]
        }
    });


    console.log(username);
    if ("WebSocket" in window)
    {
        console.log("您的浏览器支持 WebSocket!");
        // 打开一个 web socket
        ws = new WebSocket("ws://localhost:8080/websocket");
        ws.onopen = function()
        {
            console.log("已经与服务器建立了连接...");
        };

        ws.onmessage = function (event)
        {
            console.log("接收到服务器发送的数据...");
            console.log(event.data);
            var rdata = JSON.parse(event.data);
            if(rdata.type == 1) {
                vue.$data.userDatas.push(rdata);
            } else if(rdata.type == 2) {
                vue.$data.sysDatas.push(rdata);
                vue.$data.sysUsers = rdata.users;
            }
        };

        ws.onclose = function()
        {
            // 关闭 websocket
            alert("连接已关闭...");
        };
    }
    else
    {
        // 浏览器不支持 WebSocket
        alert("您的浏览器不支持 WebSocket!");
    }

    $("#sendBtn").click(function() {
        var content = $("#content").val();
        if(content == "") {
            alert("发送内容不能为空");
        } else {
            ws.send(content);
            $("#content").val("");
        }
    });
</script>
</body>
</html>
```

# 效果图

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200411195714214.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpemNfbGl6Yw==,size_16,color_FFFFFF,t_70#pic_center)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200411195730243.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpemNfbGl6Yw==,size_16,color_FFFFFF,t_70#pic_center)

