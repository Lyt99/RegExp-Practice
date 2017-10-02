package sRegExp

import sRegExp.nodes.MatchNode
import sRegExp.nodes.*
import java.util.*

class CompiledRegExp constructor(exp : String) {

    private var begin = VoidNode()
    private var exp : String = ""
    private var matchBegin = false

    var group : Array<String>? = null

    init{
        this.exp = exp
        this.compile()
    }


    private fun compile() {
        var reader = ExpReader(this.exp)

        var lastnode : MatchNode = this.begin
        var node : MatchNode
        while(!reader.end()){
                if(reader.peek() == '^') {
                    this.matchBegin = true
                    reader.read()
                }
                node = this.complieOne(reader)
                lastnode.nextNode = node
                lastnode = node
            }


        }


    fun complieOne(reader : ExpReader): MatchNode {
        when(reader.peek()){
            '$' ->
            {
                return EndMatch().init(reader)
            }
            else ->{
                return CharMatch().init(reader)
            }
        }

    }

    fun match(text : String) : Boolean{
        if(this.matchBegin)
            return this.match_reg(text) != -1
        for(i in 0..text.length){
            if(this.match_reg(text.substring(i)) != -1) return true
        }
        return false
    }

    fun findOne(text : String) : String?{
        val r = this.findIndex(text)
        //println(r)
        if(r.second >= 0)
            return text.substring(r.first, r.second)
        else
            return null
    }

    fun findIndex(text: String) : Pair<Int, Int>{
        var s = 0
        var e = 0

        if(this.matchBegin)
            e = this.match_reg(text)
        else {
            for (i in 0..text.length) {
                e = this.match_reg(text.substring(i))

                if(e != -1){
                    s = i
                    e += s
                    break
                }
            }
        }

        //println("s: {$s}  e: {$e}")
        return Pair(s, e)
    }

    private fun match_reg(str : String) : Int{//返回最终位置，-1为未匹配到
        var stk = Stack<Pair<Int, MatchNode?>>()
        stk.push(Pair(0, begin.nextNode))

        while(stk.isNotEmpty()){
            var i = stk.pop()
            var r = i.second!!.match(str.substring(i.first), i.first)
            //println(str.substring(i.first))
            //println(i.second)
            if(r.isEmpty()) {//未匹配到
                //println("匹配失败")
                continue
            }
            if(i.second!!.nextNode == null){//匹配完毕
                //println("匹配成功")
                return i.first + r[r.count() - 1]
            }
            for(res in r){
                stk.push(Pair(i.first + res, i.second!!.nextNode))
            }
        }

        return -1
    }

    private fun find_reg(){

    }

}