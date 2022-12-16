package com.example.budgettracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class ExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseBinding

    private lateinit var db : AppDatabase

    private lateinit var adapterExpense: ExpenseAdapter

    private lateinit var transactions : List<Transaction>

    private lateinit var linearlayoutManager: LinearLayoutManager

    private lateinit var deletedTransaction: Transaction
    private lateinit var oldTransactions : List<Transaction>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transactions = arrayListOf()
        linearlayoutManager = LinearLayoutManager(this)
        adapterExpense = ExpenseAdapter(transactions)

        db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()

        binding.recyclerExpense.apply {
            adapter = adapterExpense
            layoutManager = linearlayoutManager
        }

        loadExpense()

        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                var p0 = -1
                var cnt = -1

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
        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(binding.recyclerExpense)
    }

    private fun loadExpense() {

        GlobalScope.launch {
            transactions = db.transactionDao().getAll()
            var tr: ArrayList<Transaction> = ArrayList()

            for (ds in transactions){
                val  model = ds
                if (model.amount<0){
                    tr.add(model)
                }
            }

            runOnUiThread {
                adapterExpense.setData(tr)
            }
        }
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