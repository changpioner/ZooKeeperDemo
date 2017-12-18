package nam.utils

import java.util.concurrent.TimeUnit
import java.util.logging.{Level, Logger}

import org.apache.curator.framework.recipes.cache.{ChildData, PathChildrenCache, PathChildrenCacheEvent, PathChildrenCacheListener}
import org.apache.curator.framework.recipes.leader.{LeaderSelector, LeaderSelectorListener}
import org.apache.curator.framework.recipes.locks.InterProcessMutex
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.CreateMode

/**
  * Created by 42259 on 2017/12/17.
  */
object ZkUtil {
  private val retryPolicy = new ExponentialBackoffRetry(1000, 3)
  private val client: CuratorFramework = CuratorFrameworkFactory.newClient("47.94.131.111:2181", retryPolicy)
  client.start()
  def createPath(path:String,data:String): Unit = {
    createPath(path, data,lock = false)
  }
  def createPath(path:String,data:String,lock:Boolean): Unit =  {
    if(lock) createWithLock(path,data) else client.create().creatingParentsIfNeeded().forPath(path,data.getBytes("UTF-8"))

  }
  private def createWithLock(path:String,data:String) :Unit = {
      val logger = Logger.getLogger("TEST")
      val pathArr = path.split("/")
      val lock = new InterProcessMutex(client, pathArr.slice(0,pathArr.length-1).mkString("/"))
      if (lock.acquire(3000, TimeUnit.MILLISECONDS)) {
        println(data +"started ...")
        if (!checkExists(path)) {
          logger.log(Level.INFO, "data=>" + data)
          client.create()
          .orSetData().withMode(CreateMode.PERSISTENT)
          .forPath(path, data.getBytes("UTF-8"))
          }
        lock.release()
      }
    println(data +"finished ...")
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
  def addWatchListener(path:String,eventFunc:(PathChildrenCacheEvent)=>Any): Unit = {
    val pathChildrenWatcher  = new PathChildrenCache(client,path,true)
    pathChildrenWatcher.getListenable.addListener(
      new PathChildrenCacheListener {
        override def childEvent(curatorFramework: CuratorFramework, pathChildrenCacheEvent: PathChildrenCacheEvent): Unit =
        {
          val data: ChildData = pathChildrenCacheEvent.getData
          if( data==null ) {
            println("No data in event[" + pathChildrenCacheEvent + "]")
          } else {
            eventFunc(pathChildrenCacheEvent)
          }
        }
      }
    )
    pathChildrenWatcher.start()
  }

  def addCandidateLeader(candidateLeader:LeaderSelectorListener,leaderPath:String,autoRequeue:Boolean=false): Unit = {
    val leaderSelector = new LeaderSelector(client,leaderPath,candidateLeader)
    if(autoRequeue)
      leaderSelector.autoRequeue()
    leaderSelector.start()
  }

  def close(): Unit = {
    client.close()
  }

}
