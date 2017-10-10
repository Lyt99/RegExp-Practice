package sRegExp.nodes

import sRegExp.CapturePair
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

    override fun match(str : String) : ArrayList<Pair<Int, ArrayList<CapturePair>>> {
        val res = ArrayList<Pair<Int, ArrayList<CapturePair>>>()
        val stk = Stack<Triple<Int, MatchNode?, ArrayList<CapturePair>>>() //起始index 当前匹配 [CapturePair]

        for (i in this.branches.reversed()) {
            stk.push(Triple(0, i, ArrayList()))
        }


        while (stk.isNotEmpty()) {
            val i = stk.pop()
            val r = i.second!!.match(str.substring(i.first))

            if (r.isEmpty()) {//未匹配到
                continue
            }
            if (i.second?.nextNode == null) {//匹配完毕
                r.reverse()//为什么要reverse???，啧
                r.forEach {
                    var list = i.third.clone() as ArrayList<CapturePair>
                    list.addAll(it.second)
                    res.add(Pair(it.first + i.first, list))
                }
                continue
            }
            for (it in r) {
                //stk.push(Triple(i.first + it, i.second!!.nextNode, Pair(i.second, str.substring(i.first, it + i.first))))
                var list = i.third.clone() as ArrayList<CapturePair>//unchecked,绝望
                list.addAll(it.second)
                stk.push(Triple(i.first + it.first, i.second!!.nextNode, list))
            }
        }

        return res

    }

    fun matchReg(str : String) : Int{
        val stk = Stack<Triple<Int, MatchNode?, ArrayList<CapturePair>>>() //起始index 当前匹配 [CapturePair]
        for (i in this.branches.reversed()) {
            stk.push(Triple(0, i, ArrayList()))
        }


        while (stk.isNotEmpty()) {
            val i = stk.pop()
            val r = i.second!!.match(str.substring(i.first))

            if (r.isEmpty()) {//未匹配到
                println("匹配失败")
                continue
            }
            if (i.second?.nextNode == null) {//匹配完毕
                val cns = r[r.count() - 1].second
                cns.addAll(i.third)//合并捕获组

                for(node in cns){
                    this.parentRe.setGroup(node.id, node.str)
                }

                return i.first + r[r.count() - 1].first
            }
            for (it in r) {
                var list = i.third.clone() as ArrayList<CapturePair>
                list.addAll(it.second)
                stk.push(Triple(i.first + it.first, i.second!!.nextNode, list))
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