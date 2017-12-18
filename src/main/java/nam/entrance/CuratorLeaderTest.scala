package nam.entrance

import nam.utils.ZkUtil
import org.apache.curator.framework.{CuratorFramework, CuratorFrameworkFactory}
import org.apache.curator.framework.recipes.leader.LeaderSelector
import org.apache.curator.retry.ExponentialBackoffRetry

/**
  * Created by 42259 on 2017/12/18.
  */
object CuratorLeaderTest {
  def main(args: Array[String]): Unit = {


    //register listener
    val leaders = Array("p0","p1","p2","p3","p4","p5")
    val client = ZkUtil.getZkClient
    leaders.par.foreach(
      x=> {
        val leaderPath = "/namhwik/leader"
        val leaderListener: MyLeaderListener = new MyLeaderListener(x,leaderPath)
        ZkUtil.addCandidateLeader(leaderListener,leaderPath)
      }
    )
    while (true) {

    }
  }
}
