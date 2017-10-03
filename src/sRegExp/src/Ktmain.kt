import sRegExp.RegExp

fun main(args: Array<String>) {
    //println("Hello World!")
    //TODO: 多个()嵌套似乎还是有问题
    val regexp = "(..(.+)..)"
    //val regexp = "(.....)"
    val text = "abcdefghijklmn"

    val res = RegExp.findAll(regexp, text)

    for(i in res)
        println("捕获内容: ${i.group[1]} ${i.group[2]}")

}