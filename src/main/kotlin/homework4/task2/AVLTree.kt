package homework4.task2

import java.lang.Math.max

class AVLTree<K : Comparable<K>, V> : Map<K, V> {
    private var root: TreeNode<K, V>? = null

    private fun height(x: TreeNode<K, V>?) = x?.height ?: 0
    private fun bFactor(x: TreeNode<K, V>) = height(x.left) - height(x.right)
    private fun fixHeight(x: TreeNode<K, V>) {
        x.height = max(height(x.right), height(x.left)) + 1
    }

    internal fun rotateLeft(a: TreeNode<K, V>): TreeNode<K, V> {
        val b = a.right!!

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

        fixHeight(a)
        fixHeight(b)
        return b
    }

    internal fun rotateRight(a: TreeNode<K, V>): TreeNode<K, V> {
        val b = a.left as TreeNode

        a.left = b.right
        b.right?.p = a

        b.right = a

        if (a.p == null) {
            root = b
        } else if (a.p?.right == a) {
            a.p?.right = b
        } else {
            a.p?.left = b
        }

        b.p = a.p
        a.p = b

        fixHeight(a)
        fixHeight(b)
        return b
    }

    private fun bigRotateRight(x: TreeNode<K, V>): TreeNode<K, V> {
        rotateLeft(x.left!!)
        return rotateRight(x)
    }

    private fun bigRotateLeft(x: TreeNode<K, V>): TreeNode<K, V> {
        rotateRight(x.right!!)
        return rotateLeft(x)
    }

    fun put(key: K, value: V): V? {
        var curNode: TreeNode<K, V>? = root
        var parentNode: TreeNode<K, V>? = null
        var res: V? = null
        while (curNode != null) {
            parentNode = curNode
            if (curNode.key == key) {
                res = curNode.value
                curNode.value = value
                return res
            } else if (curNode.key < key) {
                curNode = curNode.right
            } else {
                curNode = curNode.left
            }
        }

        val newNode = TreeNode(key, value)
        if (parentNode == null) {
            root = newNode
        } else if (parentNode.key > key) {
            parentNode.left = newNode
            newNode.p = parentNode
        } else {
            parentNode.right = newNode
            newNode.p = parentNode
        }

        balance(parentNode)
        size++
        return res
    }

    private fun transplant(u: TreeNode<K, V>, v: TreeNode<K, V>?) {
        if (u.p == null) {
            root = v
        } else if (u == u.p?.left) {
            u.p?.left = v
        } else { u.p?.right = v }
        if (v != null) { v.p = u.p }
    }

    fun remove(key: K): V? {
        val z = find(key)
        if (z != null) size--
        else return null

        if (z.left == null) {
            transplant(z, z.right)
            balance(z.p)
        } else if (z.right == null) {
            transplant(z, z.left)
            balance(z.p)
        } else {
            val y = treeMin(z.right as TreeNode<K, V>)
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

    private fun treeMin(start: TreeNode<K, V>): TreeNode<K, V> {
        var curNode = start
        while (curNode.left != null)
            curNode = curNode.left as TreeNode
        return curNode
    }

    @Suppress("MagicNumber")
    private fun balance(x: TreeNode<K, V>?) {
        if (x == null) { return }

        fixHeight(x)

        var pn = x
        if (bFactor(pn) == -2) {
            if (pn.right != null && bFactor(pn.right!!) == 1) {
                pn = bigRotateLeft(pn)
            } else { pn = rotateLeft(pn) }
        } else if (bFactor(pn) == 2) {
            if (pn.left != null && bFactor(pn.left!!) == -1) {
                pn = bigRotateRight(pn)
            } else {
                pn = rotateRight(pn)
            }
        }

        if (pn.p != null) balance(pn.p)
    }

    override val entries: Set<Map.Entry<K, V>>
        get() = mutableSetOf<Map.Entry<K, V>>().apply {
            inOrderTreeWalk { x -> this.add(x) }
        }

    override val keys: Set<K>
        get() = mutableSetOf<K>().apply {
            inOrderTreeWalk { x -> this.add(x.key) }
        }

    override val values: Collection<V>
        get() = mutableListOf<V>().apply {
            inOrderTreeWalk { x -> this.add(x.value) }
        }

    override fun containsKey(key: K): Boolean {
        return get(key) != null
    }

    override fun containsValue(value: V): Boolean {
        return root?.containsValue(value) == true
    }

    private fun inOrderTreeWalk(f: (x: TreeNode<K, V>) -> Unit) {
        fun walk(t: TreeNode<K, V>?) {
            if (t != null) {
                walk(t.right)
                f(t)
                walk(t.left)
            }
        }

        walk(root)
    }

    internal fun find(key: K): TreeNode<K, V>? {
        var e: TreeNode<K, V>? = root
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

    override fun get(key: K): V? {
        return find(key)?.value
    }

    override fun isEmpty() = root == null

    override var size = 0

    fun clear() {
        root = null
    }

    internal fun printDebug() {
        fun temp(x: TreeNode<K, V>?) {
            val v = x
            if (v != null) {
                println(
                    "${x.key} (${x.height}): ${x.left?.key} (${x.left?.height})," +
                            "${x.right?.key} (${x.right?.height})"
                )
                temp(x.left)
                temp(x.right)
            }
        }
        temp(root)
    }

    internal fun getStructure(): Set<TestNode<K>> {
        return mutableSetOf<TestNode<K>>().apply {
            inOrderTreeWalk { x -> this.add(TestNode(x.key, x.left?.key, x.right?.key)) }
        }
    }
}
