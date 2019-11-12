package ru.exrates.mobile

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify
import org.junit.Test

import org.junit.Assert.*
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import java.util.concurrent.ArrayBlockingQueue

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @UseExperimental(ImplicitReflectionSerializer::class)
    @Test
    fun serialize(){
        val ex1 = Exchange(
            "testExchange",
            mutableListOf(
                CurrencyPair(
                    "btc_ltc",
                    0.345,
                    mapOf("1m" to 4453.023, "1h" to 5433.3230),
                    ArrayBlockingQueue(2)
                )
            ),
            listOf("1s", "1h")
        )

        val ex2 = Exchange(
            "testExchange 2",
            mutableListOf(
                CurrencyPair(
                    "btc_ltc",
                    0.657,
                    mapOf("1m" to 4343.023, "1h" to 45333.3230),
                    ArrayBlockingQueue(2)
                )
            ),
            listOf("1s", "1h")
        )

        val s = Json.stringify(Exchange.serializer(), ex1)
        println(s)

    }

    @Test
    fun jacksonSerialize(){
        val om = ObjectMapper().registerKotlinModule()
        val map = HashMap<String, Exchange>()
        val ex1 = Exchange(
            "testExchange",
            mutableListOf(
                CurrencyPair(
                    "btc_ltc",
                    0.345,
                    mapOf("1m" to 4453.023, "1h" to 5433.3230),
                    ArrayBlockingQueue(2)
                )
            ),
            listOf("1s", "1h")
        )
        map["q"] = ex1
        val s = om.writeValueAsString(map)
        println(s)
        val map2: Map<String, Exchange> = om.readValue(s, object: TypeReference<Map<String, Exchange>>(){})
        println(map2)
    }
}
