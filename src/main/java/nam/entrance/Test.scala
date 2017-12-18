package nam.entrance

import java.util.Date

import org.I0Itec.zkclient.ZkClient
import org.apache.curator.RetryPolicy
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode
import org.apache.curator.framework.recipes.cache._
import org.apache.curator.retry.{ExponentialBackoffRetry, RetryNTimes}
import org.apache.zookeeper.CreateMode
/**
  * Created by 42259 on 2017/12/13.
  */
object Test {
  def main(args: Array[String]): Unit = {

    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    val client: CuratorFramework = CuratorFrameworkFactory.newClient("47.94.131.111:2181", retryPolicy)
    client.start()
    val creater = new Thread(new NodeCreater(client,"/namhwik/watchtest002"))
    import scala.collection.JavaConversions._

      client.getChildren.forPath("/namhwik").toList.par.foreach(println(_))
    val pathChildrenWatcher  = new PathChildrenCache(client,"/namhwik",true)
    pathChildrenWatcher.getListenable.addListener(
      new PathChildrenCacheListener {
        override def childEvent(curatorFramework: CuratorFramework, pathChildrenCacheEvent: PathChildrenCacheEvent): Unit =
        {
          val data: ChildData = pathChildrenCacheEvent.getData
          if( data==null ) {
            println("No data in event[" + pathChildrenCacheEvent + "]")
          } else {
            println(new Date(System.currentTimeMillis()) +"Receive event: "
              + "type=[" + pathChildrenCacheEvent.getType + "]"
              + ", path=[" + data.getPath + "]"
              + ", data=[" + new String(data.getData) + "]"
              + ", stat=[" + data.getStat + "]"
            )
          }
        }
      }
    )
    val nodeCacheWatcher = new NodeCache(client,"/namhwik/node01")
    nodeCacheWatcher.getListenable.addListener(
      new NodeCacheListener {
        override def nodeChanged(): Unit = {
          println("changed data " +new String(nodeCacheWatcher.getCurrentData.getData))
          println("changed path " +nodeCacheWatcher.getCurrentData.getPath)
        }
      }
    )
    //nodeCacheWatcher.start(false)

    pathChildrenWatcher.start()
    println("nodeCacheWatcher started ...")

    creater.start()
    println("creater started ...")

    //client.create().creatingParentsIfNeeded().forPath("/namhwik/ ")
    while (true){

    }

    client.close()

  }
}
