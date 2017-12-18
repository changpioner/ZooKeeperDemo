package nam.entrance

import java.util.Date

import nam.utils.ZkUtil
import org.apache.curator.framework.recipes.cache.{ChildData, PathChildrenCacheEvent}
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.zookeeper.data.Stat

/**
  * Created by 42259 on 2017/12/17.
  */
object CuratorListenerTest {
  def main(args: Array[String]): Unit = {
    val listener = (pathChildrenCacheEvent :PathChildrenCacheEvent)=> {
      val childData = pathChildrenCacheEvent.getData
      println(new Date(System.currentTimeMillis()) +"...Receive event: "
        + "type=[" + pathChildrenCacheEvent.getType + "]"
        + ", path=[" + childData.getPath + "]"
        + ", data=[" + new String(childData.getData) + "]"
        + ", stat=[" + childData.getStat + "]"
      )
    }
    ZkUtil.addWatchListener("/namhwik",listener)
    while (true) {

    }
  }
}
