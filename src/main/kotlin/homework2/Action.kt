package homework2

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val module = SerializersModule {
    polymorphic(Action::class) {
        subclass(AddAction::class, AddAction.serializer())
        subclass(MoveAction::class, MoveAction.serializer())
    }
}

val format = Json { serializersModule = module
    prettyPrint = true }

/**
 * Base class for all Actions.
 *
 * Can be set only if action has not been performed yet
 */
@Serializable
@Suppress("UnnecessaryAbstractClass")
abstract class Action {
    abstract fun cancelAction(list: MutableList<Int>): Boolean
    abstract fun performAction(list: MutableList<Int>): Boolean
}

/**
 * Action adding Int at specified position in MutableList<Int>.
 *
 * @param elem the Int which is added to ls
 * @param pos the index for insertion
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
     * Returns true if [elem] was added
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
 * Action moving element from pos i in MutableList<Int> ls to position j.
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
     * @return true if successful, false if i or j are not valid idx and this method
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
     * Removes element at idx [j] and inserts it at idx [i] if possible.
     *
     * @return true if successful, false if i or j are not valid idx and this method
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
