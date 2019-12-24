package ru.exrates.mobile

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.junit.Test
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import java.util.*
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

    @Test
    fun timer(){
        var timer = Timer()
        val job = GlobalScope.launch(Dispatchers.Default) {
           val job2 = launch {
                timer.schedule(object :  TimerTask(){
                    override fun run() {
                        log_d("tic....")
                    }
                }, 5000L, 1000L)
            }




        }
        runBlocking(Dispatchers.Unconfined) {
            job.join()
            timer.cancel()
            timer = Timer()
            timer.schedule(object : TimerTask(){
                override fun run() {
                    log_d("tak...")
                }
            }, 1000)
        }








    }
}