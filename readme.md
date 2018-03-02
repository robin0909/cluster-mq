## manager 模块

### 项目打包编译

```$xslt
mvn clean install -Dmaven.test.skip=true

BUILD SUCCESS 说明构建成功

或者在idea右侧的maven project 窗口的 cluster-mq 模块下点击  clean 和 package 就可以构建项目

构建好了之后，会在 manager/target下面有一个jar(cluster-mq-manager.jar 体积大的)
```

### 启动
1. 不带参数启动时，默认的是设置自己为 leader节点

	```
	java -jar cluster-mq-manager.jar
	
	默认属性值：
	--cluster.ip=null
	--cluster.port=0 
	--server.port=8080
	--tcp.port=9000
	```
2. 带参数启动节点

	```$xslt
	java -jar cluster-mq-manager.jar --cluster.ip=127.0.0.1 --cluster.port=8080 --server.port=8081 --tcp.port=9001
	
	java -jar cluster-mq-manager.jar --cluster.ip=127.0.0.1 --cluster.port=8080 --server.port=8082 --tcp.port=9002
	```

### api
    
## client 模块

## test 模块

## manager web下的adminlte模块安装

    在 web下的bower.json同级目录下，执行 bower install 
