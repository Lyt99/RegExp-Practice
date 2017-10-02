package sRegExp.nodes

import sRegExp.ExpReader

class EndMatch : MatchNode(){
    override fun init(sr : ExpReader) : MatchNode{
        sr.read()//干掉$
        return this
    }

    override fun match(str : String, pos : Int) : ArrayList<Int>{
        if(str.isEmpty())
            return arrayListOf(0)
        else
            return ArrayList()
    }
}