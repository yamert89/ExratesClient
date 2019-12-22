package ru.exrates.mobile

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Coroutines {

    @Test
    fun coroutine1() = runBlocking{
        launch { // launch a new coroutine in background and continue
            delay(1000L)
            println("World")


        }
        println("Hello,")

    }

    suspend fun del(): String{
        return suspendCoroutine { continuation -> {
            //runBlocking { delay(3000L) }
            Thread.sleep(3000)
            continuation.resume("fuck")
        } }
    }
}