package sRegExp

object RegExp{
    fun match(regexp : String, text : String) : Boolean{
        var r = CompiledRegExp(regexp)
        return r.match(text)
    }

    fun findOne(regexp : String, text : String) : FindResult{
        var r = CompiledRegExp(regexp)
        return r.findOne(text)
    }

    fun findAll(regexp : String, text : String) : Array<FindResult>{
        var r = CompiledRegExp(regexp)
        return r.findAll(text)
    }
}


