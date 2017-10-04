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

    fun replace(regexp : String, text:String, rep : String) : String {
        var r = CompiledRegExp(regexp)
        return r.replace(text, rep)
    }

    fun replace(regexp : String, text : String, func : (r : String) -> String) : String {
        var r = CompiledRegExp(regexp)
        return r.replace(text, func)
    }

    fun replaceOne(regexp : String, text : String, rep : String) : String {
        var r = CompiledRegExp(regexp)
        return r.replace(text, rep)
    }

    fun replaceOne(regexp : String, text : String, func : (r : String) -> String) : String {
        var r = CompiledRegExp(regexp)
        return r.replaceOne(text, func)
    }

    fun split(regexp : String, text : String) : Array<String> {
        var r = CompiledRegExp(regexp)
        return r.split(text)
    }
}


