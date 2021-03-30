package homework4.task2

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class TreeNodeTest {

    companion object {
        @JvmStatic
        fun inputDataTreeWalk() = listOf<Arguments>(
            Arguments.of(listOf<Int>(), 10, emptyList<Int>()),
            Arguments.of(listOf(20), 20, listOf(20)),
            Arguments.of(listOf(20, 10), 20, listOf(20, 10)),
            Arguments.of(listOf(20, 10), 10, listOf(10)),
            Arguments.of(listOf(10, 20, 30), 20, listOf(30, 20, 10)),
            )

        @JvmStatic
        private fun constructArgs(
            treeKeys: List<Int>,
            startNodeKey: Int,
            searchedNodeKey: Int,
            expected: Boolean
        ): Arguments {
            val tree = AVLTree<Int, String>()
            treeKeys.forEach { x -> tree.put(x, "") }
            val start = tree.entries.find { x -> x.key == startNodeKey } as TreeNode?
            val searched = tree.entries.find { x -> x.key == searchedNodeKey } as TreeNode?
            val exp = if (expected) searched else null
            return Arguments.of(start, searchedNodeKey, exp)
        }

        @JvmStatic
        fun inputDataFind() = listOf<Arguments>(
            constructArgs(listOf<Int>(20), 20, 20, true),
            constructArgs(listOf(20, 10, 30), 20, 30, true),
            constructArgs(listOf(20, 10, 30), 30, 20, false),
            constructArgs(listOf(20, 10, 30, 60, 25, 5, 22, 27), 20, 22, true),
            constructArgs(listOf(20, 10, 30, 60, 25, 5, 22, 27), 10, 22, false),
            constructArgs(listOf(20, 10, 30, 60, 25, 5, 22, 27), 30, 22, true)
        )

        @JvmStatic
        fun inputDataHeight() = listOf<Arguments>(
            Arguments.of(listOf<Int>(20), 1),
            Arguments.of(listOf(20, 10, 30), 2),
            Arguments.of(listOf(20, 10, 30, 60, 25, 5, 22, 27), 4)
        )
    }

    @MethodSource("inputDataTreeWalk")
    @ParameterizedTest
    fun inOrderTreeWalkTest(treeKeys: List<Int>, start: Int, expectedWalk: List<Int>){
        val actualWalk = mutableListOf<Int>()
        val tree = AVLTree<Int, String>()
        treeKeys.forEach { x -> tree.put(x, "") }
        tree.root?.find(start)?.inOrderTreeWalk { x -> actualWalk.add(x.key) }
        assertEquals(expectedWalk, actualWalk)
    }

    @MethodSource("inputDataHeight")
    @ParameterizedTest
    fun heightTest(treeKeys: List<Int>, expected: Int){
        val tree = AVLTree<Int, String>()
        treeKeys.forEach { x -> tree.put(x, "") }
        assertEquals(expected, tree.root?.height())
    }

    @MethodSource("inputDataFind")
    @ParameterizedTest
    fun findTest(
        start: TreeNode<Int, String>,
        searchedNodeKey: Int,
        expected: TreeNode<Int, String>?
    ) {
        assertEquals(expected, start.find(searchedNodeKey))
    }
}