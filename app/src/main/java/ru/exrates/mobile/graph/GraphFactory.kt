package ru.exrates.mobile.graph

import android.graphics.Color
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.view.LineChartView
import ru.exrates.mobile.log_d
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class GraphFactory(private val anyChartView: LineChartView, val currentInterval: String) {


    fun createSmallGraph(priceHistory: List<Double>){
        val valueDataList = createChartValueDataList(priceHistory)
        val line = Line(valueDataList.values).setColor(Color.RED).setCubic(true)
        //line.isSquare = false
        line.strokeWidth = 1
        line.setHasPoints(false)
        //line.isSquare = true
        line.isFilled = true
        createGraph(valueDataList, line)
    }

    fun createBigGraph(priceHistory: List<Double>) {
        val valueDataList = createChartValueDataList(priceHistory)
        val line = Line(valueDataList.values).setColor(Color.RED).setCubic(true)
        line.isSquare = true
        line.setHasPoints(false)
        createGraph(valueDataList, line)
    }

    private fun createGraph(valueDataList: ValueDataList, line: Line){

        log_d(valueDataList.values.joinToString { "x: ${it.x} y: ${it.y}" })
        //line.setHasPoints(false)
        val lineChartData = LineChartData(mutableListOf(line))
        lineChartData.isValueLabelBackgroundEnabled = true
        lineChartData.axisYLeft = Axis.generateAxisFromCollection(valueDataList.yAxisLabelValues, valueDataList.yLabels)
        lineChartData.axisXBottom = Axis.generateAxisFromCollection(valueDataList.xAxisLabelValues, valueDataList.xLabels)

        anyChartView.isValueSelectionEnabled = true



        //lineChartData.axisYLeft.name = "Price"
        lineChartData.axisXBottom.name = valueDataList.xAxisLabel
        lineChartData.axisYLeft.maxLabelChars = 10
        lineChartData.axisYLeft.textSize = 10
        lineChartData.axisXBottom.textSize = 10

        /* val v = Viewport(*//*anyChartView.maximumViewport*//*)
        *//*v.bottom = 0f
        v.top = 200f
        v.left = 100f
        v.right = 200f
        anyChartView.maximumViewport = v
        anyChartView.currentViewport = v*/

        anyChartView.lineChartData = lineChartData
        //anyChartView.contentDescription = "dfdf"
    }

    private fun createChartValueDataList(priceHistory: List<Double>): ValueDataList {
        log_d("current interval = $currentInterval")
        var dateInterval = Duration.ZERO
        var pattern = "HH:mm"
        var xLabel = "time"
        val regExp = "(\\d{1,2})(\\D)".toRegex()
        val groups = regExp.find(currentInterval)?.groups ?: throw NullPointerException("Regexp is null")
        val numToken = groups[1]!!.value.toLong()
        var offsetTimeUnit = ChronoUnit.MINUTES
        var numberOfDateIntervals = 5 //empiric

        when(groups[2]!!.value){
            "m" -> dateInterval = Duration.ofMinutes(numToken)
            "h" -> {
                dateInterval = Duration.ofHours(numToken)
                offsetTimeUnit = ChronoUnit.HOURS
                pattern = if(numToken == 1L) "HH:mm" else "dd LLL HH:mm"
                numberOfDateIntervals = 4
            }
            "d" -> {
                dateInterval = Duration.ofDays(numToken)
                pattern = "dd"
                xLabel = "dates"
                numberOfDateIntervals = 10
            }
            "w" -> {
                dateInterval = Duration.ofDays(7)
                pattern = "dd LLL"
                xLabel = "dates"
            }
            "M" -> {
                dateInterval = Duration.ofDays(30)
                pattern = "LLL"
                xLabel = "months"
            }
        }

        var now = ZonedDateTime.now(ZoneId.systemDefault())

        var start = ZonedDateTime.now(ZoneId.systemDefault())
        start.withMinute(0)
        start = start.withMinute(start.minute - start.minute % 5)
        val dataList = ArrayList<PointValue>()
        val labelList = ArrayList<String>()
        val labelValueList = ArrayList<Float>()
        for (element in priceHistory){
            now = now.minus(dateInterval)
            val x = now.toEpochSecond().toFloat()
            dataList.add(0, PointValue(x, element.toFloat()))
        }

        val duration = Duration.between(now, start)
        val seconds = duration.seconds
        val interval = Duration.of(seconds / numberOfDateIntervals, ChronoUnit.SECONDS)

        while(start > now){
            start = start.truncatedTo(offsetTimeUnit)
            start -= interval
            labelList.add(start.format(DateTimeFormatter.ofPattern(pattern)))
            labelValueList.add(start.toEpochSecond().toFloat())
        }
        log_d("price history values: ${if(priceHistory.isEmpty()) "0" else priceHistory.joinToString()}")
        val maxYL = priceHistory.max()!!.toFloat() //todo round values todo - offset min and max
        val minYL = priceHistory.min()!!.toFloat()
        val midYL = maxYL.minus((maxYL.minus(minYL)) / 2)

        val yLabels = listOf(minYL.toString(), midYL.toString(), maxYL.toString())
        val yAxisLabelValues = listOf(minYL, midYL, maxYL)


        log_d("labels size: ${labelList.size}  l. values size: ${labelValueList.size}")
        log_d("labels X: ${labelList.joinToString()}")
        log_d("label values X: ${labelValueList.joinToString()}")
        log_d("labels Y: ${yLabels.joinToString()}")
        log_d("label values Y ${yAxisLabelValues.joinToString()}")

        return ValueDataList(
            dataList,
            labelList,
            labelValueList,
            xLabel,
            yLabels,
            yAxisLabelValues
        )

    }

    data class ValueDataList(
        val values: List<PointValue>,
        val xLabels: List<String>,
        val xAxisLabelValues: List<Float>,
        val xAxisLabel: String,
        val yLabels: List<String>,
        val yAxisLabelValues: List<Float>)

}