package sRegExp.nodes

import sRegExp.ExpReader

abstract  class MatchNode{
    var nextNode : MatchNode? = null

    abstract fun init(sr : ExpReader) : MatchNode
    abstract fun match(str : String, pos : Int) : ArrayList<Int>
}