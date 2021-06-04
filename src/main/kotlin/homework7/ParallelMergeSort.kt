package homework7

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

enum class ResourceKind(val kind: String) {
    THREADS("Threads"), COROUTINES("Coroutines")
}

interface Sorter {
    suspend fun sort(sourceArray: IntArray, numberOfResources: Int): IntArray
    val resourcesKind: ResourceKind
}

object CoroutinesMergeSort : Sorter {
    private data class SubArray(val left: Int, val right: Int) {
        val size = right - left + 1
        val middle = (right + left) / 2
    }

    override val resourcesKind: ResourceKind = ResourceKind.COROUTINES

    override suspend fun sort(
        sourceArray: IntArray,
        numberOfResources: Int,
    ): IntArray {
        if (numberOfResources < 0) {
            error(
                """
                    Invalid number of threads: $numberOfResources. 
                    Number of coroutines must be >= 0.
                """
            )
        }

        sort(sourceArray, SubArray(0, sourceArray.lastIndex), sourceArray, 0, numberOfResources)
        return sourceArray
    }

    // for detekt to calm down on parameter list
    private data class ArraysForMerge(val from: IntArray, val to: IntArray)

    private fun lowerBound(value: Int, array: IntArray, sa: SubArray): Int {
        if (sa.left > sa.right) return sa.left
        var leftBorder = sa.left - 1
        var rightBorder = sa.right + 1
        var middleIndex: Int
        // array[leftBorder] < value <= array[rightBorder]
        while (leftBorder + 1 < rightBorder) {
            middleIndex = (leftBorder + rightBorder) / 2
            if (array[middleIndex] < value) {
                leftBorder = middleIndex
            } else {
                rightBorder = middleIndex
            }
        }
        return rightBorder
    }

    // sort sourceArray[l, r] and save into destArray[s, s + r - l], s - starting index in destination array
    private suspend fun sort(
        initialArray: IntArray,
        sourceArray: SubArray,
        destArray: IntArray,
        startIndexInDestArray: Int,
        numberOfCoroutines: Int = 0
    ) {
        if (initialArray.isEmpty()) return
        if (sourceArray.size == 1) {
            destArray[startIndexInDestArray] = initialArray[sourceArray.left]
            return
        }
        val tempArray = IntArray(sourceArray.size)
        // number of elements in the right part of subArray sa
        val numOfElemsInRightPartOfSourceArray = sourceArray.middle - sourceArray.left + 1
        if (numberOfCoroutines > 0) {
            val job = coroutineScope {
                launch {
                    sort(
                        initialArray, SubArray(sourceArray.left, sourceArray.middle), tempArray, 0,
                        numberOfCoroutines / 2
                    )
                }
            }
            sort(initialArray, SubArray(sourceArray.middle + 1, sourceArray.right), tempArray,
                numOfElemsInRightPartOfSourceArray,
                numberOfCoroutines - numberOfCoroutines / 2)
            job.join()
        } else {
            sort(initialArray, SubArray(sourceArray.left, sourceArray.middle), tempArray, 0)
            sort(initialArray, SubArray(sourceArray.middle + 1, sourceArray.right), tempArray,
                numOfElemsInRightPartOfSourceArray)
        }

        merge(
            ArraysForMerge(tempArray, destArray),
            SubArray(0, numOfElemsInRightPartOfSourceArray - 1),
            SubArray(numOfElemsInRightPartOfSourceArray, sourceArray.size - 1),
            startIndexInDestArray,
            numberOfCoroutines
        )
    }

    // merge two sorted sourceArray[l1, r1] and sourceArray[l2, r2] into destArray[s, s + r1 - l1 + r2 - l2 + 1]
    private suspend fun merge(
        arraysForMerge: ArraysForMerge,
        sourceArray1: SubArray,
        sourceArray2: SubArray,
        startIndexInDestArray: Int,
        numberOfCoroutines: Int = 0
    ) {
        if (sourceArray1.size < sourceArray2.size) {
            merge(arraysForMerge, sourceArray2, sourceArray1, startIndexInDestArray, numberOfCoroutines)
            return
        }
        if (sourceArray1.size == 0) return

        val mid1 = sourceArray1.middle
        val mid2 = lowerBound(arraysForMerge.from[mid1], arraysForMerge.from, sourceArray2)
        val mid3 = startIndexInDestArray + (mid1 - sourceArray1.left) + mid2 - sourceArray2.left
        arraysForMerge.to[mid3] = arraysForMerge.from[mid1]
        if (numberOfCoroutines > 0) {
            val job = coroutineScope {
                launch {
                    merge(
                        arraysForMerge,
                        SubArray(sourceArray1.left, mid1 - 1),
                        SubArray(sourceArray2.left, mid2 - 1),
                        startIndexInDestArray,
                        numberOfCoroutines / 2
                    )
                }
            }
            merge(
                arraysForMerge,
                SubArray(mid1 + 1, sourceArray1.right),
                SubArray(mid2, sourceArray2.right),
                mid3 + 1,
                numberOfCoroutines - numberOfCoroutines / 2
            )
            job.join()
        } else {
            merge(
                arraysForMerge,
                SubArray(sourceArray1.left, mid1 - 1),
                SubArray(sourceArray2.left, mid2 - 1),
                startIndexInDestArray
            )
            merge(
                arraysForMerge,
                SubArray(mid1 + 1, sourceArray1.right),
                SubArray(mid2, sourceArray2.right),
                mid3 + 1
            )
        }
    }
}
