package sRegExp.nodes

import sRegExp.CapturePair
import sRegExp.CompiledRegExp
import sRegExp.ExpReader

class CharMatch(re : CompiledRegExp) : MatchNode(re) {

    inner class MatchPattern {
        var type : Int = 0 //0 - single 1 - range 2 - any(.)
        var single : Char =  '\\' //complex type要了我的命
        //range
        var rangeFrom : Int = 0
        var rangeTo : Int = 0
        var except : Boolean = false

        fun match(c : Char): Boolean{
            val result : Boolean
            when(type){
                0 -> result = c == single
                1 -> result = (c.toInt() in rangeFrom..rangeTo)
                2 -> result = true
                else -> throw Exception("Unknown match type.")
            }

            return if(except) !result else result
        }

        override fun toString(): String {
            var str = ""
            when(type){
                0 -> str = "<SingleMatch Single:{$single}"
                1 -> str = "<RangeMatch From {$rangeFrom} to {$rangeTo}"
                2 -> str = "<AnyMatch"
            }

            str += " Except {$except}>"
            return str
        }

    }
    //MatchPatterns
    private var matchCollection = ArrayList<MatchPattern>()
    //Match amount
    private var max : Int = 1
    private var min : Int = 1
    private var greedy : Boolean = true //?
    private var nomax : Boolean = false //+

    override fun match(str : String) : ArrayList<Pair<Int, ArrayList<CapturePair>>> {
        val r = ArrayList<Pair<Int, ArrayList<CapturePair>>>()
        var len  = 1
        if(min == 0) r.add(Pair(0, ArrayList()))
        for(i in str){
            if(this.matchCollection.any { it.match(i) }) {
                var breaksign = false
                when {
                    len < min -> {
                        ++len
                    }

                    len in min..max || nomax-> {
                        r.add(Pair(len++, ArrayList()))
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

        if(!greedy) r.reverse()
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
                val t = sr.readUntil(']') ?: throw Exception("Syntax error.")
                val reader = ExpReader(t)
                var except = false

                if(reader.peek() == '^') except = true

                while(reader.peek() != ']'){
                    val ch = reader.read()
                    val mp = MatchPattern()
                    when(ch){

                        '-' -> throw Exception("Syntax Error.")

                        '\\' -> { //escaped character
                            mpl.addAll(this.getEscapedPatterns(reader.read()))
                        }

                        else -> {
                            if(reader.peek() == '-'){
                                reader.read()
                                mp.type = 1 // range
                                mp.rangeFrom = ch.toInt()
                                mp.rangeTo = reader.read().toInt()
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
                    this.max = t.toInt()
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
                mp.rangeFrom = 'A'.toInt()
                mp.rangeTo = 'Z'.toInt()
                patterns.add(mp)

                mp = MatchPattern()
                mp.type = 1
                mp.rangeFrom = 'a'.toInt()
                mp.rangeTo = 'z'.toInt()
                patterns.add(mp)

                mp = MatchPattern()
                mp.type = 1
                mp.rangeFrom = '0'.toInt()
                mp.rangeTo = '9'.toInt()
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
                val a = getEscapedPatterns('s')
                a.forEach { it.except = true }
                patterns.addAll(a)
            }

            'W' -> {//除/w外
                val a = getEscapedPatterns('w')
                a.forEach { it.except = true }
                patterns.addAll(a)
            }

            'd' -> {//数字
                val mp = MatchPattern()
                mp.type = 1
                mp.rangeFrom = '0'.toInt()
                mp.rangeTo = '9'.toInt()
                patterns.add(mp)
            }

            '(',')','{','}','[',']' -> { //各种特殊字符
                val mp = MatchPattern()
                mp.type = 0
                mp.single = c
                patterns.add(mp)
            }
        }

        return patterns
    }

    override fun toString(): String {
        return "<CharMatch {Pattern:{${this.matchCollection}}} Min:{${this.min}} Max:{${this.max}}} Nomax:{${this.nomax}} Greedy {${this.greedy}}>"
    }
}