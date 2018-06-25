package machisuji

import io.reactivex.{Observable, Observer}
import io.reactivex.disposables.Disposable

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Future, Promise}

/**
  * As in subscription to an Observable.
  *
  * @param nextHandler Called for each new observation.
  * @param errorHandler Called in case of an error.
  * @param completeHandler Called once there will be no more new observations.
  * @tparam T
  */
class Sub[T] private(
  private val nextHandler:     T         => Unit = null,
  private val errorHandler:    Throwable => Unit = null,
  private val completeHandler: ()        => Unit = null,

  private var _sub: Disposable = null,
  private var _isComplete: Boolean = false,

  private val buffer: ArrayBuffer[T] = ArrayBuffer[T](),
  private var _error: Throwable = null,

  private val nextFilter: T => Boolean = null
) extends Observer[T] {
  def cancel(): Unit = {
    if (_sub ne null) {
      _sub.dispose()
    }
  }

  def next(handler: T => Unit): Sub[T] = {
    val sub = copy(nextHandler = handler)

    if (sub.buffer.nonEmpty && sub.hasNextHandler) {
      sub.buffer.foreach(sub.handleNext)
      sub.buffer.clear()
    }

    sub
  }

  def error(handler: Throwable => Unit): Sub[T] = {
    val sub = copy(errorHandler = handler)

    if (sub._error ne null) {
      sub.errorHandler(sub._error)
      sub._error = null
    }

    sub
  }

  def complete(handler: => Unit): Sub[T] = {
    val sub = copy(completeHandler = () => handler)

    if (sub._isComplete) {
      sub.completeHandler()
    }

    sub
  }

  def foreach(f: T => Unit): Sub[T] = next(f)
  def filter(p: T => Boolean): Sub[T] = copy(nextFilter = p)
  def filterNot(p: T => Boolean): Sub[T] = copy(nextFilter = e => !p(e))

  def first: Future[T] = {
    val p = Promise[T]()

    next(p.success).error(p.failure)

    p.future
  }

  def isComplete: Boolean = _isComplete

  override def onSubscribe(d: Disposable): Unit = _sub = d

  override def onNext(t: T): Unit = {
    if (hasNextHandler) {
      nextHandler(t)
    } else {
      buffer.append(t)
    }
  }

  override def onError(e: Throwable): Unit = {
    if (hasErrorHandler) {
      errorHandler(e)
    } else {
      _error = e
    }
  }

  override def onComplete(): Unit = {
    _isComplete = true

    if (hasCompleteHandler) {
      completeHandler()
    }
  }

  private def handleNext(next: T): Unit = {
    if (!hasNextFilter || nextFilter(next)) {
      nextHandler(next)
    }
  }

  def hasNextHandler: Boolean = nextHandler ne null
  def hasErrorHandler: Boolean = errorHandler ne null
  def hasCompleteHandler: Boolean = completeHandler ne null

  def hasNextFilter: Boolean = nextFilter ne null

  private def copy(
    nextHandler: T => Unit = null,
    errorHandler: Throwable => Unit = null,
    completeHandler: () => Unit = null,
    nextFilter: T => Boolean = null,
  ): Sub[T] = {
    new Sub(
      if (nextHandler ne null) nextHandler else this.nextHandler,
      if (errorHandler ne null) errorHandler else this.errorHandler,
      if (completeHandler ne null) completeHandler else this.completeHandler,
      _sub,
      _isComplete,
      buffer,
      _error,
      if (nextFilter ne null) nextFilter else this.nextFilter
    )
  }

  override def toString: String = s"Sub(next: $nextHandler, error: $errorHandler, complete: $completeHandler, buffer: [${buffer.mkString(", ")}])"
}

object Sub {
  def apply[T](observable: Observable[T]): Sub[T] = {
    val sub: Sub[T] = new Sub[T]()

    observable.subscribe(sub)

    sub
  }

  def next[T](handler: T => Unit): Sub[T] = new Sub(nextHandler = handler)
  def error[T](handler: Throwable => Unit): Sub[T] = new Sub(errorHandler = handler)
  def complete[T](handler: () => Unit): Sub[T] = new Sub(completeHandler = handler)

  private def buffer[T](sub: Sub[T])(e: T): Unit = sub.buffer.append(e)
}