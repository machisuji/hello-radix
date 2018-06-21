package machisuji

import com.radixdlt.client.core._
import com.radixdlt.client.core.address._
import com.radixdlt.client.core.identity._
import com.radixdlt.client.messaging.RadixMessaging

class HelloRadix {
  val universe = RadixUniverse.bootstrap(Bootstrap.SUNSTONE)
  val identity = new SimpleRadixIdentity()
  val addr = RadixAddress.fromString("JGuwJVu7REeqQtx7736GB9AJ91z5xB55t8NvteaoC25AumYovjp") // radix faucet

  def msg = RadixMessaging.getInstance.sendMessage("Hello Faucet, this is Markus!", identity, addr)

  def hello(): Unit = {
    println("Sending hello to " + addr)
    msg.subscribe(s => println("RadixMessaging: " + s))

    msg.doFinally(() => {
      println("Message sent. Shutting down.")
      universe.getNetwork.close()
      universe.disconnect()
      println("Bye!")
    })
  }
}

object HelloRadix extends App {
  val hr = new HelloRadix

  hr.hello()
}
