package com.example.budgettracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.budgettracker.databinding.ActivityExpenseBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class ExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseBinding

    private lateinit var db : AppDatabase

    private lateinit var adapterExpense: ExpenseAdapter
    private lateinit var preadapterExpense: PreExpenseAdapter
    private lateinit var prepreadapterExpense: PrePreExpenseAdapter

    private lateinit var transactions : List<Transaction>

    private lateinit var linearlayoutManager: LinearLayoutManager
    private lateinit var linearlayoutManager1: LinearLayoutManager
    private lateinit var linearlayoutManager2: LinearLayoutManager
    private lateinit var thisExp: TextView
    private lateinit var preExp: TextView
    private lateinit var prepreExp: TextView

    private lateinit var deletedTransaction: Transaction
    private lateinit var oldTransactions : List<Transaction>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transactions = arrayListOf()
        linearlayoutManager = LinearLayoutManager(this)
        linearlayoutManager1 = LinearLayoutManager(this)
        linearlayoutManager2 = LinearLayoutManager(this)
        adapterExpense = ExpenseAdapter(transactions)
        preadapterExpense = PreExpenseAdapter(transactions)
        prepreadapterExpense = PrePreExpenseAdapter(transactions)

        db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()

        binding.recyclerExpense.apply {
            adapter = adapterExpense
            layoutManager = linearlayoutManager
        }

        binding.recyclerExpensePre.apply {
            adapter = preadapterExpense
            layoutManager = linearlayoutManager1
        }

        binding.recyclerExpensePrePre.apply {
            adapter = prepreadapterExpense
            layoutManager = linearlayoutManager2
        }

        loadExpense()

        /*val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var position = viewHolder.adapterPosition
                var p0 = 0
                var cnt = -1

                var tot = 0

                for (ds in transactions){
                    val  model = ds
                    if (model.amount<0){
                        tot += 1
                    }
                }

                position = tot - position

                var tr: ArrayList<Transaction> = ArrayList()

                for (ds in transactions){
                    val  model = ds
                    cnt += 1
                    if (model.amount<0){
                        p0 += 1
                        if(p0 == position){
                            break
                        }
                    }
                }

                deleteTransaction(transactions[cnt])
            }
        }*/

        //val swipeHelper = ItemTouchHelper(itemTouchHelper)
        //swipeHelper.attachToRecyclerView(binding.recyclerExpense)
    }

    private fun loadExpense() {

        thisExp = findViewById(R.id.thisExp)
        preExp = findViewById(R.id.preExp)
        prepreExp = findViewById(R.id.prepreExp)
        var thisSum = 0
        var preSum = 0
        var prepreSum = 0
        val m = Calendar.getInstance().time.month
        val date = Calendar.getInstance()
        date.add(Calendar.MONTH, -1)
        var prem = date.time.month

        GlobalScope.launch {
            transactions = db.transactionDao().getAll()
            var tr: ArrayList<Transaction> = ArrayList()
            var pretr: ArrayList<Transaction> = ArrayList()
            var prepretr: ArrayList<Transaction> = ArrayList()

            for (ds in transactions){
                val  model = ds
                if (model.amount<0){

                    if (ds.date.month == m){
                        tr.add(model)
                        thisSum += model.amount.toInt()
                    }
                    else if(ds.date.month == prem){
                        pretr.add(model)
                        preSum += model.amount.toInt()
                    }
                    else{
                        prepretr.add(model)
                        prepreSum += model.amount.toInt()
                    }
                }
            }

            thisExp.text = "- ₹"+thisSum.unaryMinus().toString()
            preExp.text = "- ₹"+preSum.unaryMinus().toString()
            prepreExp.text = "- ₹"+prepreSum.unaryMinus().toString()


            runOnUiThread {
                adapterExpense.setData(tr)
                preadapterExpense.setData(pretr)
                prepreadapterExpense.setData(prepretr)
            }
        }

        thisExp.setTextColor(ContextCompat.getColor(this,R.color.red))
        preExp.setTextColor(ContextCompat.getColor(this,R.color.red))
        prepreExp.setTextColor(ContextCompat.getColor(this,R.color.red))
    }

    private fun undoDelete(){
        GlobalScope.launch {
            db.transactionDao().insertAll(deletedTransaction)

            transactions = oldTransactions

            runOnUiThread {
                loadExpense()

                var tr: ArrayList<Transaction> = ArrayList()

                for (ds in transactions){
                    val  model = ds
                    if (model.amount>0){
                        tr.add(model)
                    }
                }
                adapterExpense.setData(tr)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        Animatoo.animateSlideRight(this)
    }

    private fun showSnackbar(){
        val view = binding.recyclerExpense
        val snackbar = Snackbar.make(view,"Transaction deleted", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo"){
            undoDelete()
        }
            .setActionTextColor(ContextCompat.getColor(this, R.color.red))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }

    private fun deleteTransaction(transaction: Transaction){
        var tr: ArrayList<Transaction> = ArrayList()

        for (ds in transactions){
            val  model = ds
            if (model.amount>0){
                tr.add(model)
            }
        }

        deletedTransaction = transaction
        oldTransactions = tr

        GlobalScope.launch {
            db.transactionDao().delete(transaction)


            transactions = transactions.filter { it.id != transaction.id}
            runOnUiThread {
                loadExpense()
                var tr: ArrayList<Transaction> = ArrayList()

                for (ds in transactions){
                    val  model = ds
                    if (model.amount>0){
                        tr.add(model)
                    }
                }
                adapterExpense.setData(tr)
                showSnackbar()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadExpense()

    }
}