import sRegExp.CompiledRegExp
import sRegExp.RegExp

fun main(args: Array<String>) {
    //println("Hello World!")
    //TODO: 已确认多个()嵌套有问题，且为设计架构问题，准备重构吧(惊恐

    /*
    val regexp = "(1(\\d+))[0-9]{8}"
    //val regexp = "(.....)"
    val text = "18677777777"

    val res = RegExp.findOne(regexp, text)
    println("RegExp: $regexp")
    if(res.success) {
        println("是正确电话号码, 且前三位为${res.group[1]}, 前三位的后两位是${res.group[2]}")
    }
    else {
        println("不是")
    }
    */

    val regexp = "</*\\w+?>"
    val text = "<html>a<title>Hello World!</html>b</title>"
    println(text)
    val cre = CompiledRegExp(regexp)

    for(i in cre.split(text))
        println("r:" + i)


}

