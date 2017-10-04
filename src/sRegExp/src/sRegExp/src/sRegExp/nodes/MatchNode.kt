package sRegExp.nodes

import sRegExp.CompiledRegExp
import sRegExp.ExpReader

abstract  class MatchNode constructor(re : CompiledRegExp){
    var nextNode : MatchNode? = null
    internal var parentRe : CompiledRegExp = re

    abstract fun init(sr : ExpReader) : MatchNode
    abstract fun match(str : String) : ArrayList<Int>

    companion object {
        internal fun compileOne(reader: ExpReader, parentRe: CompiledRegExp): MatchNode {

            when (reader.peek()) {
                '$' -> {
                    return EndMatch(parentRe).init(reader)
                }

                '(' -> {
                    return CaptureNode(parentRe).init(reader)
                }

                ')','{', '}' -> {
                    throw Exception("Syntax error ${reader.peek()}")
                }

                else -> {
                    return CharMatch(parentRe).init(reader)
                }
            }

        }
    }
}