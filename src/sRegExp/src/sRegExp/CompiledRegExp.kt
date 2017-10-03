package sRegExp

import sRegExp.nodes.*
import java.util.*

class CompiledRegExp constructor(exp : String) {

    private var begin = BranchNode(this)
    private var exp : String = ""
    private var matchBegin = false

    internal var group  = ArrayList<String>()

    init{
        this.exp = exp
        this.compile()
    }


    private fun compile() {
        var reader = ExpReader(this.exp)
        if (reader.peek() == '^') {
            this.matchBegin = true
            reader.read()
        }

        this.begin.init(reader)

    }

    fun match(text : String) : Boolean{
        if(this.matchBegin)
            return this.matchReg(text) != -1
        for(i in 0..text.length){
            if(this.matchReg(text.substring(i)) != -1) return true
        }
        return false
    }

    fun findAll(text :String) : Array<FindResult> {
        val r = ArrayList<FindResult>()

        var index = 0
        var str = text
        while(index < str.length) {
            str = str.substring(index)
            var n = this.findIndex(str)
            if (n.second > 0) {
                val fr = FindResult()
                fr.success = true
                fr.index = n.first
                fr.length = n.second - n.first

                val g = arrayListOf(str.substring(n.first, n.second))
                g.addAll(this.group)
                fr.group = g.toTypedArray()
                r.add(fr)
                index = n.second
            }
            else{
                break
            }
        }

        return r.toTypedArray()
    }

    fun findOne(text : String) : FindResult{
        val r = this.findIndex(text)
        //println(r)
        var ret = FindResult()
        if(r.second >= 0)
        {
            ret.success = true
            ret.index = r.first
            ret.length = r.second - r.first
            val l = arrayListOf(text.substring(r.first, r.second))
            l.addAll(this.group)

            ret.group = l.toTypedArray()
        }

        return ret
    }

    fun findIndex(text: String) : Pair<Int, Int>{
        var s = 0
        var e = 0

        if(this.matchBegin)
            e = this.matchReg(text)
        else {
            for (i in 0..text.length) {
                //println("findIndex方法: " + text.substring(i))
                e = this.matchReg(text.substring(i))

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

    private fun matchReg(str : String) : Int{
        return this.begin.matchReg(str)
    }

    internal fun getNewGroupId() : Int{
        var l = this.group.count()
        this.group.add("")
        return l
    }

    internal fun setGroup(id : Int, str : String) {
        this.group[id] = str//maybe out of range
    }

}