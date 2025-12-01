### 下载 centOS镜像 和 vmware(虚拟机)
- centOS7 镜像：`D:\VM_OS\CentOS-7-x86_64-Minimal-2009.iso`
- 安装：均默认即可，注意开启网络（centOS安装过程中，会有个整体系统配置页面），另外注意设置root和密码
### 下载finalShell 或者其他ssh工具
- finalShell 下载地址：`https://www.hostbuf.com/t/988.html`
- 安装：均默认即可
### 使用ssh连接centOS
- 使用命令查看centos主机ip地址：`ip a`，查看ens33的inet地址，例如：`192.168.1.100`。
- 打开finalShell，点击`文件`->`新建会话`
- 输入会话名称：`centOS`
- 输入主机地址：`192.168.1.100`
- 输入端口号：`22`
- 输入用户名：`root`
- 输入密码：`root`
- 点击`连接`
### 在centOS中安装docker
- `curl -o /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo`
- `sudo yum install -y yum-utils`
- 下面是个整体（\结尾表示命令未结束）：`sudo yum-config-manager \`
`--add-repo \`
`http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo`
- `sudo yum install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin`
- 启动docker `sudo systemctl start docker`
- 设置开机启动 `sudo systemctl enable docker`
### 将初始配置文件夹 `docker-compose/`下的docker-compose.yml的seata写死的ip改为自己的虚拟机ip，并将整个文件夹所有文件和文件夹 上传至 `/root/hmall-docker` 目录
### 先换docker的源
`https://docker.xuanyuan.me/`
`https://mp.weixin.qq.com/s?__biz=Mzg4ODQ1NTE2Mg==&mid=2247573614&idx=1&sn=6371a462fdccd91edf4bf7445f3adef6&chksm=cee53aae5c2744d86ab16201d665dfc0ad3a868f32bc55f070e82e6537d5cceaad2469173592&scene=27`（20251130可用）
按照里面的步骤换源
一定要记着换完源，要重启docker `sudo systemctl restart docker`
### 关闭防火墙
- `sudo systemctl stop firewalld`
- `sudo systemctl disable firewalld`   # 不想开机自启的话

### 快速启动docker中的所有服务，包括mysql、nacos、seata、mq
- `cd /root/hmall-docker`
- `export SEATA_IP=$(hostname -I | awk '{print $1}')`
- `docker compose up -d`

启动完毕后，查看docker compose ps确认所有服务都启动成功
没有的话，就单独启动：`docker compose up -d 服务名`,如`docker compose up -d seata`
如果还是报错，就重启docker `sudo systemctl restart docker`
然后启动所有服务：`docker compose up -d`

### Windows 上按原文档启动 hmall-nginx和各个微服务
#### 前端Nginx部署
1. 将本文件夹中的hmall-nginx放到一个不含中文的文件夹，比如我放到D盘
2. 在当前文件夹下手动建一个文件夹：temp 
3. 不进入temp。**使用管理员cmd（右键开始菜单-终端管理员，然后cd到当前文件夹D:\gitRepository\hmall\hmall-nginx）运行下面的不同命令来进行nginx部署，这里只启动**
```Bash
# 启动nginx
start nginx.exe
# 停止使用任务管理器
```
3. 访问http://localhost:18080看能否看到前端页面

注意，上面启动nginx应该会出现问题，查看log下的error.log
- 如果是` [emerg] 53532#23980: CreateDirectory() "D:\hmall-nginx/temp/client_body_temp" failed (3: The system cannot find the path specified)`
则需要手动创建临时目录temp
- 如果是` [emerg] 60632#35676: bind() to 0.0.0.0:80 failed (10013: An attempt was made to access a socket in a way forbidden by its access permissions)`
则需要以管理员身份运行cmd,将conf目录下的nginx.conf文件中的
`listen       80;`
改为一个大于1024的，比如`1080;`
- 重新访问http://localhost:18080/

#### 后端部分
在代码里全局查找`192.168.203.131`替换为自己的虚拟机ip
注意jdk版本，jdk11可以运行。（在项目结构-项目中选择sdk：corretto-11）
一次启动所有微服务：
- GatewayApplication
- ItemApplication
- CartApplication
- UserApplication
- TradeApplication
- PayApplication
#### nacos中加入共享配置
见README.md中的nacos做配置中心 共享配置部分，共6个配置文件

#### rabbitMQ验证
访问 http://192.168.203.131:15672即可查看RabbitMQ控制台，账号hmall，密码123
admin-右侧Virtual Host
下面创建一个虚拟主机/hmall

在queues-下面add一个队列，名字为`trade.delay.order.queue`
再新建一个队列，名字为`trade.pay.success.queue`

此时还需要安装延迟插件：
执行`docker volume ls`查看带有mq的挂载卷（也就是docker的mq具体映射到了哪个虚拟机的文件夹）
执行`docker volume inspect hmall-docker_mq-plugins`查看具体挂在位置
之后将`docker-compose/rabbitmq_delayed_message_exchange-3.8.17.8f537ac.ez`
复制到挂载卷对应的文件夹下，比如我的是`/var/lib/docker/volumes/hmall-docker_mq-plugins/_data`
之后执行`docker exec -it mq rabbitmq-plugins enable rabbitmq_delayed_message_exchange`启动延迟插件
### 附录：
##### docker验证：
```docker compose ps```
 看到 mysql / nacos / seata / mq 都是 Up

##### Windows 上做连接数据库验证和RabbitMQ验证：
Navicat 连接：虚拟机IP:3306，root / 123
浏览器访问： `http://192.168.203.131:8848/nacos（nacos/nacos）`
MQ验证：
浏览器访问：`http://虚拟机IP:15672（hmall/123）`

##### 微服务里或者nacos中共享配置：
MySQL：host = 虚拟机IP，port=3306，user=root，password=123
Nacos：server-addr = 虚拟机IP:8848
Seata：Nacos 里 shared-seata.yaml 里 server-addr 写 虚拟机IP:8848
RabbitMQ：Nacos 里 shared-mq.yaml 里 host = 虚拟机IP，port=5672，user=hmall，pw=123
##### 前端和微服务启动
Windows 上按原文档启动 hmall-nginx，并启动每一个微服务