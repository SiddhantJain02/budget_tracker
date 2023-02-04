package com.example.budgettracker

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.budgettracker.R.color
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup.*
import kotlinx.android.synthetic.main.popup.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar.getInstance


class MainActivity : AppCompatActivity() {
    private lateinit var deletedTransaction: Transaction
    private lateinit var transactions : List<Transaction>
    private lateinit var oldTransactions : List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var deleteallbtn: Button
    private lateinit var linearlayoutManager: LinearLayoutManager
    private lateinit var db : AppDatabase
    private lateinit var mpChartView: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        transactions = arrayListOf()
        deleteallbtn = findViewById(R.id.deleteAllBtn)
        mpChartView = findViewById(R.id.mPie)
        transactionAdapter = TransactionAdapter(transactions)
        linearlayoutManager = LinearLayoutManager(this)

        db = Room.databaseBuilder(this,
        AppDatabase::class.java,
        "transactions").build()

        recycler.apply {
            adapter = transactionAdapter
            layoutManager = linearlayoutManager
        }

        updateDashboard()

        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var position = viewHolder.adapterPosition
                var tot = transactions.size
                position = tot - position - 1

                deleteTransaction(transactions[position])
            }
        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recycler)


        addBtn.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
            Animatoo.animateSlideLeft(this)
        }

        deleteAllBtn.setOnClickListener {
            val mDialog = LayoutInflater.from(this).inflate(R.layout.popup,null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialog)

            val mAlertDialog = mBuilder.show()
            Animatoo.animateSlideLeft(this)

            mDialog.cnlBtn2.setOnClickListener {
                mAlertDialog.dismiss()
            }

            mDialog.delBtn2.setOnClickListener {
                deleteTransactionAll()
                mAlertDialog.dismiss()
            }

        }



            budget.setOnClickListener {
                val intent = Intent(this, BudgetActivity::class.java)
                startActivity(intent)
                Animatoo.animateSlideRight(this)
            }


         expense.setOnClickListener {
            val intent = Intent(this, ExpenseActivity::class.java)
            startActivity(intent)
            Animatoo.animateSlideLeft(this)
        }

    }

    private fun fetchAll(){
        GlobalScope.launch {
            transactions = db.transactionDao().getAll()

            var tr: ArrayList<Transaction> = ArrayList()

            for (ds in transactions){
                val  model = ds

                val month = ds.date.month
                val m = getInstance().time.month

                val year = ds.date.year
                val y = getInstance().time.year

                if (month==m && year==y){
                    tr.add(model)
                }
            }

            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(tr)
            }
        }

    }

    private fun updateDashboard(){

        var tr: ArrayList<Transaction> = ArrayList()

        for (ds in transactions){
            val  model = ds

            val month = ds.date.month
            val m = getInstance().time.month

            val year = ds.date.year
            val y = getInstance().time.year

            if (month==m && year==y){
                tr.add(model)
            }
        }

        val totalAmount:Double = tr.map { it.amount }.sum()
        var totalTravel:Float = tr.filter { it.description=="Travel" }.map { it.amount }.sum().unaryMinus().toFloat()
        var totalFood:Float = tr.filter { it.description=="Food" }.map { it.amount }.sum().unaryMinus().toFloat()
        var totalNecessity:Float = tr.filter { it.description=="Necessity" }.map { it.amount }.sum().unaryMinus().toFloat()
        var totalClothing:Float = tr.filter { it.description=="Clothing" }.map { it.amount }.sum().unaryMinus().toFloat()
        var totalEntertainment:Float = tr.filter { it.description=="Entertainment" }.map { it.amount }.sum().unaryMinus().toFloat()
        val budgetAmount:Double = tr.filter { it.amount>0 }.map { it.amount }.sum()
        val expenseAmount:Double = totalAmount - budgetAmount

        val entries: ArrayList<PieEntry> = ArrayList()

        if(totalEntertainment != 0f){
            entries.add(PieEntry(totalEntertainment, "Entertainment"))
        }

        if(totalNecessity != 0f){
            entries.add(PieEntry(totalNecessity, "Necessity"))
        }

        if(totalClothing != 0f){
            entries.add(PieEntry(totalClothing, "Clothing"))
        }

        if(totalFood != 0f){
            entries.add(PieEntry(totalFood, "Food"))
        }

        if(totalTravel != 0f){
            entries.add(PieEntry(totalTravel, "Travel"))
        }


        val colors: ArrayList<Int> = ArrayList()
        for (color in ColorTemplate.MATERIAL_COLORS) {
            colors.add(color)
        }

        for (color in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color)
        }

        val dataSet = PieDataSet(entries,"")
        mpChartView.setCenterText("Expenses\n" + "₹ %.1f".format(expenseAmount.unaryMinus()))
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(mpChartView))
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK);

        mpChartView.setData(data)
        mpChartView.invalidate()

        mpChartView.animateY(1400, Easing.EaseInOutQuad)

        setupPieChart()

        if(!transactions.isEmpty()){
            deleteallbtn.setVisibility(View.VISIBLE)
        }

        if(totalFood == 0f && totalTravel==0f && totalEntertainment==0f && totalClothing==0f && totalNecessity==0f){
            mpChartView.visibility = View.GONE
            nmid.layoutParams.height = 0

        }

        else{
            mpChartView.visibility = View.VISIBLE
            nmid.layoutParams.height = 800

        }



        balance.text = "₹ %.2f".format(totalAmount)
        if(totalAmount > 0){
            balance.setTextColor(ContextCompat.getColor(this,R.color.green))
        }
        else if(totalAmount < 0){
            balance.setTextColor(ContextCompat.getColor(this,R.color.red))
        }
        left2.text = "₹ %.2f".format(budgetAmount)
        right2.text = "₹ %.2f".format(expenseAmount)

    }

    private fun setupPieChart() {
        mpChartView.setDrawHoleEnabled(true)
        mpChartView.setUsePercentValues(false)
        mpChartView.setEntryLabelTextSize(0f)
        mpChartView.setEntryLabelColor(Color.BLACK)
        mpChartView.setCenterTextSize(18f)
        mpChartView.setCenterTextColor(Color.WHITE)
        mpChartView.getDescription().setEnabled(false)
        mpChartView.setHoleRadius(45f)
        mpChartView.setTransparentCircleColor(Color.BLACK)
        mpChartView.setHoleColor(Color.TRANSPARENT)
        val l: Legend = mpChartView.getLegend()
        l.setDrawInside(false)
        l.isEnabled = true
        l.textColor = Color.WHITE
        l.textSize = 12f
        l.setForm(Legend.LegendForm.CIRCLE)
        l.setFormToTextSpace(2f)
        l.setOrientation(Legend.LegendOrientation.VERTICAL)
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP)

    }

    private fun undoDelete(){
        GlobalScope.launch {
           db.transactionDao().insertAll(deletedTransaction)

            transactions = oldTransactions

            runOnUiThread {
                updateDashboard()

                var tr: ArrayList<Transaction> = ArrayList()

                for (ds in transactions){
                    val  model = ds

                    val month = ds.date.month
                    val m = getInstance().time.month

                    val year = ds.date.year
                    val y = getInstance().time.year

                    if (month==m && year==y){
                        tr.add(model)
                    }
                }

                transactionAdapter.setData(tr)
            }
        }
    }

    private fun showSnackbar(){
        val view = findViewById<View>(R.id.relativeLayout2)
        val snackbar = Snackbar.make(view,"Transaction deleted",Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo"){
            undoDelete()
        }
            .setActionTextColor(ContextCompat.getColor(this, color.red))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }


    private fun deleteTransaction(transaction: Transaction){
        deletedTransaction = transaction

        var tr: ArrayList<Transaction> = ArrayList()

        for (ds in transactions){
            val  model = ds

            val month = ds.date.month
            val m = getInstance().time.month

            val year = ds.date.year
            val y = getInstance().time.year

            if (month==m && year==y){
                tr.add(model)
            }
        }

        oldTransactions = tr

        GlobalScope.launch {
            db.transactionDao().delete(transaction)


            transactions = transactions.filter { it.id != transaction.id}
            runOnUiThread {
                updateDashboard()

                var tr: ArrayList<Transaction> = ArrayList()

                for (ds in transactions){
                    val  model = ds

                    val month = ds.date.month
                    val m = getInstance().time.month

                    val year = ds.date.year
                    val y = getInstance().time.year

                    if (month==m && year==y){
                        tr.add(model)
                    }
                }

                transactionAdapter.setData(tr)
                showSnackbar()
            }
        }
    }

    private fun deleteTransactionAll(){


        GlobalScope.launch {
            db.transactionDao().deleteAll()

            transactions = db.transactionDao().getAll()

            runOnUiThread {
                updateDashboard()
                var tr: ArrayList<Transaction> = ArrayList()

                for (ds in transactions){
                    val  model = ds

                    val month = ds.date.month
                    val m = getInstance().time.month

                    val year = ds.date.year
                    val y = getInstance().time.year

                    if (month==m && year==y){
                        tr.add(model)
                    }
                }

                transactionAdapter.setData(tr)
            }
        }

            deleteallbtn.setVisibility(View.GONE)

    }

    override fun onResume() {
        super.onResume()
        fetchAll()

    }

}