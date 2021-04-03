package homework5

import java.io.File

abstract class Node {
    var left: Node? = null
    var right: Node? = null
    abstract fun compute(): Int
    abstract fun print(indentWidth: Int, indentStr: String = ".")
}

class Operator(private val type: Char) : Node() {
    override fun compute(): Int {
        val l = left; val r = right
        if (l == null || r == null) error("Operator Node must have two children!")
        return when (type) {
            '+' -> l.compute() + r.compute()
            '*' -> l.compute() * r.compute()
            '/' -> l.compute() / r.compute()
            '-' -> l.compute() - r.compute()
            else -> error("Unknown operator: $type !")
        }
    }

    override fun print(indentWidth: Int, indentStr: String) {
        println(indentStr.repeat(indentWidth) + type)
        left?.print(indentWidth, indentStr)
        right?.print(indentWidth, indentStr)
    }
}

class Operand(private val value: Int) : Node() {
    override fun compute() = value
    override fun print(indentWidth: Int, indentStr: String) {
        println(indentStr.repeat(indentWidth) + value.toString())
        left?.print(indentWidth, indentStr)
        right?.print(indentWidth, indentStr)
    }
}

enum class Type {
    OPERATOR, OPERAND, LEFT, RIGHT
}

class Tokenizer(path: String) {
    private val sourceStr: String = File(path).readText()
    private var i: Int = -1
    fun next(): Pair<String, Type> {
        while (nextChar() == ' ')
            move()
        val c = nextChar()
        val token: Pair<String, Type> = when {
            c.isDigit() || (c == '-' && nextChar(2).isDigit()) -> {
                """-?\d+""".toRegex().find(sourceStr, i + 1)?.value?.run {
                    this to Type.OPERAND
                } as Pair<String, Type>
            }
            c in "+-*/" -> c.toString() to Type.OPERATOR
            c == '(' -> c.toString() to Type.LEFT
            c == ')' -> c.toString() to Type.RIGHT
            else -> error("Unknown symbol in source string: ${sourceStr[i]}")
        }
        move(token.first.length)
        return token
    }

    private fun move(shift: Int = 1) {
        if (i + shift < sourceStr.length) {
            i += shift
        } else { throw IndexOutOfBoundsException(i + shift) }
    }

    private fun nextChar(shift: Int = 1): Char {
        if (i + shift < sourceStr.length) {
            return sourceStr[i + shift]
        }
        throw IndexOutOfBoundsException(i + shift)
    }
}

class ParseTree(path: String) {
    private val tokenizer = Tokenizer(path)
    private var root: Node = parseExpr()

    private fun parseExpr(): Node {
        var curToken = tokenizer.next()
        return when (curToken.second) {
            Type.LEFT -> {
                curToken = tokenizer.next()
                if (curToken.second != Type.OPERATOR) {
                    error("Expression parsing failed: expected operator after '(', but got ${curToken.first}")
                }
                val x = Operator(curToken.first[0])
                x.left = parseExpr()
                x.right = parseExpr()
                if (tokenizer.next().second != Type.RIGHT) {
                    error("Expression parsing failed: expected ')' after two operands, but got ${curToken.first}")
                }
                x
            }
            Type.OPERAND -> Operand(curToken.first.toInt())
            else -> error("Parsing failed: expected an '(' or an operand, but got ${curToken.first}")
        }
    }

    fun compute(): Int = root.compute()

    fun print() = root.print(0, ".")
}
