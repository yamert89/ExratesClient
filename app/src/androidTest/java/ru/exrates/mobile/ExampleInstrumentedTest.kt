package ru.exrates.mobile

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import ru.exrates.mobile.logic.rest.ExchangePayload
import ru.exrates.mobile.logic.rest.RestService
import java.io.*
import java.time.Duration
import java.util.concurrent.ArrayBlockingQueue

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    var bool = false
    //val ip = "192.168.0.100"
        val ip = "192.168.1.72"
    lateinit var context: Context
    @Before
    fun init(){
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("ru.exrates.mobile", appContext.packageName)
    }

    @Test
    fun javaSerializable(){
        val os = ObjectOutputStream(FileOutputStream(File(context.filesDir, "exchanges")))

        val map = HashMap<String, Exchange>()
        val ex1 = Exchange(
            "testExchange", 5,
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

        os.writeObject(map)
        os.flush()
        os.close()
        val _is = ObjectInputStream(FileInputStream(File(context.filesDir, "exchanges")))
        val newMap = _is.readObject() as Map<String, Exchange>
        _is.close()
        //Log.d("MY", map.toString())
        //Log.d("MY", newMap.toString())
        assertEquals(map.size, newMap.size)
    }

    /*@Test
    fun restSyncTest(){
        try{
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ip:8080/")
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
        val restService = retrofit.create(RestService::class.java)
        val call: Call<Map<String, Exchange>> = restService.getExchanges("""{"exchange": "binanceExchange", "timeout" : 12, "pairs":["VENBTC"]}""")
        val exch: Exchange? = call.execute().body()?.get("binanceExchange")
            println(exch.toString())
        assertNotNull(exch)
        assertEquals("binanceExchange", exch?.name)
        }catch (e: Exception){
            e.printStackTrace()
        }

    }*/

    @Test
    fun restSyncTest(){
        try{
            val dur = Duration.ofSeconds(300)
            val httpClient = OkHttpClient.Builder()
                .connectTimeout(dur)
                .readTimeout(dur)
                .writeTimeout(dur).build()
            val retrofit = Retrofit.Builder()
                .client(httpClient)
                .baseUrl("http://$ip:8080/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
            val restService = retrofit.create(RestService::class.java)
            //val payload = """{"exchange": "binanceExchange", "timeout" : 12, "pairs":["VENBTC"]}"""
            val payload = ExchangePayload("binanceExchange", "1h", arrayOf("VENBTC"))
            //Log.d("Exrates", payload.toString())
            val call: Call<Exchange> = restService.getExchange(payload)

            val response = call.execute()
            Log.d("Exrates", "!!!" + response.raw().message())
            val exchange = response.body()
            assertEquals(200, response.code())
            assertNotNull(exchange)
            assertEquals("binanceExchange", exchange?.name)
            assertEquals(1, exchange?.id)
        }catch (e: Exception){
            Log.e("Exrates", e.message ?: "no message")
            e.printStackTrace()
            assertEquals(1,2)
        }

    }


    @Test
    fun restAsyncTest(){
        try{
            val retrofit = Retrofit.Builder()
                .baseUrl("http://$ip:8080/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
            val restService = retrofit.create(RestService::class.java)
            val payload = """{"exchange": "binanceExchange", "timeout" : 12, "pairs":["VENBTC"]}"""
            //Log.d("Exrates", payload)
            val call: Call<Exchange> = restService.getExchange(payload)
            call.enqueue(Some())
           //assertEquals(true, bool)
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    class Some: Callback<Exchange>{
        var bool: Boolean = false

        override fun onFailure(call: Call<Exchange>, t: Throwable) {
            t.printStackTrace()
            throw IllegalStateException("Connection failed")
        }

        override fun onResponse(call: Call<Exchange>, response: Response<Exchange>) {
            bool = true
        }

    }




}
