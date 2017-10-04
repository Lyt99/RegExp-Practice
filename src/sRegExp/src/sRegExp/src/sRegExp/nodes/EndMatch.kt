package sRegExp.nodes

import sRegExp.CompiledRegExp
import sRegExp.ExpReader

class EndMatch(re : CompiledRegExp) : MatchNode(re){
    override fun init(sr : ExpReader) : MatchNode{
        sr.read()//干掉$
        return this
    }

    override fun match(str : String) : ArrayList<Int>{
        if(str.isEmpty())
            return arrayListOf(0)
        else
            return ArrayList()
    }
}