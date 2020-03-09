package ru.exrates.mobile

import org.junit.Assert
import org.junit.Test

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
}