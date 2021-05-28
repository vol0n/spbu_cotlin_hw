package final

import java.lang.ArithmeticException

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

/**
 * Return sorted List from Iterable using given comparator
 *
 * @param Iterable<T> - iterable which elements will be used
 * @param comparator - comparator to use when comparing elements of [Iterable<T>]
 */
inline fun <T, R : Comparable<R>> Iterable<T>.bubbleSort(crossinline comparator: (T) -> R): List<T> {
    val result = mutableListOf<T>()
    this.forEach { result.add(it) }
    result.forEach { _ ->
        result.foldIndexed(result.first()) { index, prev, next ->
            try {
                if (index - 1 >= 0 && comparator(prev) > comparator(next)) {
                    result.swap(index - 1, index)
                }
            } catch (e: ArithmeticException) {
                println("Arithmetic exception!")
            }
            result[index]
        }
    }
    return result
}
