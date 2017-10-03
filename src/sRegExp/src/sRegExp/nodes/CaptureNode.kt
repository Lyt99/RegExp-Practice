package sRegExp.nodes

import sRegExp.CompiledRegExp
import sRegExp.ExpReader

class CaptureNode(re : CompiledRegExp) : MatchNode(re) {

    private var begin = BranchNode(this.parentRe)
    var groupId = 0

    override fun init(sr: ExpReader): MatchNode {
        this.groupId = this.parentRe.getNewGroupId()

        var exp = sr.readPair()
        if(exp == null) throw Exception("Syntax error.")
        var reader = ExpReader(exp.substring(1,exp.length - 1))
        this.begin.init(reader)

        return this
    }

    override fun match(str: String): ArrayList<Int> {
        val a =this.begin.match(str)
        a.reverse()
        return a
    }

}