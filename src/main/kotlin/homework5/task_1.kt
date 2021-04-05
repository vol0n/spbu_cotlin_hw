package homework5

import java.io.File

interface Node {
    companion object {
        const val indentWidth = 3
        const val indentStr = "."
    }
    var height: Int
    fun compute(): Int
}

class Operator(private val type: Char, private var left: Node, private var right: Node) : Node {
    override var height = 0
    override fun compute(): Int {
        return when (type) {
            '+' -> left.compute() + right.compute()
            '*' -> left.compute() * right.compute()
            '/' -> left.compute() / right.compute()
            '-' -> left.compute() - right.compute()
            else -> error("Unknown operator: $type !")
        }
    }

    override fun toString(): String = Node.indentStr.repeat(height) + type +
            "\n" + left.also { it.height = this.height + Node.indentWidth } +
            "\n" + right.also { it.height = this.height + Node.indentWidth }
}

class Operand(private val value: Int) : Node {
    override var height: Int = 0
    override fun compute() = value
    override fun toString(): String = ".".repeat(height) + value
}

enum class Type {
    OPERATOR, OPERAND, LEFT, RIGHT
}

data class Token(val lexeme: String, val type: Type)

class Tokenizer(path: String) {
    private val sourceStr: String = File(path).readText()
    private var i: Int = -1
    fun next(): Token {
        while (nextChar() == ' ')
            move()
        val c = nextChar()
        val token: Token = when {
            c.isDigit() || (c == '-' && nextChar(2).isDigit()) -> {
                """-?\d+""".toRegex().find(sourceStr, i + 1)?.value?.run {
                    Token(this, Type.OPERAND)
                } as Token
            }
            c in "+-*/" -> Token(c.toString(), Type.OPERATOR)
            c == '(' -> Token(c.toString(), Type.LEFT)
            c == ')' -> Token(c.toString(), Type.RIGHT)
            else -> error("Unknown symbol in source string: ${sourceStr[i]}")
        }
        move(token.lexeme.length)
        return token
    }

    private fun move(shift: Int = 1) {
        if (i + shift < sourceStr.length) {
            i += shift
        } else { throw error("Index out of range: ${i + shift}") }
    }

    private fun nextChar(shift: Int = 1): Char {
        if (i + shift < sourceStr.length) {
            return sourceStr[i + shift]
        }
        throw error("Index out of range: ${i + shift}")
    }
}

class ParseTree(path: String) {
    private val tokenizer = Tokenizer(path)
    private var root: Node = parseExpr()

    private fun parseExpr(): Node {
        var curToken = tokenizer.next()
        return when (curToken.type) {
            Type.LEFT -> {
                curToken = tokenizer.next()
                if (curToken.type != Type.OPERATOR) {
                    error("Expression parsing failed: expected operator after '(', but got ${curToken.lexeme}")
                }
                val x = Operator(curToken.lexeme[0], parseExpr(), parseExpr())
                if (tokenizer.next().type != Type.RIGHT) {
                    error("Expression parsing failed: expected ')' after two operands, but got ${curToken.lexeme}")
                }
                x
            }
            Type.OPERAND -> Operand(curToken.lexeme.toInt())
            else -> error("Parsing failed: expected an '(' or an operand, but got ${curToken.lexeme}")
        }
    }

    fun compute(): Int = root.compute()

    override fun toString() = root.toString()
}
