package ru.exrates.mobile

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert.assertNotNull
import org.junit.Assert.assertEquals
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
import ru.exrates.mobile.logic.rest.RestService
import java.io.*
import java.util.concurrent.ArrayBlockingQueue

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    var bool = false
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

    @Test
    fun restSyncTest(){
        try{
        val retrofit = Retrofit.Builder()
            .baseUrl("http://enchat.ru:8080/")
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
        val restService = retrofit.create(RestService::class.java)
        val call: Call<Map<String, Exchange>> = restService.getExchanges("{\"exchange\": \"binanceExchange\", \"timeout\" : 12, \"pairs\":[\"VENBTC\"]}")
        val exch: Exchange? = call.execute().body()?.get("binanceExchange")
            println(exch.toString())
        assertNotNull(exch)
        assertEquals("binanceExchange", exch?.name)
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    @Test
    fun restAsyncTest(){
        try{
            val retrofit = Retrofit.Builder()
                .baseUrl("http://enchat.ru:8080/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
            val restService = retrofit.create(RestService::class.java)
            val call: Call<Exchange> = restService.getExchange("{\"exchange\": \"binanceExchange\", \"timeout\" : 12, \"pairs\":[\"VENBTC\"]}")
            call.enqueue(Some())
           assertEquals(true, bool)
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    class Some: Callback<Exchange>{
        var bool: Boolean = false

        override fun onFailure(call: Call<Exchange>, t: Throwable) {
            throw IllegalStateException(call.request().body().toString())
        }

        override fun onResponse(call: Call<Exchange>, response: Response<Exchange>) {
            bool = true
        }

    }




}
