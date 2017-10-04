package sRegExp

import sRegExp.nodes.*
import kotlin.collections.ArrayList

class CompiledRegExp constructor(exp : String) {

    private var begin = BranchNode(this)
    private var exp : String = ""
    private var matchBegin = false

    private var group  = ArrayList<String>()

    init{
        this.exp = exp
        this.compile()
    }


    private fun compile() {
        val reader = ExpReader(this.exp)
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
        var len = 0
        while(index < str.length) {
            str = str.substring(index)
            val n = this.findIndex(str)
            if (n.second > 0) {
                val fr = FindResult()
                fr.success = true
                fr.index = len + n.first
                fr.length = n.second - n.first
                val g = arrayListOf(str.substring(n.first, n.second))
                g.addAll(this.group)
                fr.group = g.toTypedArray()
                r.add(fr)
                index = n.second
                len += index
            }
            else{
                break
            }
        }

        return r.toTypedArray()
    }

    fun findOne(text : String) : FindResult{
        val r = this.findIndex(text)
        val ret = FindResult()
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

    fun replace(text : String, rep : String) : String {
        val r = this.findAll(text)
        var ret = text
        var offset = 0

        for(i in r){
            ret = ret.substring(0, i.index + offset) + rep + ret.substring(i.index + i.length + offset)
            offset += rep.length - i.length
        }

        return ret
    }

    fun replace(text : String, func : (r : String) -> String) : String {
        val r = this.findAll(text)
        var ret = text
        var offset = 0

        for(i in r){
            val rep = func(i.group[0])
            ret = ret.substring(0, i.index + offset) + rep + ret.substring(i.index + i.length + offset)
            offset += rep.length - i.length
        }

        return ret
    }

    fun replaceOne(text : String, rep : String) : String {
        val r = this.findOne(text)
        if(r.success)
            return text.replaceFirst(r.group[0], rep)
        else
            return text
    }

    fun replaceOne(text : String, func : (r : String) -> String) : String {
        val r = this.findOne(text)
        if(r.success)
            return text.replaceFirst(r.group[0], func(r.group[0]))
        else
            return text
    }

    fun split(text : String) : Array<String> {
        var str = text
        val r = this.findAll(text)
        val ret = ArrayList<String>()
        var offset = 0

        for(i in r) {
            ret.add(str.substring(0,i.index - offset))
            offset = i.index + i.length
            str = text.substring(i.index + i.length)

        }

        ret.add(str)
        return ret.toTypedArray()
    }

    private fun findIndex(text: String) : Pair<Int, Int>{
        var s = 0
        var e = 0

        if(this.matchBegin)
            e = this.matchReg(text)
        else {
            for (i in 0..text.length) {
                e = this.matchReg(text.substring(i))

                if(e != -1){
                    s = i
                    e += s
                    break
                }
            }
        }

        return Pair(s, e)
    }

    private fun matchReg(str : String) : Int{
        return this.begin.matchReg(str)
    }

    internal fun getNewGroupId() : Int{
        val l = this.group.count()
        this.group.add("")
        return l
    }

    internal fun setGroup(id : Int, str : String) {
        this.group[id] = str//maybe out of range
    }

}