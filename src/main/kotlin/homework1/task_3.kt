package homework1

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.io.File

val module = SerializersModule {
    polymorphic(Action::class) {
        subclass(AddAction::class)
        subclass(MoveAction::class)
    }
}

val format = Json { serializersModule = module }

/**
 * Base class for all Actions.
 *
 * Can be set only if action has not been performed yet.
 */
@Serializable
@Suppress("UnnecessaryAbstractClass")
abstract class Action {
    abstract fun cancelAction(list: MutableList<Int>): Boolean
    abstract fun performAction(list: MutableList<Int>): Boolean
}

/**
 * Action adding Int at specified position in MutableList<T>.
 *
 * @param elem the Int which is added to ls.
 * @param pos the index for insertion.
 */
@Serializable
open class AddAction(private val elem: Int, private val pos: Int) : Action() {
    private fun checkParams(list: MutableList<Int>): Boolean {
        return (pos in 0..list.lastIndex)
    }

    /**
     * Adds [elem] at [pos] in [list] if possible.
     *
     * @return false if [pos] is not a valid index for [list] and no insertion happened.
     * Returns true if [elem] was added.
     */
    override fun performAction(list: MutableList<Int>): Boolean {
        if (checkParams(list)) {
            list.add(pos, elem)
            return true
        }
        println("Index out of range for adding element in AddAction: $this")
        return false
    }

    /**
     * Removes [elem] at [pos] in [list] if possible.
     *
     * @return false if [pos] is not a valid index for [list] and no insertion happened.
     * Returns true if [elem] was added.
     */
    override fun cancelAction(list: MutableList<Int>): Boolean {
        if (checkParams(list)) {
            list.removeAt(pos)
            return true
        }
        println("Index out of range for canceling AddAction: $this")
        return false
    }
}

/**
 * Action moving element from pos i in MutableList<T> ls to position j.
 *
 * @param i the index of element to move. 0 <= i < ls.length.
 * @param j the index where to move. 0 <= j < ls.length.
 */
@Serializable
class MoveAction(private val i: Int, private val j: Int) : Action() {
    private fun checkParams(list: MutableList<Int>): Boolean {
        val validRange = 0..list.lastIndex
        return (i in validRange && j in validRange)
    }

    /**
     * Removes element at idx [i] and inserts it at idx [j] if possible.
     *
     * @return true if successful, false if [i] or [j] are not valid indexes and this method
     * did not change ls.
     */
    override fun performAction(list: MutableList<Int>): Boolean {
        if (checkParams(list)) {
            list.add(j, list.removeAt(i))
            return true
        }
        println("Index out of range for performing MoveAction: $this")
        return false
    }

    /**
     * Removes element at index [j] and inserts it at index [i] if possible.
     *
     * @return true if successful, false if [i] or [j] are not valid idx and this method
     * did not change ls.
     */
    override fun cancelAction(list: MutableList<Int>): Boolean {
        if (checkParams(list)) {
            list.add(i, list.removeAt(j))
            return true
        }
        println("Index out of range for MoveAction: $this")
        return false
    }
}

/**
 * Stores instances of type Action<Int>.
 *
 * @param ls the list on which to perform and cancel actions.
 */
class PerformedCommandStorage(private val ls: MutableList<Int>) {
    var data = mutableListOf<Action>()

    /**
     * Puts [act] in storage.
     */
    fun store(act: Action) = data.add(act)
    fun performStore(act: Action) = data.add(act.apply { this.performAction(ls) })

    /**
     * Cancels the last action on [ls].
     */
    fun cancelAction() {
        if (data.isEmpty()) println("No actions in the storage, nothing to cancel")
        else data.removeAt(data.lastIndex).cancelAction(ls)
    }

    /**
     * Saves json representation of [data] to file specified by [path].
     */
    fun toJSON(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.writeText(format.encodeToString(data))
        } else {
            println("Could not open the file: $path")
        }
    }

    /**
     * Fills [data] with Actions from json file specified by [path].
     */
    fun readJSON(path: String) {
        val file = File(path)
        if (file.exists()) {
            data = format.decodeFromString(file.readText())
        } else {
            println("Could not open the file: $path")
        }
    }
}

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
    log.cancelAction()
    println(l)
    log.cancelAction()
    println(l)
    log.cancelAction()
    println(l)
}
