[![Codacy Badge](https://api.codacy.com/project/badge/Grade/98c8a0ea3b43404aa8a820078e2c6e12)](https://www.codacy.com/app/104381832/Limitart?utm_source=github.com&utm_medium=referral&utm_content=HankXV/Limitart&utm_campaign=badger)
[![Build Status](https://travis-ci.org/HankXV/Limitart.svg?branch=master)](https://travis-ci.org/HankXV/Limitart)
# 什么是Limitart?
一个帮助您快速搭建起游戏服务器或者是中小型网络服务的框架
## 环境要求
Jdk8或以上
# 快速开始
首先，我们需要定义一个网络通信的消息类

```java

	public class BinaryMessageDemo extends Message {
		// transfer of information
		public String info;
	
		//  message id
		@Override
		public short getMessageId() {
			return 1;
		}
	}

```

再创建一个处理消息的处理器

```java

	public class BinaryHandlerDemo implements IHandler<BinaryMessageDemo> {
	
		@Override
		public void handle(BinaryMessageDemo msg) {
			System.out.println("server received message:" + msg.info);
		}
	
	}
	
```

初始化一个消息工厂把消息处理器注册进去

```java

	MessageFactory factory = new MessageFactory().registerMsg(BinaryHandlerDemo.class);
		
```

最后实例化一个服务器并且开启服务

```java

			new BinaryServer.BinaryServerBuilder()
				// port
				.addressPair(new AddressPair(8888))
				// register factory
				.factory(messageFactory).build();
				.startServer();
		
```

初始化一个客户端，在客户端链接验证通过后发送消息给服务器

```java

			new BinaryClient.BinaryClientBuilder()
				.remoteAddress(new AddressPair("127.0.0.1", 8888))
				.onConnectionEffective(c -> {
					BinaryMessageDemo message = new BinaryMessageDemo();
					message.info = "Hello Limitart!";
					c.sendMessage(message, null);
				}).build()
				.connect();
				
```

最后服务器收到了消息

```

	server received message:Hello Limitart!

```
	
# 消息编码
如果你不使用此框架而是其他地方的客户端，你需要了解此框架的默认链接过程和编码模式。

	客户端--socket-->服务器--发送验证码-->客户端--解析结果-->服务器--检查并发送成功消息-->客户端
		
完成此过程后，服务器和客户端都会触发`onConnectionEffective`回调。如果客户端在一定时间内未完成验证，其链接会被踢掉。其中链接验证消息参考：`org.slingerxv.limitart.net.binary.message.impl.validate`包，验证码加密解密参考`org.slingerxv.limitart.util.SymmetricEncryptionUtil`<br>
消息二进制编码为：

	消息长度(short,包含消息体长度+2)+消息ID(short)+消息体
	
# 游戏服务器开发思路
	1.首先你需要对接好网络通信
	2.根据游戏的不同来指定线程模型，在游戏中指定线程基本有两个目的，第一，IO操作或者复杂计算操作不能阻塞玩家的操作，第二，如果玩家有数据交互，他们应当在同一个线程
	3.根据2的参考方式制定了线程模型，然后在服务器的`dispatchMessage`回调方法中发消息分发到不同线程中
	4.如果你不做分发，直接在`dispatchMessage`中执行handler，那么就默认使用了Netty的work线程，他只保证了一个channel一定会在同一个线程运行
	5.线程间通信的消息队列推荐使用`DisruptorTaskQueue`，如果你有使用类似地图或者房间的线程需求(既一组需要交互的玩家要在一个线程里)，推荐使用`AutoGrowthTaskQueueGroup`，如果你不知道你该怎么使用线程，那么推荐你使用`FunctionalTaskQueueGroup`
	6.如果你需要使用控制台来操作服务器，那么可以使用`ConsoleServer`或者`HttpServer`嵌入游戏服务器中来进行交互
	7.作者不提倡滥用线程，所以请使用者预估好使用场景，再做相应的线程安排
	8.在`game`包下是属于游戏逻辑层的抽象，比如背包、道具、帮会、扑克等，后面会慢慢增加
	9.如果你要做排行榜，推荐使用`FrequencyReadRankMap`或`FrequencyWriteRankMap`，推荐排行榜存储量为10万数量级及一下
	10.游戏服务器的热更新请参考`org.slingerxv.limitart.script`
	11.游戏中常用的唯一Id生成请参考`org.slingerxv.limitart.util.UniqueIdUtil`
	12.带有`@beta`标记的代表此API是新加入的测试版
# 更新日志
## v2.0-alpha
