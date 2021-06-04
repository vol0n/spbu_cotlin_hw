package retest.task2

interface ArithmeticAvailable<T> {
    operator fun plus(rhs: T): T
    operator fun times(rhs: T): T
    operator fun minus(rhs: T): T
    fun isZero(): Boolean
}

class Vector<T : ArithmeticAvailable<T>>(source: List<T>) {
    val data = source.toList()
    val size: Int
        get() = data.size

    private fun check(other: Vector<T>) {
        if (other.data.size != data.size) {
            error("To perform operation Vectors must have the same size!")
        }
    }

    operator fun plus(rightHandSide: Vector<T>): Vector<T> {
        check(rightHandSide)
        if (data.isEmpty()) return Vector(emptyList())
        return Vector(
            data.zip(rightHandSide.data) { leftOperand, rightOperand -> leftOperand + rightOperand }
        )
    }

    operator fun minus(rhs: Vector<T>): Vector<T> {
        check(rhs)
        if (data.isEmpty()) return Vector(emptyList())
        return Vector(
            data.zip(rhs.data) { leftOperand, rightOperand -> leftOperand - rightOperand }
        )
    }

    operator fun times(rhs: Vector<T>): T {
        check(rhs)
        if (data.isEmpty()) error("Sorry, no idea how to dot two empty Vector<T>!")
        return data.zip(rhs.data) { leftOperand, rightOperand -> leftOperand * rightOperand }
            .reduce { acc, t -> acc + t }
    }

    fun isAllZero(): Boolean {
        if (data.isEmpty()) return false
        return data.all { it.isZero() }
    }
}
