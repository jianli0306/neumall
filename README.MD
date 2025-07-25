# 整体代码说明
## hm-api
存openFeign远程调用的api，类似于rpc当中的代理
以及openFeign的配置类和DTO
client就是各个客户端代理
fallback用于sentinel的降级、快速失败
## hm-common
- 用到的通用异常处理器类
- 各种异常类
- 各种配置类，如json、Mq、Mvc、MyBatis
- 分页查询用到的通用类、通用结果类
- 拦截器，用于用户登录
- 各种工具类，比如BeanUtils、存储用户登录信息的UserContext实际上就是ThreadLocal的封装
## hm-gateway
SpringCloudGateway，也就是网关。
做网关鉴权：登录校验和传递用户信息；做统一路由等等
## hm-nginx
前端以及nginx代理，转发请求到后端
# 使用指南、项目开启指南
## 可以参考这个文档
https://b11et3un53m.feishu.cn/wiki/R4Sdwvo8Si4kilkSKfscgQX0niB
## 先安装虚拟机，并在windows使用finalShell或其他连接虚拟机
## 下载docker，并配置镜像源
参照下面网页中的-镜像更换方法
https://docker.xuanyuan.me/
https://www.coderjia.cn/archives/dba3f94c-a021-468a-8ac6-e840f85867ea

## 虚拟机使用docker安装数据库，并配置数据库
1. 将该文件夹下的mysql文件夹放到虚拟机的/root中
2. 创建通用docker网络
```Bash
docker network create hm-net
```
3. 使用docker安装并配置mysql，启动mysql容器
```Bash
docker run -d \
  --name mysql \
  -p 3306:3306 \
  -e TZ=Asia/Shanghai \
  -e MYSQL_ROOT_PASSWORD=123 \
  -v /root/mysql/data:/var/lib/mysql \
  -v /root/mysql/conf:/etc/mysql/conf.d \
  -v /root/mysql/init:/docker-entrypoint-initdb.d \
  --network hm-net\
  mysql
```
注意，重启docker的mysql只需要`docker ps -a`查看是否已存在mysql容器，存在则直接启动`docker start mysql`

### windows下载navicat，图形化方式操作数据库，远程连接到虚拟机的mysql容器
使用navicat运行各个sql脚本（**除了nacos.sql和user.sql**），建立各个微服务的数据库
## 前端Nginx部署
1. 将本文件夹中的hmall-nginx放到一个不含中文的文件夹，比如我放到D盘
2. 进入该文件夹，**使用管理员cmd运行下面的不同命令来进行nginx部署**
```Bash
# 启动nginx
start nginx.exe
# 停止
nginx.exe -s stop
# 重新加载配置
nginx.exe -s reload
# 重启
nginx.exe -s restart
```
3. 访问http://localhost:18080

注意，上面启动nginx应该会出现问题，查看log下的error.log
- 如果是` [emerg] 53532#23980: CreateDirectory() "D:\hmall-nginx/temp/client_body_temp" failed (3: The system cannot find the path specified)`
则需要手动创建临时目录temp
- 如果是` [emerg] 60632#35676: bind() to 0.0.0.0:80 failed (10013: An attempt was made to access a socket in a way forbidden by its access permissions)`
则需要以管理员身份运行cmd,将conf目录下的nginx.conf文件中的
```Bash
listen       80;
```
改为一个大于1024的，比如
```Bash
listen       1080;
```
- 重新访问http://localhost:18080/

## Nacos
1. navicat连接虚拟机docker数据库
2. 在该数据库运行课前文件中的nacos.sql
3. 将改过ip地址配置(custom.env中的ip改为自己的)的课前文件中的nacos目录上传至虚拟机的/root目录(使用finalshell上传)
4. 虚拟机进入root，执行以下命令拉取nacos镜像并启动nacos容器
```Bash
docker run -d \
--name nacos \
--env-file ./nacos/custom.env \
-p 8848:8848 \
-p 9848:9848 \
-p 9849:9849 \
--restart=always \
nacos/nacos-server:v2.1.0-slim
```
5. 启动完成后，访问下面地址：http://192.168.18.128:8848/nacos/，注意将192.168.18.128替换为你自己的虚拟机IP地址。
6. 首次访问会跳转到登录页，账号密码都是nacos

7. 在idea项目中双击shift全局搜索server-addr，将nacos的server-addr改为`192.168.18.128:8848`

## yaml文件的mysql的db配置
**注意除了hm-api、hm-common、hm-gateway、hm-service**
user-service/src/main/resources/application.yaml
pay-service/src/main/resources/application.yaml
trade-service/src/main/resources/application.yaml
cart-service/src/main/resources/application.yaml
item-service/src/main/resources/application.yaml

以及各个db的配置
pay-service/src/main/resources/application-local.yaml
pay-service/src/main/resources/application-dev.yaml

host:192.168.18.128
password:123
注意pay-sevice的application.yaml 中数据库配置hm-pay库

