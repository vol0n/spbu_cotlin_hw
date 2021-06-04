package homework6

enum class ResourceKind(val kind: String) {
    THREADS("Threads"), COROUTINES("Coroutines")
}

interface Sorter {
    fun sort(sourceArray: IntArray, numberOfResources: Int): IntArray
    val resourcesKind: ResourceKind
}

object ParallelMergeSort : Sorter {
    private data class SubArray(val left: Int, val right: Int) {
        val size = right - left + 1
        val middle = (right + left) / 2
    }

    override val resourcesKind: ResourceKind = ResourceKind.THREADS

    override fun sort(
        sourceArray: IntArray,
        numberOfResources: Int,
    ): IntArray {
        if (numberOfResources < 1) {
            error(
                """
                    Invalid number of threads: $numberOfResources. 
                    Number of threads must be >= 1.
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
   private fun sort(
       initialArray: IntArray,
       sourceArray: SubArray,
       destArray: IntArray,
       startIndexInDestArray: Int,
       numberOfThreads: Int = 1
   ) {
        if (initialArray.isEmpty()) return
        if (sourceArray.size == 1) {
            destArray[startIndexInDestArray] = initialArray[sourceArray.left]
            return
        }
        val tempArray = IntArray(sourceArray.size)
        // number of elements in the right part of subArray sa
        val numOfElemsInRightPartOfSourceArray = sourceArray.middle - sourceArray.left + 1
        if (numberOfThreads > 1) {
            val th = Thread { sort(initialArray, SubArray(sourceArray.left, sourceArray.middle), tempArray, 0,
                numberOfThreads / 2) }
            th.start()
            sort(initialArray, SubArray(sourceArray.middle + 1, sourceArray.right), tempArray,
                numOfElemsInRightPartOfSourceArray,
                numberOfThreads - numberOfThreads / 2)
            th.join()
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
            numberOfThreads
        )
   }

    // merge two sorted sourceArray[l1, r1] and sourceArray[l2, r2] into destArray[s, s + r1 - l1 + r2 - l2 + 1]
    private fun merge(
        arraysForMerge: ArraysForMerge,
        sourceArray1: SubArray,
        sourceArray2: SubArray,
        startIndexInDestArray: Int,
        numberOfThreads: Int = 1
    ) {
        if (sourceArray1.size < sourceArray2.size) {
            merge(arraysForMerge, sourceArray2, sourceArray1, startIndexInDestArray, numberOfThreads)
            return
        }
        if (sourceArray1.size == 0) return

        val mid1 = sourceArray1.middle
        val mid2 = lowerBound(arraysForMerge.from[mid1], arraysForMerge.from, sourceArray2)
        val mid3 = startIndexInDestArray + (mid1 - sourceArray1.left) + mid2 - sourceArray2.left
        arraysForMerge.to[mid3] = arraysForMerge.from[mid1]
        if (numberOfThreads > 1) {
            val th = Thread {
                merge(arraysForMerge,
                    SubArray(sourceArray1.left, mid1 - 1),
                    SubArray(sourceArray2.left, mid2 - 1),
                    startIndexInDestArray,
                    numberOfThreads / 2)
            }
            th.start()
            merge(
                arraysForMerge,
                SubArray(mid1 + 1, sourceArray1.right),
                SubArray(mid2, sourceArray2.right),
                mid3 + 1,
                numberOfThreads - numberOfThreads / 2
            )
            th.join()
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
