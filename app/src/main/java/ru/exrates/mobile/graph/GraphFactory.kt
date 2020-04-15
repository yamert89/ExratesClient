package ru.exrates.mobile.graph

import android.graphics.Color
import lecho.lib.hellocharts.model.*
import lecho.lib.hellocharts.view.LineChartView
import ru.exrates.mobile.log_d
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

//fixme gasbtc 4h y axis not showed
class GraphFactory(private val anyChartView: LineChartView, val currentInterval: String) {


    fun createSmallGraph(priceHistory: List<Double>){
        val valueDataList = createChartValueDataList(priceHistory)
        val line = Line(valueDataList.values).apply {
            color = Color.RED
            isCubic = true
            isSquare = true
            strokeWidth = 1
            setHasPoints(false)
            isFilled = true
        }

        createGraph(valueDataList, line)
    }

    fun createBigGraph(priceHistory: List<Double>) {
        val valueDataList = createChartValueDataList(priceHistory)
        val line = Line(valueDataList.values).setColor(Color.RED).setCubic(true)
        line.isSquare = true
        line.setHasPoints(true)
        line.pointRadius = 3
        line.shape = ValueShape.SQUARE


        createGraph(valueDataList, line)
    }

    private fun createGraph(valueDataList: ValueDataList, line: Line){
        //anyChartView.isViewportCalculationEnabled = false



        log_d(valueDataList.values.joinToString { "x: ${it.x} y: ${it.y}" })
        //line.setHasPoints(false)
        val lineChartData = LineChartData(mutableListOf(line))
        with(lineChartData){
            isValueLabelBackgroundEnabled = true
            axisXBottom = Axis.generateAxisFromCollection(valueDataList.xAxisLabelValues, valueDataList.xLabels)
            axisXBottom.setHasLines(true)
            if (valueDataList.yAxisLabelValues != null){
                axisYLeft = Axis.generateAxisFromCollection(valueDataList.yAxisLabelValues, valueDataList.yLabels)
            } else {
                axisYLeft = Axis()
                axisYLeft.isAutoGenerated = true
            }
            axisYLeft.setHasLines(true)
        }

        //lineChartData.axisYLeft.name = "Price"
        lineChartData.axisXBottom.name = valueDataList.xAxisLabel
        lineChartData.axisYLeft.maxLabelChars = 10
        lineChartData.axisYLeft.textSize = 10
        lineChartData.axisXBottom.textSize = 10

         val v = Viewport(anyChartView.maximumViewport)
        v.bottom = valueDataList.values.last().y + 0.1f
        v.top = valueDataList.values.first().y - 0.1f
        anyChartView.maximumViewport = v
        anyChartView.currentViewport = v

        anyChartView.lineChartData = lineChartData
        anyChartView.isValueSelectionEnabled = true

        //anyChartView.setHa
        //anyChartView.contentDescription = "dfdf"
    }

    private fun createChartValueDataList(priceHistory: List<Double>): ValueDataList {
        log_d("current interval = $currentInterval")
        var dateInterval = Duration.ZERO
        var pattern = "HH:mm"
        var xLabel = ""
        val regExp = "(\\d{1,2})(\\D)".toRegex()
        val groups = regExp.find(currentInterval)?.groups ?: throw NullPointerException("Regexp is null")
        val numToken = groups[1]!!.value.toLong()
        var offsetTimeUnit = ChronoUnit.MINUTES
        var numberOfDateIntervals = 5 //empiric
        var autoGeneratedY = true

        when(groups[2]!!.value){
            "m" -> {
                dateInterval = Duration.ofMinutes(numToken)
                autoGeneratedY = false
            }
            "h" -> {
                if (numToken == 1L) autoGeneratedY = false
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

        var now = LocalDateTime.now()

        var start = LocalDateTime.now()
        start.withMinute(0)
        start = start.withMinute(start.minute - start.minute % 5)
        val dataList = ArrayList<PointValue>()
        val labelList = ArrayList<String>()
        val labelValueList = ArrayList<Float>()
        for (i in priceHistory.lastIndex downTo 0){
            now = now.minus(dateInterval)
            val x = now.toEpochSecond(ZoneOffset.UTC).toFloat()
            dataList.add(0, PointValue(x, priceHistory[i].toFloat()))
        }

        val duration = Duration.between(now, start)
        val seconds = duration.seconds
        val interval = Duration.of(seconds / numberOfDateIntervals, ChronoUnit.SECONDS)

        while(start > now){
            start = start.truncatedTo(offsetTimeUnit)
            start -= interval

            labelList.add(start.format(DateTimeFormatter.ofPattern(pattern)))

            labelValueList.add(start.toEpochSecond(ZoneOffset.UTC).toFloat())

        }
        log_d("price history values: ${if(priceHistory.isEmpty()) "0" else priceHistory.joinToString()}")

        var yLabels: List<String>? = null
        var yAxisLabelValues: List<Float>? = null

        if (!autoGeneratedY){
            val f = DecimalFormat("#.########")
            f.maximumFractionDigits = 8
            val maxYL = priceHistory.max()!!.toFloat()
            val minYL = priceHistory.min()!!.toFloat()
            if(maxYL == minYL) return ValueDataList(dataList, labelList,  labelValueList, listOf(f.format(minYL)), listOf(minYL),  xLabel)
            val step = (maxYL - minYL) / 8
            yLabels = ArrayList<String>(8)
            yAxisLabelValues = ArrayList<Float>(8)
            var value = minYL - step
            while (value <= maxYL){
                var lValue = f.format(value)
                while (lValue.length < 10) lValue += "0"
                yLabels.add(lValue)
                yAxisLabelValues.add(value)
                value += step
            }
        }

        log_d("labels size: ${labelList.size}  l. values size: ${labelValueList.size}")
        log_d("labels X: ${labelList.joinToString()}")
        log_d("label values X: ${labelValueList.joinToString()}")
        log_d("labels Y: ${yLabels?.joinToString()}")
        log_d("label values Y ${yAxisLabelValues?.joinToString()}")

        return ValueDataList(
            dataList,
            labelList,
            labelValueList,
            yLabels,
            yAxisLabelValues,
            xLabel
        )

    }

    data class ValueDataList(
        val values: List<PointValue>,
        val xLabels: List<String>,
        val xAxisLabelValues: List<Float>,
        val yLabels: List<String>?,
        val yAxisLabelValues: List<Float>?,
        val xAxisLabel: String)

}