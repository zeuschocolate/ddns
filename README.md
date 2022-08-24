# ddns
阿里云域名ddns动态解析

## 说明
不完全是自己写的!参考了别的优秀开发者的思路,解决了部分启动问题
当你的服务器公共IP是动态变化时,访问服务器就变得不方便起来,这时候一个能将动态变化的公共IP及时绑定到你的阿里云域名上的服务就很重要了


## 配置方式
application.properties内相关配置即可
默认检测时间间隔是半小时检测一次IP是否发生变化,需要修改可以在DDNSSchedule.java内配置@Scheduled(fixedRate=1800000)的值

## 启动方式
建议后台启动
nohup java -jar ddns.jar >msg.log 2>&1

