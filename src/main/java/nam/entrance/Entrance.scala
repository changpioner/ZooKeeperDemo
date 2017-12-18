package nam.entrance

import nam.utils.ZkUtil

/**
  * Created by 42259 on 2017/12/17.
  */
object Entrance {
  def main(args: Array[String]): Unit = {
   /* val creators = Array("creator1","creator2","creator3","creator4","creator5")
    creators.par.foreach(
      x=> {
        ZkUtil.createPath("/namhwik/lockTest",x,lock=true)
      }
    )
    ZkUtil.close()*/
    val client = ZkUtil.getZkClient
    client.start()
  }
}
