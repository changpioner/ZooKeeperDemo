package nam.entrance

import java.util.concurrent.TimeUnit
import java.util.logging.{Level, Logger}

import org.apache.curator.framework.recipes.locks.InterProcessMutex
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode

/**
  * Created by 42259 on 2017/12/15.
  */
class NodeCreater2(path:String,data:String="defaultdata") extends Runnable{
  override def run(): Unit = {
    val retryPolicy = new ExponentialBackoffRetry(1000, 3)
    val client: CuratorFramework = CuratorFrameworkFactory.newClient("47.94.131.111:2181", retryPolicy)
    client.start()
    if(client.checkExists().forPath(path)==null) {
      val lock = new InterProcessMutex(client,path)
      if(lock.acquire(100000,TimeUnit.MILLISECONDS)) {
        val logger = Logger.getLogger("TEST")
        logger.log(Level.INFO,"data=>"+data)
        client.create()
          .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
          .forPath(path,data.getBytes("UTF-8"))
        client.setData().forPath(path,data.getBytes("UTF-8"))
        lock.release()
      }
    }
    client.close()
  }
}
