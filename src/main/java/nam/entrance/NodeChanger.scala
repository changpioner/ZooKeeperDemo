package nam.entrance

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.locks.{InterProcessLock, InterProcessMutex}

/**
  * Created by 42259 on 2017/12/15.
  */
class NodeChanger(client:CuratorFramework,path:String,data:String) extends Runnable{
  override def run(): Unit = {
    val lock = new InterProcessMutex(client,path)
    client.setData().forPath(path,data.getBytes())
  }
}
