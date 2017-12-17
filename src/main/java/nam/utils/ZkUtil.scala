package nam.utils

import java.util.concurrent.TimeUnit
import java.util.logging.{Level, Logger}

import org.apache.curator.framework.recipes.cache.{ChildData, PathChildrenCache, PathChildrenCacheEvent, PathChildrenCacheListener}
import org.apache.curator.framework.recipes.locks.InterProcessMutex
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode

/**
  * Created by 42259 on 2017/12/17.
  */
object ZkUtil {
  val retryPolicy = new ExponentialBackoffRetry(1000, 3)
  val client: CuratorFramework = CuratorFrameworkFactory.newClient("47.94.131.111:2181", retryPolicy)
  client.start()
  def createPath(path:String,data:String): Unit = {
    createPath(path, data,lock = false)
  }
  def createPath(path:String,data:String,lock:Boolean): Unit =  {
//    if(checkExists(path))
//      throw new RuntimeException("The path already exist !")
//    else {
//      if(lock) createWithLock(path,data) else client.create().creatingParentsIfNeeded().forPath(path,data.getBytes("UTF-8"))
//    }
    if(lock) createWithLock(path,data) else client.create().creatingParentsIfNeeded().forPath(path,data.getBytes("UTF-8"))

  }
  var test :String= _
  private def createWithLock(path:String,data:String) :Unit= synchronized {
    if (!checkExists(path)) {
      val logger = Logger.getLogger("TEST")
      val lock = new InterProcessMutex(client, path)
      if (lock.acquire(300000, TimeUnit.MILLISECONDS)) {
        logger.log(Level.INFO, "data=>" + data)
        client.create().orSetData().withMode(CreateMode.PERSISTENT).forPath(path, data.getBytes("UTF-8"))
        lock.release()
      }
    }
  }
  def getZkClient: CuratorFramework = {
    client
  }

  def getChildren(path:String): List[String] = {
    import scala.collection.JavaConversions._
    client.getChildren.forPath(path).toList
  }
  def checkExists(path:String):Boolean ={
    client.checkExists().forPath(path)!=null
  }
  def addWatchListener(path:String,dataFunc:(ChildData)=>Any): Unit = {
    val pathChildrenWatcher  = new PathChildrenCache(client,"/namhwik",true)
    pathChildrenWatcher.getListenable.addListener(
      new PathChildrenCacheListener {
        override def childEvent(curatorFramework: CuratorFramework, pathChildrenCacheEvent: PathChildrenCacheEvent): Unit =
        {
          val data: ChildData = pathChildrenCacheEvent.getData
          if( data==null ) {
            println("No data in event[" + pathChildrenCacheEvent + "]")
          } else {
            println("Receive event: "
              + "type=[" + pathChildrenCacheEvent.getType + "]"
              + ", path=[" + data.getPath + "]"
              + ", data=[" + new String(data.getData) + "]"
              + ", stat=[" + data.getStat + "]"
            )
            dataFunc(data)
          }
        }
      }
    )
  }
  def close(): Unit = {
    client.close()
  }
  val f1: (String) => String = (path:String)=> {
    path
  }
}
