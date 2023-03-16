package com.example.budgettracker

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.stackedchart.RoundedBarChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_bar_chart.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class BarChartActivity : AppCompatActivity() {
    private lateinit var transactions : List<Transaction>
    private lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_chart)

        transactions = arrayListOf()

        db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            transactions = db.transactionDao().getAll()

            var tr: ArrayList<Transaction> = ArrayList()

            for (ds in transactions){
                val  model = ds

                val month = ds.date.month
                val m = Calendar.getInstance().time.month

                val year = ds.date.year
                val y = Calendar.getInstance().time.year

                if (month==m && year==y){
                    tr.add(model)
                }
            }
            val n = Calendar.getInstance().time.toString().split(" ").toTypedArray()[2].toInt()

            populateGraphData(n,tr)
            updateData(n,tr)
        }

    }

    private fun updateData(n: Int, tr: ArrayList<Transaction>) {
        val expAmount:Double = tr.filter { it.amount<0 }.map { it.amount }.sum()
        val totalAmount:Double = tr.map { it.amount }.sum()

        val avgAmount = expAmount/n
        var bugAmount = totalAmount/(30-n)

        avg2.text = "₹ %.2f".format(avgAmount)

        val m = Calendar.getInstance().time.toString().split(" ").toTypedArray()[1]

        if (m=="Jan" || m=="Mar" || m=="May" || m=="Jul" || m=="Aug" || m=="Oct" || m=="Dec"){
            bugAmount = totalAmount/(31-n)
        }
        else if (m=="Feb"){
            bugAmount = totalAmount/(28-n)
        }

        bud2.text = "₹ %.2f".format(bugAmount)

    }

    fun populateGraphData(n: Int, tr: ArrayList<Transaction>) {

        var barChartView = findViewById<BarChart>(R.id.chartConsumptionGraph)

        val barWidth: Float
        val barSpace: Float
        val groupSpace: Float
        val groupCount = n

        barWidth = 0.2f
        barSpace = 0.05f
        groupSpace = 0.50f

        val m = Calendar.getInstance().time.toString().split(" ").toTypedArray()[1]

        var xAxisValues = ArrayList<String>()
        for (i in 1 until  n+1 ){
            if(i==1){
                xAxisValues.add(i.toString()+"st "+m)
            }
            else if(i==2){
                xAxisValues.add(i.toString()+"nd "+m)
            }
            else if(i==3){
                xAxisValues.add(i.toString()+"rd "+m)
            }
            else{
                xAxisValues.add(i.toString()+"th "+m)
            }

        }

        var yValueGroup1 = ArrayList<BarEntry>()


        // draw the graph
        var barDataSet1: BarDataSet

        var d = 1

        var fo=0f
        var ne=0f
        var cl=0f
        var ta=0f
        var en=0f

        for (ds in tr){
            val str = ds.date.toString()
            val sp = str.split(" ").toTypedArray()
            if (sp[2].toString().toInt() != d){
                yValueGroup1.add(BarEntry(d.toFloat()-0.5f, floatArrayOf(fo, ne,cl,ta,en)))

                fo=0f; ne=0f; cl=0f; ta=0f; en=0f
                d=sp[2].toString().toInt()
                if (ds.description == "Food") {
                    fo += ds.amount.unaryMinus().toFloat()
                } else if (ds.description == "Necessity") {
                    ne += ds.amount.unaryMinus().toFloat()
                } else if (ds.description == "Clothing") {
                    cl += ds.amount.unaryMinus().toFloat()
                } else if (ds.description == "Travel") {
                    ta += ds.amount.unaryMinus().toFloat()
                } else if (ds.description == "Entertainment") {
                    en += ds.amount.unaryMinus().toFloat()
                }
            }

            else{
                if (ds.description == "Food") {
                    fo += ds.amount.unaryMinus().toFloat()
                } else if (ds.description == "Necessity") {
                    ne += ds.amount.unaryMinus().toFloat()
                } else if (ds.description == "Clothing") {
                    cl += ds.amount.unaryMinus().toFloat()
                } else if (ds.description == "Travel") {
                    ta += ds.amount.unaryMinus().toFloat()
                } else if (ds.description == "Entertainment") {
                    en += ds.amount.unaryMinus().toFloat()
                }
            }



            //yValueGroup1.add(BarEntry(x.toFloat(), floatArrayOf(9.toFloat(), 3.toFloat())))

        }

        yValueGroup1.add(BarEntry(d.toFloat()-0.5f, floatArrayOf(fo, ne,cl,ta,en)))

        val colors: ArrayList<Int> = ArrayList()
        for (color in ColorTemplate.MATERIAL_COLORS) {
            colors.add(color)
        }

        for (color in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color)
        }



        barDataSet1 = BarDataSet(yValueGroup1, "")
        barDataSet1.setColors(ContextCompat.getColor(this, R.color.c1), ContextCompat.getColor(this, R.color.c2), ContextCompat.getColor(this, R.color.c3),ContextCompat.getColor(this, R.color.c4), ContextCompat.getColor(this, R.color.c5))
        barDataSet1.label = "2016"
        barDataSet1.setDrawIcons(false)
        barDataSet1.setDrawValues(false)



        var barData = BarData(barDataSet1)

        barChartView.description.isEnabled = false
        barChartView.description.textSize = 0f
        barData.setValueFormatter(LargeValueFormatter())
        barChartView.setData(barData)
        barChartView.getBarData().setBarWidth(barWidth)
        barChartView.getXAxis().setAxisMinimum(0f)
        barChartView.getXAxis().setAxisMaximum(n.toFloat())
        //barChartView.setFitBars(true)
        barChartView.getData().setHighlightEnabled(true)
        barChartView.invalidate()

        // set bar label
        var legend = barChartView.legend
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT)
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        legend.setDrawInside(false)

        var legenedEntries = arrayListOf<LegendEntry>()

        legenedEntries.add(LegendEntry("Food", Legend.LegendForm.SQUARE, 9f, 8f, null, ContextCompat.getColor(this, R.color.c1)))
        legenedEntries.add(LegendEntry("Necessity", Legend.LegendForm.SQUARE, 9f, 8f, null, ContextCompat.getColor(this, R.color.c2)))
        legenedEntries.add(LegendEntry("Clothing", Legend.LegendForm.SQUARE, 9f, 8f, null, ContextCompat.getColor(this, R.color.c3)))
        legenedEntries.add(LegendEntry("Travel", Legend.LegendForm.SQUARE, 9f, 8f, null, ContextCompat.getColor(this, R.color.c4)))
        legenedEntries.add(LegendEntry("Entertainment", Legend.LegendForm.SQUARE, 9f, 8f, null, ContextCompat.getColor(this, R.color.c5)))

        legend.setCustom(legenedEntries)

        legend.setYOffset(2f)
        legend.setXOffset(2f)
        legend.setYEntrySpace(5f)
        legend.setTextSize(12f)
        legend.textColor = Color.WHITE

        val xAxis = barChartView.getXAxis()
        xAxis.setGranularity(1f)
        xAxis.setGranularityEnabled(true)
        xAxis.setCenterAxisLabels(true)
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 10f
        xAxis.textColor = Color.WHITE

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setValueFormatter(IndexAxisValueFormatter(xAxisValues))

        xAxis.setLabelCount(n)
        xAxis.mAxisMaximum = n.toFloat()
        xAxis.setCenterAxisLabels(true)
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.spaceMin = 4f
        xAxis.spaceMax = 4f

        barChartView.setVisibleXRangeMaximum(n.toFloat())
        barChartView.setVisibleXRangeMinimum(n.toFloat())
        barChartView.setDragEnabled(true)

        //Y-axis
        barChartView.getAxisRight().setEnabled(false)
        barChartView.setScaleEnabled(true)
        barChartView.setScaleMinima(1f,1f)

        val leftAxis = barChartView.getAxisLeft()
        leftAxis.setValueFormatter(LargeValueFormatter())
        leftAxis.setDrawGridLines(true)
        leftAxis.setSpaceTop(1f)
        leftAxis.setAxisMinimum(0f)
        leftAxis.textColor = Color.WHITE


        barChartView.data = barData

        //barChartView.animateY(500)

        runOnUiThread {
            barChartView.animateY(800)
        }
        barChartView.setVisibleXRange(1f,6f)
        barChartView.renderer = RoundedBarChart(barChartView, barChartView.animator, barChartView.viewPortHandler)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        Animatoo.animateSlideUp(this)
    }
}