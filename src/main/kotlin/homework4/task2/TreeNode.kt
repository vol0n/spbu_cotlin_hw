package homework4.task2

internal class TreeNode<K : Comparable<K>, V> (override val key: K, override var value: V) : Map.Entry<K, V> {
    var height = 1
    var right: TreeNode<K, V>? = null
    var left: TreeNode<K, V>? = null
    var p: TreeNode<K, V>? = null

    fun containsValue(v: V): Boolean {
        return if (v == value) true else (right?.containsValue(v) == true || left?.containsValue(v) == true)
    }
}
