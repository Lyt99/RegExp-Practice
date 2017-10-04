package sRegExp.nodes

import sRegExp.CompiledRegExp
import sRegExp.ExpReader
import java.util.*
import kotlin.collections.ArrayList

//不会出现在任何nextNode里
class BranchNode(re : CompiledRegExp) : MatchNode(re){
    private var branches = ArrayList<MatchNode>()
    private var lastNode : MatchNode? = null
    private var switch = false

    override fun init(sr : ExpReader) : MatchNode{
        while(!sr.end()){
            val ch = sr.peek()
            when(ch){
                '|' -> {
                    this.switch()
                    sr.read()
                }

                else -> {
                    val node = compileOne(sr, this.parentRe)
                    this.addNode(node)
                }
            }
        }
        return this
    }

    override fun match(str : String) : ArrayList<Int> {
        val res = ArrayList<Int>()
        val stk = Stack<Triple<Int, MatchNode?, Pair<MatchNode?, String>>>() //起始index, 当前匹配, <上一匹配,上一匹配文本>
        for (i in this.branches.reversed()) {
            stk.push(Triple(0, i, Pair(null, "")))
        }


        while (stk.isNotEmpty()) {
            val i = stk.pop()

            val r = i.second!!.match(str.substring(i.first))

            if (r.isEmpty()) {//未匹配到
                continue
            }
            if (i.second?.nextNode == null) {//匹配完毕
                r.forEach { res.add(i.first + it) }
                continue
            }
            for (it in r) {
                stk.push(Triple(i.first + it, i.second!!.nextNode, Pair(i.second, str.substring(i.first, it + i.first))))
            }
        }


        return res

    }

    fun matchReg(str : String) : Int{

        val stk = Stack<Triple<Int, MatchNode?, Pair<MatchNode?, String>>>() //起始index, 当前匹配, <上一匹配,上一匹配文本>
        for (i in this.branches.reversed()) {
            stk.push(Triple(0, i, Pair(null, "")))
        }


        while (stk.isNotEmpty()) {
            val i = stk.pop()
            val r = i.second!!.match(str.substring(i.first))
            if(i.third.first != null && i.third.first is CaptureNode) { //设置捕获组
                this.parentRe.setGroup((i.third.first as CaptureNode).groupId, i.third.second)
            }

            if (r.isEmpty()) {//未匹配到
                continue
            }
            if (i.second?.nextNode == null) {//匹配完毕
                //特例：捕获器在结尾
                if(i.second is CaptureNode) {
                    this.parentRe.setGroup((i.second as CaptureNode).groupId, str.substring(i.first, i.first + r[r.count() - 1]))
                }
                return i.first + r[r.count() - 1]
            }
            for (res in r) {
                stk.push(Triple(i.first + res, i.second!!.nextNode, Pair(i.second, str.substring(i.first, res + i.first))))
            }
        }
            return -1
    }

    private fun addNode(node : MatchNode) {
        if(lastNode == null || switch) {
            branches.add(node)
            lastNode = node
            switch = false
        }
        else{
            lastNode!!.nextNode = node
            lastNode = node
        }
    }

    private fun switch() {
        this.switch = true
    }

}