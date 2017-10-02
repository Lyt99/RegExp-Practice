package sRegExp

object RegExp{
    fun match(regexp : String, text : String) : Boolean{
        var r = CompiledRegExp(regexp)
        return r.match(text)
    }

    fun findOne(regexp : String, text : String) : String?{
        var r = CompiledRegExp(regexp)
        return r.findOne(text)
    }
}


