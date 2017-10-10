package sRegExp.nodes

import sRegExp.CapturePair
import sRegExp.CompiledRegExp
import sRegExp.ExpReader

abstract  class MatchNode constructor(re : CompiledRegExp){
    var nextNode : MatchNode? = null
    internal var parentRe : CompiledRegExp = re

    internal abstract fun init(sr : ExpReader) : MatchNode
    internal abstract fun match(str : String) : ArrayList<Pair<Int, ArrayList<CapturePair>>>

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