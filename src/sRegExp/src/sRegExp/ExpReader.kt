package sRegExp

class ExpReader constructor(exp : String) {
    private var exp : String = ""
    private var position : Int = 0

    init {
        this.exp = exp
    }

    private fun size() :Int{
        return exp.length
    }

    fun read() : Char {
        if(this.position <= this.size() - 1)
            return exp[position++]
        else
            throw Exception("Reader has reached the end.")
    }

    fun peek() : Char? {
        if(this.end()) return null
        return exp[position]
    }

    fun peek(num : Int) : Char?{
        return if(position + num - 1 < this.size()) exp[position + num - 1] else null//要命了
    }

    fun reset() {
        this.position = 0
    }

    fun skip(count : Int) {
        this.position += count
    }

    fun end() : Boolean {
        return this.position >= this.size()
    }

    fun begin() : Boolean {
        return this.position == 0
    }

    fun readPair() : String? { //Read () 仿佛自己不会写代码
        var res = ""
        val pos = this.position
        var b = 0

        while(!this.end()) {
            val ch = this.read()
            when(ch){
                '(' -> ++b
                ')' -> --b
            }
            res += ch
            if(b == 0){
                return res
            }
        }

        this.position = pos
        return null
    }

    fun readUntil(c : Char) : String? {
        var res = ""
        val pos = this.position

        while(!this.end()){
            val ch = this.read()
            res += ch
            if(ch == c){
                return res
            }
        }

        this.position = pos
        return null

    }


}