package test1
import java.util.PriorityQueue

class MyQueue<K : Comparable<K>, T> {
    private val data = PriorityQueue(compareByDescending<Pair<K, T>> { it.first })
    fun enqueue(priority: K, element: T) = data.add(Pair(priority, element))
    fun peek(): T {
        if (data.isEmpty()) {
            error("Can't remove element from an empty queue!")
        }
        return data.peek().second
    }

    fun remove() {
        if (data.isEmpty()) {
            error("Can't remove element from an empty queue!")
        }
        data.remove()
    }

    fun rool(): T {
        if (data.isEmpty()) {
            error("Can't remove element from an empty queue!")
        }
        return data.remove().second
    }
}
