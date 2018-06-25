package machisuji

import com.radixdlt.client.assets.Asset
import com.radixdlt.client.core._
import com.radixdlt.client.core.address._
import com.radixdlt.client.core.identity._
import com.radixdlt.client.core.network.AtomSubmissionUpdate
import com.radixdlt.client.messaging.RadixMessaging
import com.radixdlt.client.wallet.RadixWallet
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Await, CanAwait, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

class HelloRadix {
  val universe = RadixUniverse.bootstrap(Bootstrap.SUNSTONE)
  val identity = new SimpleRadixIdentity()

  val faucetAddress = RadixAddress.fromString("JGuwJVu7REeqQtx7736GB9AJ91z5xB55t8NvteaoC25AumYovjp") // radix faucet
  val myAddress = universe.getAddressFrom(identity.getPublicKey)
  val walletAddress = address("9h4D9StAiFzLjauWerybbVN14P3eL4yp4C2KTxtHpxXGQC11mLm") // my linux wallet address

  def address(identity: RadixIdentity): RadixAddress = universe.getAddressFrom(identity.getPublicKey)
  def address(addr: String): RadixAddress = RadixAddress.fromString(addr)

  def wallet: RadixWallet = RadixWallet.getInstance()

  def balance: Double = Await.result(balance(myAddress).sub.filter(_ != 0).first, Duration.apply("5s"))

  def balance(addr: RadixAddress): Observable[Double] = wallet
    .getXRDSubUnitBalance(addr)
    .map[Double](balance => balance.toDouble / Asset.XRD.getSubUnits)

  def message(msg: String, from: RadixIdentity, to: RadixAddress): Sub[AtomSubmissionUpdate] = {
    def send: Observable[AtomSubmissionUpdate] = RadixMessaging.getInstance().sendMessage(msg, from, to)

    val sub: Sub[AtomSubmissionUpdate] = Sub
      .next { update: AtomSubmissionUpdate =>
        import AtomSubmissionUpdate.AtomSubmissionState._

        update.getState match {
          case SUBMITTING => println(s"""Submitting message "$msg" to $to from ${address(from)}""")
          case SUBMITTED => println("Message submitted")
          case STORED => println("Message stored")
          case state => println("Unexpected state: " + state)
        }
      }
      .error { e =>
        println("Failed to send message: " + e)
      }

    send.subscribe(sub)

    sub
  }

  def disconnect(): Unit = {
    RadixUniverse.getInstance.disconnect()
  }

  def hello(): Unit = {
    println()
    println("Address: " + myAddress)
    println()

    balance(myAddress).sub.filter(_ != 0).foreach { balance => println("MY BALANCE IS " + balance) }

    val addr = walletAddress

    println(s"Sending hello to $addr")

    def quit(): Unit = {
      println("Message sent. Shutting down.")
      disconnect()
      println("Bye!")

      System.exit(0)
    }

    message("Hello Radix", identity, addr).complete(quit)
  }
}

object HelloRadix extends App {
  val hr = new HelloRadix

  hr.hello()
}
