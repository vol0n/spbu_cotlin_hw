package homework6

class ParallelMergeSort(val destinationArray: IntArray, val sourceArray: IntArray, initialNumberOfThreads: Int) {
    init {
        sort(SubArray(0, sourceArray.lastIndex), destinationArray, 0, initialNumberOfThreads)
    }

    private data class SubArray(val left: Int, val right: Int) {
        val size: Int
            get() = right - left + 1
        val middle: Int
            get() = (right + left) / 2
    }

   private fun lowerBound(value: Int, array: IntArray, sa: SubArray): Int {
        if (sa.left > sa.right) return sa.left
        var l = sa.left - 1
        var r = sa.right + 1
        var mid: Int
        // array[l] < value <= array[r]
        while (l + 1 < r) {
            mid = (l + r) / 2
            if (array[mid] < value) {
                l = mid
            } else {
                r = mid
            }
        }
        return r
    }

   // for detekt to calm down on parameter list
   private data class ArraysForMerge(val from: IntArray, val to: IntArray)

   // sort sourceArray[l, r] and save into destArray[s, s + r - l]
   private fun sort(sa: SubArray, destArray: IntArray, s: Int, numberOfThreads: Int = 1) {
        if (sourceArray.isEmpty()) return
        if (sa.size == 1) {
            destArray[s] = sourceArray[sa.left]
            return
        }
        val tempArray = IntArray(sa.size)
        // number of elements in the right part of subArray sa
        val n1 = sa.middle - sa.left + 1
        if (numberOfThreads != 1) {
            val th = Thread { sort(SubArray(sa.left, sa.middle), tempArray, 0, numberOfThreads / 2) }
            th.start()
            sort(SubArray(sa.middle + 1, sa.right), tempArray, n1,
                numberOfThreads - numberOfThreads / 2)
            th.join()
        } else {
            sort(SubArray(sa.left, sa.middle), tempArray, 0)
            sort(SubArray(sa.middle + 1, sa.right), tempArray, n1)
        }

        merge(
            ArraysForMerge(tempArray, destArray),
            SubArray(0, n1 - 1),
            SubArray(n1, sa.size - 1),
            s,
            numberOfThreads
        )
   }

    // merge two sorted sourceArray[l1, r1] and sourceArray[l2, r2] into destArray[s, s + r1 - l1 + r2 - l2 + 1]
    private fun merge(
        arraysForMerge: ArraysForMerge,
        sa1: SubArray,
        sa2: SubArray,
        s: Int,
        numberOfThreads: Int = 1
    ) {
        if (sa1.size < sa2.size) {
            merge(arraysForMerge, sa2, sa1, s, numberOfThreads)
            return
        }
        if (sa1.size == 0) return

        val mid1 = sa1.middle
        val mid2 = lowerBound(arraysForMerge.from[mid1], arraysForMerge.from, sa2)
        val mid3 = s + (mid1 - sa1.left) + mid2 - sa2.left
        arraysForMerge.to[mid3] = arraysForMerge.from[mid1]
        if (numberOfThreads > 1) {
            val th = Thread {
                merge(arraysForMerge, SubArray(sa1.left, mid1 - 1), SubArray(sa2.left, mid2 - 1), s,
                    numberOfThreads / 2)
            }
            th.start()
            merge(
                arraysForMerge,
                 SubArray(mid1 + 1, sa1.right), SubArray(mid2, sa2.right), mid3 + 1,
                numberOfThreads - numberOfThreads / 2
            )
            th.join()
        } else {
            merge(
                arraysForMerge,
                SubArray(sa1.left, mid1 - 1),
                SubArray(sa2.left, mid2 - 1),
                s
            )
            merge(
                arraysForMerge,
                SubArray(mid1 + 1, sa1.right),
                SubArray(mid2, sa2.right),
                mid3 + 1
            )
        }
    }
}
