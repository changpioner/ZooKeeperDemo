package nam.entrance
import org.I0Itec.zkclient.ZkClient
import org.apache.curator.RetryPolicy
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.{ExponentialBackoffRetry, RetryNTimes}
import org.apache.zookeeper.CreateMode
/**
  * Created by 42259 on 2017/12/13.
  */
object Test {
  def main(args: Array[String]): Unit = {

    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    val client = CuratorFrameworkFactory.newClient("47.94.131.111:2181", retryPolicy)
    client.start()
    client.create().creatingParentsIfNeeded().forPath("/namhwik/test1/test11/test111")
    import scala.collection.JavaConversions._

    client.getChildren.forPath("/namhwik").toList.foreach(println(_))
    client.close()

  }
}
