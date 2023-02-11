package com.kseniabl.tasksapp

import org.junit.Test

import org.junit.Assert.*
import kotlin.system.measureTimeMillis

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val time = measureTimeMillis {
            val arr = arrayListOf("str2ing", "tr3y", "bee1", "rty5dfg", "sdf4jyty")
            val indexes = arrayListOf<Int>()
            val realStrings = arrayListOf<String>()

            for (el in arr) {
                var newString = ""
                for (ch in el) {
                    var isInt = false
                    for (i in 1..10000) {
                        if (ch.toString() == "$i") {
                            indexes.add(ch.toString().toInt())
                            isInt = true
                        }
                    }
                    if (!isInt)
                        newString += ch.toString()

                }
                realStrings.add(newString)
            }

            val finStrings = arrayOfNulls<String>(realStrings.size)

            realStrings.forEachIndexed { idx, el ->
                finStrings[indexes[idx]-1] = el
            }

            for (el in finStrings) {
                println(el)
            }
        }
        println(time)
    }
}