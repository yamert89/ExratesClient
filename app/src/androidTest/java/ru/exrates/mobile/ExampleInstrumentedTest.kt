package ru.exrates.mobile

import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import ru.exrates.mobile.logic.entities.CurrencyPair
import ru.exrates.mobile.logic.entities.Exchange
import java.io.*
import java.util.concurrent.ArrayBlockingQueue

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
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
        Log.d("MY", map.toString())
        Log.d("MY", newMap.toString())
        assertEquals(map.size, newMap.size)
    }
}
