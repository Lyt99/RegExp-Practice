package sRegExp

internal class CapturePair constructor(id : Int, str : String){
    var id  = id
    var str = str

    override fun toString(): String {
        return "<CapturePair id = $id, str = \"$str\">"
    }
}