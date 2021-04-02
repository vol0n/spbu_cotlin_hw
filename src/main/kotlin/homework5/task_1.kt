package homework5

import java.io.File

open class Token(var lexeme: String = "") {
    open fun print(indentWidth: Int, indentStr: String = ".") {
        println(indentStr.repeat(indentWidth) + lexeme)
    }
}

class Tokenizer(path: String) {
    private val sourceStr: String = File(path).readText()
    private var i: Int = -1
    companion object {
        const val END = 'e'
    }

    fun next(): Token {
        while (nextChar() == ' ')
            move()
        val c = nextChar()
        val token: Token = when {
            c.isDigit() || (c == '-' && nextChar(2).isDigit()) -> {
                """-?\d+""".toRegex().find(sourceStr, i + 1)?.value?.toInt()?.let { Operand(it) } as Token
            }
            c in "+-*/" -> {
                Operator(c)
            }
            c in "()" -> {
                Token(c.toString())
            }
            else -> {
                error("Unknown symbol in source string: ${sourceStr[i]}")
            }
        }

        move(token.lexeme.length)
        return token
    }

    private fun move(shift: Int = 1) {
        if (i + shift < sourceStr.length) {
            i += shift
        } else {
            println("Shift was not performed. i = $i, data[i] = ${sourceStr[i]}, shift = $shift")
        }
    }

    private fun nextChar(shift: Int = 1): Char {
        if (i + shift < sourceStr.length) {
            return sourceStr[i + shift]
        }
        return END
    }
}

class Node(private val data: Token) {
    var p: Node? = null
    var right: Node? = null
    var left: Node? = null

    fun compute(): Int? {
        return when (data) {
            is Operand -> data.compute()
            is Operator -> data.compute(left?.compute(), right?.compute())
            else -> null
        }
    }

    fun print(indentWidth: Int, indentStr: String = ".", increment: Int = 4) {
        data.print(indentWidth)
        left?.print(indentWidth + increment, indentStr)
        right?.print(indentWidth + increment, indentStr)
    }
}

class ParseTree(path: String) {
    private var root: Node? = null
    private val tokenizer = Tokenizer(path)

    init {
        parseExpr(root)
    }

    private fun parseExpr(v: Node?): Node {
        var curToken = tokenizer.next()
        when {
            curToken.lexeme == "(" -> {
                curToken = tokenizer.next()
                if (curToken !is Operator) {
                    error("Expression parsing failed: expected operator after '(', but got ${curToken.lexeme}")
                }
                val x = Node(curToken)
                if (v == null) {
                    root = x
                }
                x.p = v
                x.left = parseExpr(x)
                x.right = parseExpr(x)
                if (tokenizer.next().lexeme != ")") {
                    error("Expression parsing failed: expected ')' after two operands, but got ${curToken.lexeme}")
                }
                return x
            }
            curToken is Operand -> {
                val x = Node(curToken)
                if (v == null) {
                    root = x
                }
                x.p = v
                return x
            }
            else -> {
                error("Parsing failed: expected an '(' or an operand, but got ${curToken.lexeme}")
            }
        }
    }

    fun compute(): Int? {
       return root?.compute()
    }

    fun print() {
        root?.print(0, ".")
    }
}

class Operator(type: Char) : Token(type.toString()) {
    override fun print(indentWidth: Int, indentStr: String) {
        println(indentStr.repeat(indentWidth) + lexeme)
    }

    fun compute(op1: Int?, op2: Int?): Int? {
        if (op1 == null || op2 == null) {
            return null
        }
        return when (lexeme[0]) {
            '+' -> op1 + op2
            '-' -> op1 - op2
            '*' -> op1 * op2
            '/' -> op1 / op2
            else -> null
        }
    }
}

class Operand(private val num: Int) : Token(num.toString()) {
    override fun print(indentWidth: Int, indentStr: String) {
        println(indentStr.repeat(indentWidth) + lexeme)
    }

    fun compute() = num
}
