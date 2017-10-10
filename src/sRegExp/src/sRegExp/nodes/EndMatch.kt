package sRegExp.nodes

import sRegExp.CapturePair
import sRegExp.CompiledRegExp
import sRegExp.ExpReader

class EndMatch(re : CompiledRegExp) : MatchNode(re){
    override fun init(sr : ExpReader) : MatchNode{
        sr.read()//干掉$
        return this
    }

    override fun match(str : String) : ArrayList<Pair<Int, ArrayList<CapturePair>>> {
        if(str.isEmpty())
            return arrayListOf(Pair(0, ArrayList()))
        else
            return ArrayList()
    }
}