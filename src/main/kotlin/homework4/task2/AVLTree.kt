package homework4.task2

class AVLTree<K : Comparable<K>, V> : Map<K, V> {
    internal var root: TreeNode<K, V>? = null

    companion object {
        private const val LEFTHEAVY = 2
        private const val RIGHTHEAVY = -2
    }

    internal fun rotateLeft(a: TreeNode<K, V>): TreeNode<K, V> {
        val secondNode = a.right
        if (secondNode == null) {
            println("rotateLeft failed: right child is null!")
            return a
        }
        val b: TreeNode<K, V> = secondNode

        a.right = b.left
        b.left?.p = a

        b.left = a

        if (a.p == null) {
            root = b
        } else if (a.p?.right == a) {
            a.p?.right = b
        } else {
            a.p?.left = b
        }

        b.p = a.p
        a.p = b

        a.fixHeight()
        b.fixHeight()
        return b
    }

    internal fun rotateRight(rotatingNode: TreeNode<K, V>): TreeNode<K, V> {
        val n = rotatingNode.left ?: error("rotateRight failed: right child is null!")
        val secondNode: TreeNode<K, V> = n

        rotatingNode.left = secondNode.right
        secondNode.right?.p = rotatingNode

        secondNode.right = rotatingNode

        when {
            rotatingNode.p == null -> root = secondNode
            rotatingNode.p?.right == rotatingNode -> rotatingNode.p?.right = secondNode
            else -> rotatingNode.p?.left = secondNode
        }

        secondNode.p = rotatingNode.p
        rotatingNode.p = secondNode

        rotatingNode.fixHeight()
        secondNode.fixHeight()
        return secondNode
    }

    fun put(key: K, value: V): V? {
        var curNode: TreeNode<K, V>? = root
        var parentNode: TreeNode<K, V>? = null
        var res: V? = null
        while (curNode != null) {
            parentNode = curNode
            when {
                curNode.key == key -> {
                    res = curNode.value
                    curNode.value = value
                    return res
                }
                curNode.key < key -> curNode = curNode.right
                else -> curNode = curNode.left
            }
        }

        val newNode = TreeNode(key, value)
        when {
            parentNode == null -> root = newNode
            parentNode.key > key -> parentNode.left = newNode
            else -> parentNode.right = newNode
        }
        newNode.p = parentNode

        balance(parentNode)
        size++
        return res
    }

    private fun transplant(u: TreeNode<K, V>, v: TreeNode<K, V>?) {
        when {
            u.p == null -> root = v
            u == u.p?.left -> u.p?.left = v
            else -> u.p?.right = v
        }
        if (v != null) { v.p = u.p }
    }

    fun remove(key: K): V? {
        val z = root?.find(key)
        if (z != null) size--
        else return null

        if (z.left == null) {
            transplant(z, z.right)
            balance(z.p)
        } else if (z.right == null) {
            transplant(z, z.left)
            balance(z.p)
        } else {
            val y = z.right?.treeMin()
            y ?: error("y was probably accessed in another thread!")
            if (y.p != z) {
                val tmp = y.right
                transplant(y, y.right)
                y.right = z.right
                y.right?.p = y
                transplant(z, y)
                y.left = z.left
                y.left?.p = y
                balance(tmp)
            } else {
                transplant(z, y)
                y.left = z.left
                y.left?.p = y
                balance(y)
            }
        }
        return z.value
    }

    private fun balance(x: TreeNode<K, V>?) {
        if (x == null) { return }

        x.fixHeight()

        var pn = x
        if (pn.balanceFactor() == RIGHTHEAVY) {
            val right = pn.right
            pn = if (right != null && right.balanceFactor() == 1) {
                rotateRight(right)
                rotateLeft(pn)
            } else {
                rotateLeft(pn)
            }
        } else if (pn.balanceFactor() == LEFTHEAVY) {
            val left = pn.left
            pn = if (left != null && left.balanceFactor() == -1) {
                rotateLeft(left)
                rotateRight(pn)
            } else {
                rotateRight(pn)
            }
        }

        if (pn.p != null) balance(pn.p)
    }

    override val entries: Set<Map.Entry<K, V>>
        get() = mutableSetOf<Map.Entry<K, V>>().apply {
            root?.inOrderTreeWalk { x -> this.add(x) }
        }

    override val keys: Set<K>
        get() = mutableSetOf<K>().apply {
            root?.inOrderTreeWalk { x -> this.add(x.key) }
        }

    override val values: Collection<V>
        get() = mutableListOf<V>().apply {
            root?.inOrderTreeWalk { x -> this.add(x.value) }
        }

    override fun containsKey(key: K): Boolean {
        return get(key) != null
    }

    override fun containsValue(value: V): Boolean {
        return root?.containsValue(value) == true
    }

    override fun get(key: K): V? = root?.find(key)?.value

    override fun isEmpty() = root == null

    override var size = 0

    fun clear() {
        root = null
    }

    internal fun getStructure(): Set<TestNode<K>> {
        return mutableSetOf<TestNode<K>>().apply {
            root?.inOrderTreeWalk { x -> this.add(TestNode(x.key, x.left?.key, x.right?.key)) }
        }
    }
}
