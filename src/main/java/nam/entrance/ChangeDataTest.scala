package nam.entrance

import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry

/**
  * Created by 42259 on 2017/12/15.
  */
object ChangeDataTest {
  def main(args: Array[String]): Unit = {
    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    val client: CuratorFramework = CuratorFrameworkFactory.newClient("47.94.131.111:2181", retryPolicy)
    client.start()
    val creater1: NodeCreater2 = new NodeCreater2("/namhwik/aaa","changer1created")
    val creater2: NodeCreater2 = new NodeCreater2("/namhwik/aaa","changer2created")
    val creater3: NodeCreater2 = new NodeCreater2("/namhwik/aaa","changer3created")
    val creater4: NodeCreater2 = new NodeCreater2("/namhwik/aaa","changer4created")
    val thread1: Thread = new Thread(creater1)
    val thread2 = new Thread(creater2)
    val thread3 = new Thread(creater3)
    val thread4 = new Thread(creater4)

    val chaArr: Array[Thread] = Array(thread1,thread2,thread3,thread4)
    chaArr.foreach(_.start())
    var state = client.checkExists().forPath("/namhwik/aaa")
    while (state==null) {
      state=client.checkExists().forPath("/namhwik/aaa")
    }
    println(state)
    client.close()
  }
}
