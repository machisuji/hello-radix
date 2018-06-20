package machisuji

import com.radixdlt.client.core._
import com.radixdlt.client.core.address._
import com.radixdlt.client.core.identity._
import com.radixdlt.client.messaging.RadixMessaging

object HelloRadix extends App {
  val universe = RadixUniverse.bootstrap(Bootstrap.SUNSTONE)
  val identity = new SimpleRadixIdentity()
  val addr = RadixAddress.fromString("JGuwJVu7REeqQtx7736GB9AJ91z5xB55t8NvteaoC25AumYovjp") // radix faucet
  val msg = RadixMessaging.getInstance.sendMessage("Hello Faucet, this is Markus!", identity, addr)

  msg.subscribe(s => println("RadixMessaging: " + s))
}
