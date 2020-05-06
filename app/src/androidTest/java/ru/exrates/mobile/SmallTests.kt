package ru.exrates.mobile

import org.junit.Assert
import org.junit.Test
import ru.exrates.mobile.logic.toNumeric
import ru.exrates.mobile.logic.structures.IntervalComparator
import java.util.*

class SmallTests {

    @Test
    fun double(){
        Assert.assertEquals("0.00000005", 0.00000005.toNumeric())
    }

    @Test
    fun reg(){
        val one = "1h"

        Assert.assertEquals("1", one.split("\\D".toRegex())[0])
    }

    @Test
    fun exponent(){
        val num = 3.85E-4
        val res = Math.getExponent(num)
        Assert.assertEquals(4, res)
    }

    @Test
    fun comparator(){
        val set = TreeSet<String>(IntervalComparator())
        set.add("1h")
        set.add("1M")
        set.add("3m")
        set.add("1w")
        set.add("3h")
        Assert.assertEquals("3m", set.first())
        Assert.assertEquals("1M", set.last())
        val it = set.iterator()
        it.next()
        it.next()
        Assert.assertEquals("3h", it.next())

    }
}