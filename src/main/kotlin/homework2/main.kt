import homework2.AddAction
import homework2.MoveAction
import homework2.PerformedCommandStorage

fun main() {
    val l = (0..1).toMutableList()
    val log = PerformedCommandStorage(l)
    println(l)
    log.performStore(AddAction(1, 0))
    println(l)
    log.performStore(AddAction(-1, l.lastIndex))
    println(l)
    log.performStore(MoveAction(1, 0))
    println(l)

    log.toJSON("src/main/kotlin/homework2/ss.json")
    log.cancelAction()
    println(l)
    log.cancelAction()
    println(l)
    log.cancelAction()
    println(l)
}
