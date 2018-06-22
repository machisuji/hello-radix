package machisuji

import com.radixdlt.client.assets.Asset
import com.radixdlt.client.core._
import com.radixdlt.client.core.address._
import com.radixdlt.client.core.identity._
import com.radixdlt.client.messaging.RadixMessaging
import com.radixdlt.client.wallet.RadixWallet

class HelloRadix {
  val universe = RadixUniverse.bootstrap(Bootstrap.SUNSTONE)
  val identity = new SimpleRadixIdentity()
  val addr = RadixAddress.fromString("JGuwJVu7REeqQtx7736GB9AJ91z5xB55t8NvteaoC25AumYovjp") // radix faucet

  def msg = RadixMessaging.getInstance.sendMessage("Hello Faucet, this is me!", identity, addr)

  def myAddress = universe.getAddressFrom(identity.getPublicKey)

  def wallet: RadixWallet = RadixWallet.getInstance()
  def balance: Double = wallet
    .getXRDSubUnitBalance(myAddress)
    .map[Double](balance => balance.toLong / Asset.XRD.getSubUnits)
    .blockingFirst()

  def hello(): Unit = {
    universe.getNetwork.connectAndGetStatusUpdates().subscribe(e => println(s"STATUS: $e"))

    println("Address: " + myAddress)
    println("Balance: " + balance)
    println()

    println(s"Sending hello to $addr")
    msg.subscribe(e => println(s"MESSAGE: $e"))

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
