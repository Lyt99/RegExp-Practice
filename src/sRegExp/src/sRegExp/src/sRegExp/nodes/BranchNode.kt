package sRegExp.nodes

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match
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
        var res = ArrayList<Int>()
        var stk = Stack<Triple<Int, MatchNode?, Pair<MatchNode?, String>>>() //起始index, 当前匹配, <上一匹配,上一匹配文本>
        for (i in this.branches.reversed()) {
            stk.push(Triple(0, i, Pair(null, "")))
        }


        while (stk.isNotEmpty()) {
            var i = stk.pop()

            //println("m模式: ${i.second}, index: ${i.first}")
            //println("m匹配内容: ${str.substring(i.first)}")

            var r = i.second!!.match(str.substring(i.first))

            if (r.isEmpty()) {//未匹配到
                //println("m匹配失败")
                continue
            }
            if (i.second?.nextNode == null) {//匹配完毕
                //println("m匹配成功")
                r.forEach { res.add(i.first + it) }
                continue
            }
            for (res in r) {
                //println("FT: ${i.first} to ${res + i.first} : ${str.substring(i.first, res + i.first)}")
                stk.push(Triple(i.first + res, i.second!!.nextNode, Pair(i.second, str.substring(i.first, res + i.first))))
            }
        }


        return res

    }

    fun matchReg(str : String) : Int{

        var stk = Stack<Triple<Int, MatchNode?, Pair<MatchNode?, String>>>() //起始index, 当前匹配, <上一匹配,上一匹配文本>
        for (i in this.branches.reversed()) {
            stk.push(Triple(0, i, Pair(null, "")))
        }


        while (stk.isNotEmpty()) {
            var i = stk.pop()

            //println("模式: ${i.second}, index: ${i.first}")
            //println("匹配内容: ${str.substring(i.first)}")

            var r = i.second!!.match(str.substring(i.first))
            if(i.third.first != null && i.third.first is CaptureNode) { //设置捕获组
                //println("设置捕获: ${i.third.second}")
                this.parentRe.setGroup((i.third.first as CaptureNode).groupId, i.third.second)
            }

            if (r.isEmpty()) {//未匹配到
                //println("匹配失败")
                continue
            }
            if (i.second?.nextNode == null) {//匹配完毕
                //println("匹配成功")
                //特例：捕获器在结尾
                if(i.second is CaptureNode) {
                    this.parentRe.setGroup((i.second as CaptureNode).groupId, str.substring(i.first, i.first + r[r.count() - 1]))
                }
                return i.first + r[r.count() - 1]
            }
            for (res in r) {
                //println("FT: ${i.first} to ${res + i.first} : ${str.substring(i.first, res + i.first)}")
                stk.push(Triple(i.first + res, i.second!!.nextNode, Pair(i.second, str.substring(i.first, res + i.first))))
            }
        }
            return -1


        /*
        var ok_stk = Stack<Pair<MatchNode, String>>()//每个node匹配成功得到的列表
        var stk = Stack<Triple<Int, MatchNode, Int>>() //起始index, 匹配模式，层数

        for (i in this.branches.reversed()) {
            stk.push(Triple(0, i, 0))
        }

        var lastTriple : Triple<Int, MatchNode, Int>? = null
        while(stk.isNotEmpty()) {
            if(lastTriple != null) {//如果上一个有
                ok_stk.push(Pair(lastTriple.second, str.substring(lastTriple.first)))//推入
            }

            var i = stk.pop() //弹出当前Triple
            var r = i.second?.match(str.substring(i.first)) //与node匹配，获得匹配列表

            if(r.isEmpty()) {//没有匹配到
                //println("匹配失败")
                var step = stk.peek().third
                while(ok_stk.count() > step) ok_stk.pop()//弹出错误的
                continue
            }

            if (i.second.nextNode == null) {//匹配完毕
                //println("匹配成功")
                return ok_stk
            }

            for (res in r) { //匹配到多个结果
                stk.push(Triple(i.first + res, i.second.nextNode, i.third + 1) as Triple<Int, MatchNode, Int>?)
            }

            lastTriple = i
        }


        return null
        */
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