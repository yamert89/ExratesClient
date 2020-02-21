package ru.exrates.mobile.graph

import android.graphics.Color
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.Viewport
import lecho.lib.hellocharts.view.LineChartView
import ru.exrates.mobile.ExratesActivity
import ru.exrates.mobile.log_d


class GraphFactory(private val anyChartView: LineChartView) {


    fun createSmallGraph(){


    }

    fun createBigGraph(valueDataList: ExratesActivity.ValueDataList) {
        val line = Line(valueDataList.values).setColor(Color.RED).setCubic(true)
        log_d(valueDataList.values.joinToString { "x: ${it.x} y: ${it.y}" })
        //line.setHasPoints(false)
        line.isSquare = true
        val lineChartData = LineChartData(mutableListOf(line))
        lineChartData.isValueLabelBackgroundEnabled = true
        lineChartData.axisYLeft = Axis.generateAxisFromCollection(valueDataList.values.map { it.y }, valueDataList.values.map { it.y.toString() })
        lineChartData.axisXBottom = Axis.generateAxisFromCollection(valueDataList.xAxisLabelValues, valueDataList.xLabels)
        //lineChartData.axisXBottom = Axis.generateAxisFromCollection(valueDataList.values.map { it.x }, valueDataList.values.map { it.x.toString() })

        lineChartData.axisYLeft.name = "Price"
        lineChartData.axisXBottom.name = valueDataList.xAxisLabel
        lineChartData.axisYLeft.maxLabelChars = 5
       // lineChartData.axisXBottom.maxLabelChars = 15
        lineChartData.axisYLeft.textSize = 10

        val v = Viewport(anyChartView.maximumViewport)
        v.bottom = 0f
        v.top = 100f
        v.left = 0f
        v.right = 9f
        anyChartView.maximumViewport = v
        anyChartView.currentViewport = v

        anyChartView.lineChartData = lineChartData
        anyChartView.contentDescription = "dfdf"
    }

}