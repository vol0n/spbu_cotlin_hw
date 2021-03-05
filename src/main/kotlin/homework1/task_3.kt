package homework1

interface Action {
    fun cancelAction(ls: MutableList<Int>)
}

class AddFrontAction(elem: Int, ls: MutableList<Int>) : Action {
    init {
        ls.add(0, elem)
    }
    override fun cancelAction(ls: MutableList<Int>) {
        ls.removeFirst()
    }
}

class AddBackAction(elem: Int, ls: MutableList<Int>) : Action {
    init { ls.add(elem) }
    override fun cancelAction(ls: MutableList<Int>) {
        ls.removeLast()
    }
}

class MoveAction(private val i: Int, private val j: Int, ls: MutableList<Int>) : Action {
    init {
        val validRange = ls.indices
        if (i !in validRange || j !in validRange) {
            println("Incorrect indexes for SwapAction: $i, $j. Available range: $validRange")
        } else {
            ls.add(j, ls.removeAt(i))
        }
    }
    override fun cancelAction(ls: MutableList<Int>) {
        ls.add(i, ls.removeAt(j))
    }
}

class PerformedCommandStorage(private val ls: MutableList<Int>) {
    private val data = mutableListOf<Action>()
    fun storeAction(act: Action) = data.add(act)
    fun cancelAction() {
        if (data.isEmpty()) println("No actions in the storage, nothing to cancel")
        else data.removeAt(data.lastIndex).cancelAction(ls)
    }
}

fun main() {
    val l = (0..1).toMutableList()
    val log = PerformedCommandStorage(l)
    println(l)
    log.storeAction(AddBackAction(1, l))
    println(l)
    log.storeAction(AddFrontAction(-1, l))
    println(l)
    log.storeAction(MoveAction(0, l.lastIndex, l))
    println(l)
    log.cancelAction()
    println(l)
    log.cancelAction()
    println(l)
    log.cancelAction()
    println(l)
}
