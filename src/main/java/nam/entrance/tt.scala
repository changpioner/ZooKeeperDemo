package nam.entrance

import nam.utils.ZkUtil
import org.apache.zookeeper.data.Stat

/**
  * Created by 42259 on 2017/12/17.
  */
object tt {
  def main(args: Array[String]): Unit = {
    import scala.collection.JavaConversions._
   ZkUtil.getZkClient.create().forPath("/hhh","sadasd".getBytes())

  }
}
