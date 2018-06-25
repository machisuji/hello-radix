import io.reactivex.Observable

package object machisuji {
  implicit class RichObservable[T](obs: Observable[T]) {
    def sub: Sub[T] = Sub(obs)
  }
}
