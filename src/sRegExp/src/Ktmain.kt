import sRegExp.RegExp

fun main(args: Array<String>) {
    //println("Hello World!")
    val regexp = "[\\w<>\\s/]+"
    val text = "<html><title>Hello World</title></html>"
    println("匹配结果: " + RegExp.findOne(regexp, text))
}