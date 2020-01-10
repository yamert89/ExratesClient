package ru.exrates.mobile.graph

import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Line
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.Anchor
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipPositionMode

class GraphFactory(private val anyChartView: AnyChartView) {
    private val seriesData: MutableList<DataEntry> = ArrayList()

    fun getSmallGraph(seriesDataCollection: Collection<ValueDataEntry>): AnyChartView{
        val cartesian: Cartesian = AnyChart.line()

        cartesian.animation(true)

        cartesian.padding(10.0, 20.0, 5.0, 10.0)

        //cartesian.crosshair().enabled(true)
        /*cartesian.crosshair()
            .yLabel(true) // TODO ystroke
            .yStroke(null as Stroke?, null, null, null as String?, null as String?)*/

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

        //cartesian.title("Trend of Sales of the Most Popular Products of ACME Corp.")

        //cartesian.yAxis(0).title("Number of Bottles Sold (thousands)")
        cartesian.xAxis(0).title("january")
        cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)

        seriesData.addAll(seriesDataCollection)
        val set: Set = Set.instantiate()
        set.data(seriesData)
        val series1Mapping: Mapping = set.mapAs("{ x: 'x', value: 'value' }")
        //val series2Mapping: Mapping = set.mapAs("{ x: 'x', value: 'value2' }")


        val series1: Line = cartesian.line(series1Mapping)
        series1.name("Price")
        series1.hovered().markers().enabled(true)
        series1.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
        series1.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)

        /*val series2: Line = cartesian.line(series2Mapping)
        series2.name("Whiskey")
        series2.hovered().markers().enabled(true)
        series2.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
        series2.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)*/

        cartesian.legend().enabled(true)
        cartesian.legend().fontSize(13.0)
        cartesian.legend().padding(0.0, 0.0, 10.0, 0.0)

        anyChartView.setChart(cartesian)
        return anyChartView
    }

    fun getBigGraph(seriesDataCollection: Collection<ValueDataEntry>): AnyChartView {
        val cartesian: Cartesian = AnyChart.line()

        cartesian.animation(true)

        cartesian.padding(10.0, 20.0, 5.0, 10.0)

        //cartesian.crosshair().enabled(true)
        /*cartesian.crosshair()
            .yLabel(true) // TODO ystroke
            .yStroke(null as Stroke?, null, null, null as String?, null as String?)*/

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

        //cartesian.title("Trend of Sales of the Most Popular Products of ACME Corp.")

        //cartesian.yAxis(0).title("Number of Bottles Sold (thousands)")
        cartesian.xAxis(0).title("january")
        cartesian.xAxis(0).labels().padding(5.0, 5.0, 5.0, 5.0)

        seriesData.addAll(seriesDataCollection)
        val set: Set = Set.instantiate()
        set.data(seriesData)
        val series1Mapping: Mapping = set.mapAs("{ x: 'x', value: 'value' }")
        //val series2Mapping: Mapping = set.mapAs("{ x: 'x', value: 'value2' }")


        val series1: Line = cartesian.line(series1Mapping)
        series1.name("Price")
        series1.hovered().markers().enabled(true)
        series1.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
        series1.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)

        /*val series2: Line = cartesian.line(series2Mapping)
        series2.name("Whiskey")
        series2.hovered().markers().enabled(true)
        series2.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
        series2.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)*/

        cartesian.legend().enabled(true)
        cartesian.legend().fontSize(13.0)
        cartesian.legend().padding(0.0, 0.0, 10.0, 0.0)

        anyChartView.setChart(cartesian)
        return anyChartView
    }

}