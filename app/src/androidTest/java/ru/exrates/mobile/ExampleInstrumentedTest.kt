package ru.exrates.mobile

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.*
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
import ru.exrates.mobile.logic.entities.json.ExchangePayload
import ru.exrates.mobile.logic.logD
import ru.exrates.mobile.logic.rest.RestService
import java.io.*
import java.time.Duration
import java.util.*
import kotlin.collections.HashMap


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    var bool = false
    val ip = "192.168.0.100"
       // val ip = "192.168.43.114"
        //val ip = "192.168.1.72"
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
        val treeMap = TreeMap<String, Double>()
        treeMap["1m"] = 4453.932
        treeMap["1h"] = 323.4234
        val ex1 = Exchange(
            1,
            "binance",
            mutableListOf(
                CurrencyPair(
                    "BTC",
                    "LTC",
                    "btc_ltc",
                    0.345,
                    treeMap,
                    arrayOf(0L),
                    emptyList(),
                    emptyList(),
                    true,
                    "1",
                    1
                )
            ),
            listOf("1s", "1h"),
            emptyList(),
            true,
            400

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
            val dur = Duration.ofSeconds(30)
            val httpClient = OkHttpClient.Builder()
                .connectTimeout(dur)
                .readTimeout(dur)
                .writeTimeout(dur).build()
            val om = ObjectMapper()
            om.registerKotlinModule()
            val retrofit = Retrofit.Builder()
                .client(httpClient)
                .baseUrl("http://$ip:8080/")
                .addConverterFactory(JacksonConverterFactory.create(om))
                .build()
            val restService = retrofit.create(RestService::class.java)
            //val payload = """{"exchange": "binanceExchange", "timeout" : 12, "pairs":["VENBTC"]}"""
            val payload = ExchangePayload(
                1,
                "1h",
                arrayOf("VENBTC")
            )
            //log_d( payload.toString())
            val call: Call<Exchange> = restService.getExchange(payload)
            val response = call.execute()
            logD("!!!" + response.body())
            val exchange = response.body()

            assertEquals(200, response.code())
            assertNotNull(exchange)
            assertEquals("binanceExchange", exchange?.name)

        }catch (e: Exception){
            Log.e("Exrates", e.message ?: "no message")
            e.printStackTrace(System.err)
            assertEquals(1,2)
        }

    }


    @Test
    fun restAsyncTest(){
        try{
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging).build()
            val om = ObjectMapper()
            om.registerKotlinModule()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://$ip:8080/")
                .addConverterFactory(JacksonConverterFactory.create(om))
                .client(client)
                .build()
            val restService = retrofit.create(RestService::class.java)
            val payload = """{"exchange": "binanceExchange", "timeout" : "1h", "pairs":["VENBTC"]}"""
            val payload2 = ExchangePayload(1, "1h", arrayOf("VENBTC"))
            //log_d( payload)
            val call: Call<Exchange> = restService.getExchange(payload2)
            val some = Some()
            call.enqueue(some)
           //assertEquals(true, bool)
            Thread.sleep(5000)
            logD("end test body")
            assertTrue(some.bool)
        }catch (e: Exception){
            Log.e("Exrates", e.message ?: "null message")
        }

    }

    class Some: Callback<Exchange>{
        var bool: Boolean = false

        override fun onFailure(call: Call<Exchange>, t: Throwable) {
            t.printStackTrace()
            throw IllegalStateException("Connection failed")
        }

        override fun onResponse(call: Call<Exchange>, response: Response<Exchange>) {
            logD(
                "Async response success: ${response.body()}, code: ${response.code()} ," +
                        " message: ${response.message()}, callIsExecuted: ${call.isExecuted} , error: ${response.errorBody()
                            .toString()}"
            )
            logD(
                "call: " + ObjectMapper().writeValueAsString(
                    call
                )
            )
            logD(
                "response: " + ObjectMapper().writeValueAsString(
                    response
                )
            )
            bool = true

        }

    }





    /*class LoggingIntercepror: Interceptor{
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val request = chain.request()
            log_d( "REQUEST: ${request.url} ${chain.connection()} ${request.headers}")
            val response = chain.proceed(request)
            log_d( "RESPONSE: ${response.request.url}, ${response.headers}")
            return response
        }

    }*/



    /*
   Request:
   {
   "exchange" : "binanceExchange",
   "timeout": "3m",
   "pairs" : ["BTCUSDT", "ETCBTC"]
   }



   Response:
   {
   "changePeriods":["3m","5m","15m","30m","1h","4h","6h","8h","12h","1d","3d","1w","1M"],
   "name":"binanceExchange",
   "pairs":[
       {
           "symbol":"ERDPAX",
           "price":0.0012527,
           "priceChange":{
               "\"4h\"":0.0012527,
               "\"6h\"":0.0012527,
               "\"30m\"":0.0012527,
               "\"1M\"":0.00177985,
               "\"1d\"":0.0012527,
               "\"1h\"":0.0012527,
               "\"12h\"":0.0012527,
               "\"3d\"":0.00141975,
               "\"8h\"":0.0012527,
               "\"5m\"":0.0012527,
               "\"1w\"":0.0012527,
               "\"15m\"":0.0012527,
               "\"3m\"":0.0012527
               },
               "priceHistory":[],
               "lastUse":"2019-12-05T13:08:21.932122600Z",
               "updateTimes":[1575551295166,0,0]
        }
     ]
    }

   */




}
