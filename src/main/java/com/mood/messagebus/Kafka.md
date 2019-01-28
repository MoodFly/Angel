![image](http://kafka.apache.org/images/logo.png)
## 文档
[中文官方文档【半兽人】翻译](http://www.orchome.com/kafka/index)

[kafka数据可靠性深度解读](http://www.uml.org.cn/bigdata/201705093.asp?artid=19347)
## Kafka功能
![image](http://kafka.apache.org/images/kafka_diagram.png)
### 发布 & 订阅 
数据流，如消息传递系统
### 处理 
高效并实时
### 存储
数据流安全地在分布式集群中复制存储

---
kafka是用于构建实时数据管道和流应用程序。具有横向扩展，容错，wicked fast（超级快）等优点，并已在成千上万家公司运行。
## Kafka概念解释
#### Broker
Controller:主要负责Partition管理和副本状态管理，也会执行类似于重分配Partition之类的管理任务。如果当前的Controller失败，会从其他正常的Broker中重新选举Controller。

consumer  -- consumerGroup 
#### 分区
是Kafka中横向扩展和一切并行化的基础，
对某一个Partition的所有读写请求都是只由Leader来处理，所以Kafka会尽量把Leader均匀的分散到集群的各个节点上，以免造成网络流量过于集中；
每个分区的消息是有序的，多个分区之间的消息不是有序的
$KAFKA_HOME/config/server.properties
num.partitions=3
#### 分区状态
##### Kafka中Partition的四种状态
1.NonExistentPartition —— 这个状态表示该分区要么没有被创建过或曾经被创建过但后面被删除了。
2.	NewPartition —— 分区创建之后就处于NewPartition状态。在这个状态中，分区应该已经分配了副本，但是还没有选举出leader和ISR。
3.	OnlinePartition —— 一旦分区的leader被推选出来，它就处于OnlinePartition状态。
4.	OfflinePartition —— 如果leader选举出来后，leader broker宕机了，那么该分区就处于OfflinePartition状态。

##### 状态的转换关系
NonExistentPartition -> NewPartition
首先将第一个可用的副本broker作为leader broker并把所有可用的副本对象都装入ISR，然后写leader和ISR信息到zookeeper中保存
然后对这个分区，发送LeaderAndIsr请求到每个可用的副本broker，以及UpdateMetadata请求到每个可用的broker上
OnlinePartition, OfflinePartition -> OnlinePartition
为该分区选取新的leader和ISR以及接收LeaderAndIsr请求的一组副本，然后写入leader和ISR信息到zookeeper中保存。

NewPartition, OnlinePartition -> OfflinePartition
标记分区状态为离线(offline)。
OfflinePartition -> NonExistentPartition
离线状态标记为不存在分区，表示该分区失败或者被删除。
#### Controller选择器
KafkaController中共定义了五种selector选举器：
1.	ReassignedPartitionLeaderSelector
从可用的ISR中选取第一个作为leader，把当前的ISR作为新的ISR，将重分配的副本集合作为接收LeaderAndIsr请求的副本集合。
2.	PreferredReplicaPartitionLeaderSelector
如果从assignedReplicas取出的第一个副本就是分区leader的话，则抛出异常，否则将第一个副本设置为分区leader。
3.	ControlledShutdownLeaderSelector
将ISR中处于关闭状态的副本从集合中去除掉，返回一个新新的ISR集合，然后选取第一个副本作为leader，然后令当前AR作为接收LeaderAndIsr请求的副本。
4.	NoOpLeaderSelector
原则上不做任何事情，返回当前的leader和isr。
5.	OfflinePartitionLeaderSelector
从活着的ISR中选择一个broker作为leader，如果ISR中没有活着的副本，则从assignedReplicas中选择一个副本作为leader，leader选举成功后注册到Zookeeper中，并更新所有的缓存。

所有的leader选择完成后，都要通过请求把具体的request路由到对应的handler处理。

```
def handle(request: RequestChannel.Request) {  
  try{  
    trace("Handling request: " + request.requestObj + " from client: " + request.remoteAddress)  
    request.requestId match {  
      case RequestKeys.ProduceKey => handleProducerRequest(request)  // producer  
      case RequestKeys.FetchKey => handleFetchRequest(request)       // consumer  
      case RequestKeys.OffsetsKey => handleOffsetRequest(request)  
      case RequestKeys.MetadataKey => handleTopicMetadataRequest(request)  
      case RequestKeys.LeaderAndIsrKey => handleLeaderAndIsrRequest(request) //成为leader或follower设置同步副本组信息  
      case RequestKeys.StopReplicaKey => handleStopReplicaRequest(request)  
      case RequestKeys.UpdateMetadataKey => handleUpdateMetadataRequest(request)  
      case RequestKeys.ControlledShutdownKey => handleControlledShutdownRequest(request)  //shutdown broker  
      case RequestKeys.OffsetCommitKey => handleOffsetCommitRequest(request)  
      case RequestKeys.OffsetFetchKey => handleOffsetFetchRequest(request)  
      case requestId => throw new KafkaException("Unknown api code " + requestId)  
    }  
  } catch {  
    case e: Throwable =>  
      request.requestObj.handleError(e, requestChannel, request)  
      error("error when handling request %s".format(request.requestObj), e)  
  } finally  
    request.apiLocalCompleteTimeMs = SystemTime.milliseconds  
}
```
#### offset 
一个连续的序列号叫做offset,用于partition唯一标识一条消息，代表一个分区中的第几个消息，每个分区的offset都是从0开始，每添加一个消息，该值就+1；
在0.8.2之前，comsumer定期提交已经消费的kafka消息的offset位置到zookeeper中保存。对zookeeper而言，每次写操作代价是很昂贵的，而且zookeeper集群是不能扩展写能力的。在0.8.2开始，可以把comsumer提交的offset记录在compacted topic（__comsumer_offsets）中，该topic设置最高级别的持久化保证，即ack=-1。__consumer_offsets由一个三元组< comsumer group, topic, partiotion> 组成的key和offset值组成，在内存也维持一个最新的视图view，所以读取很快
###### segment
partition还可以细分为segment，一个partition物理上由多个segment组成
###### 消息存储目录：
$KAFKA_HOME/config/server.properties
log.dirs=/tmp/kafka-logs
文件存储到/tmp/kafka-logs
###### Partition的物理结构
Topic 名称 mobileReportUsers  ，分区数为4；
则/tmp/kafka-logs/目录下会有4个mobileReportUsers-n的文件夹，n{0,1,2,3}
每个文件下存储这segment的.log数据文件与.index索引文件
每个文件夹就是一个分区，文件夹下的log与index文件就是分区的segment；
###### Segment文件命名规则
partition全局的第一个segment从0开始，后续每个segment文件名为上一个segment文件最后一条消息的offset值，数值大小为64位，20位数字字符长度，没有数字用0填充；
所以每个segment的offset起始偏移为“文件名+1”
###### Segment的insex.log文件
.index里面存储的是offset 与该消息在当前segment中的物理偏移量存储格式为
[offset, 物理偏移量]
###### 如何根据offset查找消息
先用二分法，根据segment的文件名，定位到.index文件，然后从index文件中查找到该offset所在当前.log文件中的偏移量，然后从.log文件偏移量的位置开始读取；每条消息都有固定的物理结构，包括：offset（8 Bytes）、消息体的大小（4 Bytes）、crc32（4 Bytes）、magic（1 Byte）、attributes（1 Byte）、key length（4 Bytes）、key（K Bytes）、payload(NBytes)等等字段，可以确定一条消息的大小，即读取到哪里截止。
## 可靠性与副本
### 副本
为了提高消息的可靠性，Kafka每个topic的partition有N个副本（replicas），其中N(大于等于1)是topic的复制因子（replica fator）的个数，配置副本方式：
$KAFKA_HOME/config/server.properties
default.replication.refactor=2

在Kafka中发生复制时确保partition的日志能有序地写到其他节点上，N个replicas中，其中一个replica为leader，其他都为follower, leader处理partition的所有读写请求，与此同时，follower会被动定期地去复制leader上的数据
#### 副本中的三种节点
1.	分区副本的leader                    （ISR）
2.	分区处于同步状态的in-sync flower  （ISR） （满足同步阈值的队列）
3.	分区处于非同步状态out-of-sync flower	（OSR） （不满足同步阈值的队列）
4.	分区处于非同步状态的stuck  flower  被阻塞的队列
ISR包括leader和in-sync flower；
![image](http://www.uml.org.cn/bigdata/images/2017050935.png)

#### LEO
LogEndOffset的缩写，表示每个partition的log最后一条Message的位置
#### HW
HW是HighWatermark的缩写，取一个partition对应的ISR队列中，最小的LEO作为HW， consumer能够看到的此partition的位置;
HW = min(Leader.HW, Follower.offset)
对于leader新写入的消息，consumer不能立刻消费，leader会等待该消息被所有ISR中的replicas同步后更新HW，此时消息才能被consumer消费。这样就保证了如果leader所在的broker失效，该消息仍然可以从新选举的leader中获取。对于来自内部broKer的读取请求，没有HW的限制。
![image](http://www.uml.org.cn/bigdata/images/2017050937.png)

#### AR
所有副本（replicas）统称为Assigned Replicas，即AR
#### ISR（In-Sync Replicas）
副本同步队列，所有不落后的replica集合, 不落后有两层含义:距离上次FetchRequest的时间不大于某一个值或落后的消息数不大于某一个值
#### OSR（out-of-sync）
副本非同步队列
#### 副本同步队列阈值
Leader负责跟踪副本状态，满足以下条件的flower进入ISR队列，不满足以下条件的flower进入OSR队列:
- replica.lag.max.messages设置为4，表明只要follower落后leader不超过3，就不会从同步副本列表中移除（该参数在0.10.x版本中已经移除，如果短时间内生产者发送了大量的消息，那么就会导致flower直接掉线）
- replica.lag.time.max设置为500 ms，表明只要follower向leader发送请求时间间隔不超过500 ms，就不会被标记为死亡,也不会从同步副本列中移除
#### Flower与leader副本不同步的原因
一个partition的follower落后于leader足够多时，被认为不在同步副本列表或处于滞后状态
1. 慢副本：在一定周期时间内follower不能追赶上leader。最常见的原因之一是I / O瓶颈导致follower追加复制消息速度慢于从leader拉取速度。
2. 卡住副本：在一定周期时间内follower停止从leader拉取请求。follower replica卡住了是由于GC暂停或follower失效或死亡。
3. 新启动副本：当用户给主题增加副本因子时，新的follower不在同步副本列表中，直到他们完全赶上了leader日志。

#### 同步信息的保存zookeeper
Kafka的ISR的管理最终都会反馈到Zookeeper节点上。具体位置为：
/brokers/topics/[topicname]/partitions/[partition]/state。目前有两个地方会对这个Zookeeper的节点进行维护：
- Controller来维护：Kafka集群中的其中一个Broker会被选举为Controller，主要负责Partition管理和副本状态管理，也会执行类似于重分配partition之类的管理任务。在符合某些特定条件下，Controller下的LeaderSelector会选举新的leader，ISR和新的leader_epoch及controller_epoch写入Zookeeper的相关节点中。同时发起LeaderAndIsrRequest通知所有的replicas。
- leader来维护：leader有单独的线程定期检测ISR中follower是否脱离ISR, 如果发现ISR变化，则会将新的ISR的信息返回到Zookeeper的相关节点中。

#### 可靠级别：副本同步策略
一条消息只有被ISR中的所有follower都从leader复制过去才会被认为已提交。这样就避免了部分数据被写进了leader，还没来得及被任何follower复制就宕机了，而造成数据丢失。而对于producer而言，它可以选择是否等待消息commit，这可以通过request.required.acks来设置。这种机制确保了只要ISR中有一个或者以上的follower，一条被commit的消息就不会丢失。

Kafka在Zookeeper中为每一个partition动态的维护了一个ISR，这个ISR里的所有replica都跟上了leader（应该是在阈值允许的范围内，认为跟上了leader	），只有ISR里的成员才能有被选为leader的可能（unclean.leader.election.enable=false）。在这种模式下，对于f+1个副本，一个Kafka topic能在保证不丢失已经commit消息的前提下容忍f个副本的失败


当producer向leader发送数据时，可以通过request.required.acks参数来设置数据可靠性的级别：
- 1（默认）：这意味着producer在ISR中的leader已成功收到的数据并得到确认后发送下一条message。如果flowser还没有来的及fetch数据，leader宕机了，则会丢失数据。
- 0：这意味着producer无需等待来自broker的确认而继续发送下一批消息。这种情况下数据传输效率最高，但是数据可靠性确是最低的。
- -1：producer需要等待ISR中的所有follower都确认接收到数据后才算一次发送完成，可靠性最高。但是这样也不能保证数据不丢失，比如当ISR中只有leader时（前面ISR那一节讲到，ISR中的成员由于某些情况会增加也会减少，最少就只剩一个leader），这样就变成了acks=1的情况。

---
##### case问题处理
注意：当取值为-1时，另外一个参数min.insync.replicas参数开始生效，默认值为1，代表ISR中的最小副本数是多少，如果ISR中的副本数少于min.insync.replicas配置的数量时，客户端会返回异常（这时候write不能服务，但是read能继续正常服务）：org.apache.kafka.common.errors.NotEnoughReplicasExceptoin:Messages are rejected since there are fewer in-sync replicas than required。
###### 此种情况恢复方案：
- 尝试恢复(重启)replica-0，如果能起来，系统正常；
- 如果replica-0不能恢复，需要将min.insync.replicas设置为1，恢复write功能。

###### 在ack=-1的时候会有三种情况：
1.	数据发送到leader后, ISR的follower全部完成数据同步后，leader此时挂掉，那么会选举出新的leader，数据不会丢失。
2.	数据发送到leader后，部分ISR的副本同步，leader此时挂掉。比如follower1和follower2都有可能变成新的leader, producer端会得到返回异常，producer端会重新发送数据，数据可能会重复。
3.	数据发送到leader后，follower还没同步到任何数据，没有同步到数据的follower被选举为新的leader的话，这样消息就不会重复

#### 数据不一致怎么办
leader挂掉会重新选举，新的leader会发送“指令”让其余的follower截断至自身的HW的位置然后再拉取新的消息。如果失败的follower恢复过来，它首先将自己的log文件截断到上次checkpointed时刻的HW的位置，之后再从leader中同步消息；
这样就保证数据的一致性了
![image](http://www.uml.org.cn/bigdata/images/20170509311.png)
如上图，某个topic的某partition有三个副本，分别为A、B、C。A作为leader肯定是LEO最高，B紧随其后，C机器由于配置比较低，网络比较差，故而同步最慢。这个时候A机器宕机，这时候如果B成为leader，假如没有HW，在A重新恢复之后会做同步(makeFollower)操作，在宕机时log文件之后直接做追加操作，而假如B的LEO已经达到了A的LEO，会产生数据不一致的情况，所以使用HW来避免这种情况。

A在做同步操作的时候，先将log文件截断到之前自己的HW的位置，即3，之后再从B中拉取消息进行同步。

如果失败的follower恢复过来，它首先将自己的log文件截断到上次checkpointed时刻的HW的位置，之后再从leader中同步消息。leader挂掉会重新选举，新的leader会发送“指令”让其余的follower截断至自身的HW的位置然后再拉取新的消息。

当ISR中的个副本的LEO不一致时，如果此时leader挂掉，选举新的leader时并不是按照LEO的高低进行选举，而是按照ISR中的顺序选举。
#### Leader选举策略
两种方案：
1.	等待ISR中任意一个replica“活”过来，并且选它作为leader（unclean.leader.election.enable=false），kafka数据一致性大于持久化可用性；
2.	选择第一个“活”过来的replica（并不一定是在ISR中）作为leader （unclean.leader.election.enable=true 默认），kafka的持久化可用性大于数据一致性；

当unclean.leader.election.enable=false的时候，leader只能从ISR中选举，当ISR中所有副本都失效之后，需要ISR中最后失效的那个副本能恢复之后才能选举leader, 即leader 副本replica-0先失效，replica-1后失效，这时[ISR=(1),leader=-1]，需要replica-1恢复后才能选举leader；
这种情况下：如果replicat-1不能恢复，保守的方案建议把unclean.leader.election.enable设置为true,但是这样会有丢失数据的情况发生；
如果request.required.acks<>-1,这样就可以恢复服务；
如果request.required.acks=-1这样可以恢复read服务。还需要将min.insync.replicas设置为1，恢复write功能；

当ISR中的replica-0, replica-1同时宕机,此时[ISR=(0,1)],不能对外提供服务，此种情况恢复方案：尝试恢复replica-0和replica-1，当其中任意一个副本恢复正常时，如果request.required.acks<>-1，即可恢复服务；如果request.required.acks=-1，则对外可以提供read服务，直到2个副本恢复正常，write功能才能恢复，或者将将min.insync.replicas设置为1。

#### Rebalance
controlled.shutdown.enable ，是否在在关闭broker时主动迁移leader partition。基本思想是每次kafka接收到关闭broker进程请求时，主动把leader partition迁移到其存活节点上，即follow replica提升为新的leader partition。如果没有开启这个参数，集群等到replica会话超时，controller节点才会重现选择新的leader partition，这些leader partition在这段时间内也不可读写。如果集群非常大或者partition 很多，partition不可用的时间将会比较长
#### zk结构
![image](https://upload-images.jianshu.io/upload_images/2779043-ace9980fad0c49a4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/700)
#### 读写机制
使用直接内存和操作系统缓存
1. 写message
- 消息从java堆转入page cache(即物理内存)。
- 由异步线程刷盘,消息从page cache刷入磁盘。
2. 读message
- 消息直接从page cache转入socket发送出去。
- 当从page cache没有找到相应数据时，此时会产生磁盘IO,从磁盘Load消息到page cache,然后直接从socket发出去


Kafka重度依赖底层操作系统提供的PageCache功能。写盘是使用Asynchronous+Batch的方式，当上层有写操作时，操作系统只是将数据写入PageCache，同时标记Page属性为Dirty。当读操作发生时，先从PageCache中查找，如果发生缺页才进行磁盘调度，最终返回需要的数据。实际上PageCache是把尽可能多的空闲内存都当做了磁盘缓存来使用。同时如果有其他进程申请内存，回收PageCache的代价又很小，所以现代的OS都支持PageCache。
读取操作，有Read Request进来的时候分为两种情况，第一种是内存中完成数据交换，写数据的时候需要读的数据已经在page cache中存在了，一种是发生缺页中断，去硬盘上读取；

网络发送
Kafka采用了Sendfile技术，零拷贝；

Kafka的设计初衷是尽一切努力在内存中完成数据交换，无论是对外作为一整个消息系统，或是内部同底层操作系统的交互。如果Producer和Consumer之间生产和消费进度上配合得当，完全可以实现数据交换零I/O。这也就是我为什么说Kafka使用“硬盘”并没有带来过多性能损失的原因。下面是我在生产环境中采到的一些指标。

#### 优化建议
##### 消息大小配置
1. 修改kafka的broker配置：message.max.bytes（默认:1000000B），这个参数表示单条消息的最大长度。在使用kafka的时候，应该预估单条消息的最大长度，不然导致发送失败。
2. 修改kafka的broker配置：replica.fetch.max.bytes (默认: 1MB)，broker可复制的消息的最大字节数。这个值应该比message.max.bytes大，否则broker会接收此消息，但无法将此消息复制出去，从而造成数据丢失。
3. 修改消费者程序端配置：fetch.message.max.bytes (默认 1MB) – 消费者能读取的最大消息。这个值应该大于或等于message.max.bytes。如果不调节这个参数，就会导致消费者无法消费到消息，并且不会爆出异常或者警告，导致消息在broker中累积，此处要注意。

根据需要，调整上述三个参数的大小。但是否参数调节得越大越好，或者说单条消息越大越好呢？
##### 参考http://www.mamicode.com/info-detail-453907.html的说法：
1. 从性能上考虑：通过性能测试，kafka在消息为10K时吞吐量达到最大，更大的消息会降低吞吐量，在设计集群的容量时，尤其要考虑这点。
2. 可用的内存和分区数：Brokers会为每个分区分配replica.fetch.max.bytes参数指定的内存空间，假设replica.fetch.max.bytes=1M，且有1000个分区，则需要差不多1G的内存，确保 分区数*最大的消息不会超过服务器的内存，否则会报OOM错误。同样地，消费端的fetch.message.max.bytes指定了最大消息需要的内存空间，同样，分区数*最大需要内存空间 不能超过服务器的内存。所以，如果你有大的消息要传送，则在内存一定的情况下，只能使用较少的分区数或者使用更大内存的服务器。
3. 垃圾回收：更大的消息会让GC的时间更长（因为broker需要分配更大的块），随时关注GC的日志和服务器的日志信息。如果长时间的GC导致kafka丢失了zookeeper的会话，则需要配置zookeeper.session.timeout.ms参数为更大的超时时间。

##### kafka中处理超大消息的一些考虑
Kafka设计的初衷是迅速处理短小的消息，一般10K大小的消息吞吐性能最好（可参见LinkedIn的kafka性能测试）。但有时候，我们需要处理更大的消息，比如XML文档或JSON内容，一个消息差不多有10-100M，这种情况下，Kakfa应该如何处理？

针对这个问题，有以下几个建议：
- 最好的方法是不直接传送这些大的数据。如果有共享存储，如NAS, HDFS, S3等，可以把这些大的文件存放到共享存储，然后使用Kafka来传送文件的位置信息。
- 第二个方法是，将大的消息数据切片或切块，在生产端将数据切片为10K大小，使用分区主键确保一个大消息的所有部分会被发送到同一个kafka分区（这样每一部分的拆分顺序得以保留），如此以来，当消费端使用时会将这些部分重新还原为原始的消息。
- 第三，Kafka的生产端可以压缩消息，如果原始消息是XML，当通过压缩之后，消息可能会变得不那么大。在生产端的配置参数中使用compression.codec和commpressed.topics可以开启压缩功能，压缩算法可以使用GZip或Snappy。

不过如果上述方法都不是你需要的，而你最终还是希望传送大的消息，那么，则可以在kafka中设置下面一些参数：
broker 配置:
```
message.max.bytes (默认:1000000) – broker能接收消息的最大字节数，这个值应该比消费端的fetch.message.max.bytes更小才对，否则broker就会因为消费端无法使用这个消息而挂起。
log.segment.bytes (默认: 1GB) – kafka数据文件的大小，确保这个数值大于一个消息的长度。一般说来使用默认值即可（一般一个消息很难大于1G，因为这是一个消息系统，而不是文件系统）。
replica.fetch.max.bytes (默认: 1MB) – broker可复制的消息的最大字节数。这个值应该比message.max.bytes大，否则broker会接收此消息，但无法将此消息复制出去，从而造成数据丢失。
```
Consumer 配置:
```
fetch.message.max.bytes (默认 1MB) – 消费者能读取的最大消息。这个值应该大于或等于message.max.bytes。
所以，如果你一定要选择kafka来传送大的消息，还有些事项需要考虑。要传送大的消息，不是当出现问题之后再来考虑如何解决，而是在一开始设计的时候，就要考虑到大消息对集群和主题的影响。
```
性能: 根据前面提到的性能测试，kafka在消息为10K时吞吐量达到最大，更大的消息会降低吞吐量，在设计集群的容量时，尤其要考虑这点。
可用的内存和分区数：Brokers会为每个分区分配replica.fetch.max.bytes参数指定的内存空间，假设replica.fetch.max.bytes=1M，且有1000个分区，则需要差不多1G的内存，确保 分区数*最大的消息不会超过服务器的内存，否则会报OOM错误。同样地，消费端的fetch.message.max.bytes指定了最大消息需要的内存空间，同样，分区数*最大需要内存空间 不能超过服务器的内存。所以，如果你有大的消息要传送，则在内存一定的情况下，只能使用较少的分区数或者使用更大内存的服务器。
垃圾回收：到现在为止，我在kafka的使用中还没发现过此问题，但这应该是一个需要考虑的潜在问题。更大的消息会让GC的时间更长（因为broker需要分配更大的块），随时关注GC的日志和服务器的日志信息。如果长时间的GC导致kafka丢失了zookeeper的会话，则需要配置zookeeper.session.timeout.ms参数为更大的超时时间。
一切的一切，都需要在权衡利弊之后，再决定选用哪个最合适的方案。
##### Partition数量
Kafka的分区数量应该是Broker数量的整数倍，partition的数量大于等于broker的数量，并且所有partition的leader均匀分布在broker上；
1. Partition的数量并不是越多越好，Partition的数量越多，平均到每一个Broker上的数量也就越多。考虑到Broker宕机(Network Failure, Full GC)的情况下，需要由Controller来为所有宕机的Broker上的所有Partition重新选举Leader，假设每个Partition的选举消耗10ms，如果Broker上有500个Partition，那么在进行选举的5s的时间里，对上述Partition的读写操作都会触发LeaderNotAvailableException
2. 如果挂掉的Broker是整个集群的Controller，那么首先要进行的是重新任命一个Broker作为Controller。新任命的Controller要从Zookeeper上获取所有Partition的Meta信息，获取每个信息大概3-5ms，那么如果有10000个Partition这个时间就会达到30s-50s。而且不要忘记这只是重新启动一个Controller花费的时间，在这基础上还要再加上前面说的选举Leader的时间
3. 在Broker端，对Producer和Consumer都使用了Buffer机制。其中Buffer的大小是统一配置的，数量则与Partition个数相同。如果Partition个数过多，会导致Producer和Consumer的Buffer内存占用过大

##### Pagecache优化
1. Kafka官方并不建议通过Broker端的log.flush.interval.messages和log.flush.interval.ms来强制写盘，认为数据的可靠性应该通过Replica来保证，而强制Flush数据到磁盘会对整体性能产生影响。
2. 可以通过调整/proc/sys/vm/dirty_background_ratio和/proc/sys/vm/dirty_ratio来调优性能。
- a. 脏页率超过第一个指标会启动pdflush开始Flush Dirty PageCache。
- b. 脏页率超过第二个指标会阻塞所有的写操作来进行Flush。
- c. 根据不同的业务需求可以适当的降低dirty_background_ratio和提高dirty_ratio。

##### 正常关闭kafka
尽一切努力保证每次停Broker时都可以Clean Shutdown，否则问题就不仅仅是恢复服务所需时间长，还可能出现数据损坏或其他很诡异的问题。kafka和zk关闭的时候不要kill
##### Rebalance优化
低版本kafka，多个消费节点在zk注册临时节点产生冲突，另外zk同步数据延时性时间拉长远大于(rebalance.max.retries * rebalance.backoff.ms)，rebalance就会失败
如下配置会导致rebalance失败 
rebalance.max.retries * rebalance.backoff.ms < zookeeper.session.timeout.ms
#### 遇到的异常
1. 
```
org.apache.kafka.common.errors.TimeoutException: Expiring 2 record(s) for test-0 due to 30083 ms has passed since batch creation plus linger time
```
advertised.host.name和host.name该字段的值是生产者和消费者使用的。如果没有设置，则会取host.name的值，默认情况下，该值为localhost。思考一下，如果生产者拿到localhost这个值，只往本地发消息，必然会报错（因为本地没有kafka服务器）出现上面错误，是因为kafka的server.properties没有配置host.name=10.8.122.26;默认是null；每个节点都需要配置下，否则本地收不到kafka消息，也无法给kafka写消息
2. 
```
Configured brokerId 1 doesn't match stored brokerId 0 in meta.properties
kafka.common.InconsistentBrokerIdException: Configured brokerId 1 doesn't match stored brokerId 0 in meta.properties
        at kafka.server.KafkaServer.getBrokerId(KafkaServer.scala:630)
        at kafka.server.KafkaServer.startup(KafkaServer.scala:175)
        at kafka.server.KafkaServerStartable.startup(KafkaServerStartable.scala:37)
        at kafka.Kafka$.main(Kafka.scala:67)
        at kafka.Kafka.main(Kafka.scala)
```
错误的原因是log.dirs目录下的meta.properties中配置的broker.id和配置目录下的server.properties中的broker.id不一致了，解决问题的方法是将两者修改一致后再重启。
### KafkaProducer

<img src="http://yuml.me/diagram/scruffy/class/[note:消息的发送Master线程阶段流程{bg:wheat}],[note:Message{bg:wheat}]-[note:ProducerInterceptor{bg:wheat}],[note:ProducerInterceptor{bg:wheat}]-[note:ExtendedSerializer{bg:wheat}],[note:ExtendedSerializer{bg:wheat}]-[note:partition{bg:wheat}],[note:partition{bg:wheat}]-[note:Master线程把消息发往RecordAccumulator{bg:wheat}]" >

<img src="http://yuml.me/diagram/scruffy/class/[KafkaProducer|metadata:Metadata;accumulator:RecordAccumulator;keySerializer:ExtendedSerializer; valueSerializer:ExtendedSerializer;interceptors:ProducerInterceptors|send()doSend()]" >

> 我们主要关注`RecordAccumulator`,`Metadata`,`ExtendedSerializer`,`ProducerInterceptor`这几个类和`send()`方法。消息经过组装之后，调用`KafkaProducer#send(),KafkaProducer#doSend`,首先会调用拦截器去做过滤，也就是`ProducerInterceptor`类，这个类似于我们web开发的filter,实现其中onSend方法，可以在序列化消息中的key和value之前去修改消息，从而做到对特殊的消息进行增强,消息经过拦截器之后就直接调用了`doSend(interceptedRecord, callback)`.
```
  private Future<RecordMetadata> doSend(ProducerRecord<K, V> record, Callback callback) {
        TopicPartition tp = null;
        try {
            throwIfProducerClosed();
            ClusterAndWaitTime clusterAndWaitTime;
            try {
                clusterAndWaitTime = waitOnMetadata(record.topic(), record.partition(), maxBlockTimeMs);
            } catch (KafkaException e) {
                if (metadata.isClosed())
                    throw new KafkaException("Producer closed while send in progress", e);
                throw e;
            }
            long remainingWaitMs = Math.max(0, maxBlockTimeMs - clusterAndWaitTime.waitedOnMetadataMs);
            Cluster cluster = clusterAndWaitTime.cluster;
            byte[] serializedKey;
            try {
                serializedKey = keySerializer.serialize(record.topic(), record.headers(), record.key());
            } catch (ClassCastException cce) {
                throw new SerializationException("Can't convert key of class " + record.key().getClass().getName() +
                        " to class " + producerConfig.getClass(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG).getName() +
                        " specified in key.serializer", cce);
            }
            byte[] serializedValue;
            try {
                serializedValue = valueSerializer.serialize(record.topic(), record.headers(), record.value());
            } catch (ClassCastException cce) {
                throw new SerializationException("Can't convert value of class " + record.value().getClass().getName() +
                        " to class " + producerConfig.getClass(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG).getName() +
                        " specified in value.serializer", cce);
            }
            int partition = partition(record, serializedKey, serializedValue, cluster);
            tp = new TopicPartition(record.topic(), partition);

            setReadOnly(record.headers());
            Header[] headers = record.headers().toArray();

            int serializedSize = AbstractRecords.estimateSizeInBytesUpperBound(apiVersions.maxUsableProduceMagic(),
                    compressionType, serializedKey, serializedValue, headers);
            ensureValidRecordSize(serializedSize);
            long timestamp = record.timestamp() == null ? time.milliseconds() : record.timestamp();
            log.trace("Sending record {} with callback {} to topic {} partition {}", record, callback, record.topic(), partition);
            // producer callback will make sure to call both 'callback' and interceptor callback
            Callback interceptCallback = new InterceptorCallback<>(callback, this.interceptors, tp);

            if (transactionManager != null && transactionManager.isTransactional())
                transactionManager.maybeAddPartitionToTransaction(tp);

            RecordAccumulator.RecordAppendResult result = accumulator.append(tp, timestamp, serializedKey,
                    serializedValue, headers, interceptCallback, remainingWaitMs);
            if (result.batchIsFull || result.newBatchCreated) {
                log.trace("Waking up the sender since topic {} partition {} is either full or getting a new batch", record.topic(), partition);
                this.sender.wakeup();
            }
            return result.future;
        } catch (Exception e) {
           // ...省略异常抛出代码
        }
    }
```