package homework4.task2

internal class TreeNode<K : Comparable<K>, V> (override val key: K, override var value: V) : Map.Entry<K, V> {
    var height = 1
    var right: TreeNode<K, V>? = null
    var left: TreeNode<K, V>? = null
    var p: TreeNode<K, V>? = null

    private fun height() = this.height
    internal fun balanceFactor() = (this.left?.height() ?: 0) - (this.right?.height() ?: 0)
    internal fun fixHeight() {
        this.height = Math.max(this.left?.height() ?: 0, this.right?.height() ?: 0) + 1
    }

    fun containsValue(v: V): Boolean {
        return if (v == value) true else (right?.containsValue(v) == true || left?.containsValue(v) == true)
    }

    internal fun find(key: K): TreeNode<K, V>? {
        var e: TreeNode<K, V>? = this
        var res: TreeNode<K, V>? = null
        while (e != null) {
            if (e.key == key) {
                res = e
                break
            } else if (e.key < key) {
                e = e.right
            } else { e = e.left }
        }
        return res
    }

    internal fun inOrderTreeWalk(doActionOnNode: (x: TreeNode<K, V>) -> Unit) {
        fun walk(node: TreeNode<K, V>?) {
            if (node != null) {
                walk(node.right)
                doActionOnNode(node)
                walk(node.left)
            }
        }

        walk(this)
    }

    internal fun treeMin(): TreeNode<K, V> {
        var curNode = this
        while (curNode.left != null)
            curNode = curNode.left as TreeNode
        return curNode
    }
}
