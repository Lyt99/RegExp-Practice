import sRegExp.CompiledRegExp
import sRegExp.RegExp

fun main(args: Array<String>) {
    //println("Hello World!")
    //TODO: ()后+ * ? {}等实现，不过怕是很难了
    //TODO: 代码太难看了，自己都快看不懂了，等待重构

    val regexp = "1((\\d{2,3})(\\d+))"
    //val regexp = "(.....)"
    val text = "123456"

    val res = RegExp.findOne(regexp, text)
    println("RegExp: $regexp")
    if(res.success) {
        print("匹配成功，group为: ")
        for(i in res.group) {
            print("'$i' ")
        }
        println()
    }
    else {
        println("匹配失败 ")
    }




    //val regexp = "</*\\w+?>"

    //val text = "c<html>a<title>Hello World!</html>b</title>d"
    //println(text)
    //val cre = CompiledRegExp(regexp)

    //for(i in cre.split(text))
    //println("r:" + i)



}

