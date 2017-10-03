package sRegExp

class FindResult {
    var success= false
    var index = 0
    var length = 0
    var group = arrayOf("") //所以说，null safety要了命了

    override fun toString(): String {
        if(success){
            return "<SuccessfulFind GroupCount:${this.group.count()}>"
        }
        else{
            return "<FailedFind>"
        }
    }
}