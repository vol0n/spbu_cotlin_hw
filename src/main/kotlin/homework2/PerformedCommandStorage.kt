package homework2

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.File

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
    fun performAll() = data.forEach() { x -> x.performAction(ls) }

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
        file.writeText(format.encodeToString(data))
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
