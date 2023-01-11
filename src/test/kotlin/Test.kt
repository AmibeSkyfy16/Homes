import kotlin.math.ln
import kotlin.test.Test

class Test {

    fun log2(n: Int): Double {
        return ln(n.toDouble()) / ln(0.7)
    }

    @Test
    fun test(){


        println(log2((10)))
        println(log2((100)))
        println(log2((1000)))
        println(log2((2000)))
        println(log2((6000)))

//        val str1 = "homes.commands.*"
//        val str2 = "homes.commands.homes.create"
//
//        if(str2.contains(str1.substringBeforeLast('*').substringBeforeLast('.'))) println(true)
    }

}