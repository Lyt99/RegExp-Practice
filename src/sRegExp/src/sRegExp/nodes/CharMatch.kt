package sRegExp.nodes

import sRegExp.ExpReader
import java.util.regex.Pattern

class CharMatch : MatchNode() {
    inner class MatchPattern {
        var type : Int = 0 //0 - single 1 - range 2 - any(.)
        var single : Char =  '\\' //complex type要了我的命
        //range
        var range_from : Int = 0
        var range_to : Int = 0
        var except : Boolean = false

        fun match(c : Char): Boolean{
            var result : Boolean
            when(type){
                0 -> result = c == single
                1 -> result = (c.toInt() in range_from..range_to)
                2 -> result = true
                else -> throw Exception("Unknown match type.")
            }

            return if(except) !result else result
        }

    }
    //MatchPatterns
    private var matchCollection = ArrayList<MatchPattern>()
    //Match amount
    private var max : Int = 1
    private var min : Int = 1
    private var greedy : Boolean = true //?
    private var nomax : Boolean = false //+

    override fun match(str : String, pos : Int) : ArrayList<Int>{
        val r = ArrayList<Int>()
        var len  = 1
        if(min == 0) r.add(0)
        for(i in str){
            if(this.matchCollection.any { it.match(i) }) {
                //好丑
                //可能逻辑错误
                /*
                if (len < min) ++len
                else if (nomax) r.add(len++)
                else if (len in min..max) r.add(len++)
                else break*/

                var breaksign = false
                when {
                    len < min -> {
                        ++len
                    }

                    len in min..max -> {
                        r.add(len++)
                    }

                    nomax -> {
                        r.add(len++)
                    }

                    else -> {
                        breaksign = true//我也很绝望啊
                    }
                }
                if(breaksign) break
            }
            else
                break
        }

        //println(r)
        return r
    }

    //需要处理的格式
    //+ +?
    override fun init(sr : ExpReader) : MatchNode {
        val mpl = ArrayList<MatchPattern>()
        val c = sr.read()

        when(c){
            '.' ->{
                val mp = MatchPattern()
                mp.type = 2
                mpl.add(mp)

            }
            '[' -> {
                var t = sr.readUntil(']')
                if(t == null) throw Exception("Syntax error.")//elvis???
                var reader = ExpReader(t)
                var except = false

                if(reader.peek() == '^') except = true

                while(reader.peek() != ']'){
                    var ch = reader.read()
                    var mp = MatchPattern()
                    when(ch){

                        '-' -> throw Exception("Syntax Error.")

                        '\\' -> { //escaped character
                            mpl.addAll(this.getEscapedPatterns(reader.read()))
                        }

                        else -> {
                            if(reader.peek() == '-'){
                                reader.read()
                                mp.type = 1 // range
                                mp.range_from = ch.toInt()
                                mp.range_to = reader.read().toInt()
                                mp.except = except
                                mpl.add(mp)
                            }
                            else {
                                mp.single = ch
                                mp.except = except
                                mpl.add(mp)
                            }
                        }
                    }
                }

            }

            '\\' -> {
                    mpl.addAll(getEscapedPatterns(sr.read()))
            }

            else -> {
                val mp = MatchPattern()
                mp.single = c
                mpl.add(mp)
            }
        }

        this.matchCollection.addAll(mpl)

        //可选参数(???)
        when(sr.peek()){
            '*' -> {
                this.nomax = true
                this.min = 0
                sr.read()
            }

            '+' ->{
                this.nomax = true
                this.min = 1
                sr.read()
            }

            '?' ->{
                this.min = 0
                sr.read()
            }

            '{' -> {
                //很难看，但是先这样吧
                var t = sr.readUntil('}')
                t = t?.substring(1, t.length - 1) ?:throw Exception("Syntax Error: {$t}")
                if(!t.contains(',')) {
                    this.min = t.toInt()
                    this.nomax = true
                }
                else{
                    val i = t.split(',')
                    if(i.count() != 2) throw Exception("Syntax Error: {$t}")
                    this.min = i[0].toInt()
                    if(i[1].isEmpty())
                        this.nomax = true
                    else {
                        this.max = i[1].toInt()
                        if (this.max < this.min) Exception("Syntax Error: {$t}")
                    }
                }
                //sr.read()
            }

        }

        if(sr.peek() == '?') {
            this.greedy = false
            sr.read()
        }
        return this
    }

    private fun getEscapedPatterns(c : Char) : ArrayList<MatchPattern>{
        val patterns = ArrayList<MatchPattern>()
        //死难看的硬编码时间
        when(c){
            'w' -> //[A-Za-z0-9_]
            {

                var mp = MatchPattern()
                mp.type = 1
                mp.range_from = 'A'.toInt()
                mp.range_to = 'Z'.toInt()
                patterns.add(mp)

                mp = MatchPattern()
                mp.type = 1
                mp.range_from = 'a'.toInt()
                mp.range_to = 'z'.toInt()
                patterns.add(mp)

                mp = MatchPattern()
                mp.type = 1
                mp.range_from = '0'.toInt()
                mp.range_to = '9'.toInt()
                patterns.add(mp)

                mp = MatchPattern()
                mp.type = 0
                mp.single = '_'
                patterns.add(mp)
            }

            's' -> {// \f\n\r\t\v 然而，并没有\f和\v
                var mp = MatchPattern()
                mp.type = 0
                mp.single = '\n'
                patterns.add(mp)

                mp = MatchPattern()
                mp.type = 0
                mp.single = '\r'
                patterns.add(mp)

                mp = MatchPattern()
                mp.type = 0
                mp.single = '\t'
                patterns.add(mp)

                mp = MatchPattern()
                mp.type = 0
                mp.single = ' '
                patterns.add(mp)
            }

            'S' -> {//除/s外
                var a = getEscapedPatterns('s')
                a.forEach { it.except = true }
                patterns.addAll(a)
            }

            'W' -> {//除/w外
                var a = getEscapedPatterns('w')
                a.forEach { it.except = true }
                patterns.addAll(a)
            }
        }

        return patterns
    }

    override fun toString(): String {
        return "<CharMatch {Pattern:{${this.matchCollection.count()}}} Min:{${this.min}} Max:{${this.max}}} Nomax:{${this.nomax}} Greedy {${this.greedy}}>"
    }
}