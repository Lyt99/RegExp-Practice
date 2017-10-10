package sRegExp.nodes

import sRegExp.CapturePair
import sRegExp.CompiledRegExp
import sRegExp.ExpReader

class CaptureNode(re : CompiledRegExp) : MatchNode(re) {

    private var begin = BranchNode(this.parentRe)
    var groupId = -1

    override fun init(sr: ExpReader): MatchNode {
        this.groupId = this.parentRe.getNewGroupId()

        val exp: String? = sr.readPair() ?: throw Exception("Syntax error.")
        val reader = ExpReader(exp!!.substring(1,exp.length - 1))
        this.begin.init(reader)

        return this
    }

    override fun match(str: String): ArrayList<Pair<Int, ArrayList<CapturePair>>> {

        var ret = this.begin.match(str)
        for(i in ret) {
            i.second.add(CapturePair(this.groupId, str.substring(0, i.first)))
        }

        ret.reverse()
        return ret
    }

}