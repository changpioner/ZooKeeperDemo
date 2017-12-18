package nam.entrance

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener
import org.apache.curator.framework.state.ConnectionState

/**
  * Created by 42259 on 2017/12/18.
  *
  */
class MyLeaderListener(leaderName:String,leaderPath:String) extends LeaderSelectorListener{
  override def takeLeadership(curatorFramework: CuratorFramework): Unit = {
    println(Thread.currentThread().getName+" "+leaderName+" get the leader ...!")
    curatorFramework.setData().forPath(leaderPath,(Thread.currentThread().getName+"_"+leaderName).getBytes())
    Thread.sleep(15000L)
  }

  override def stateChanged(client: CuratorFramework, newState: ConnectionState): Unit = {
    println("newState " +newState)
  }
}
