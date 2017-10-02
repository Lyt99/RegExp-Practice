package sRegExp.nodes

import sRegExp.ExpReader


class VoidNode : MatchNode(){
    override fun init(sr : ExpReader) : MatchNode{
        return this
    }

    override fun match(str : String, pos : Int) : ArrayList<Int>{
        return arrayListOf(0)
    }
}