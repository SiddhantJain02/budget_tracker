package com.example.budgettracker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.android.synthetic.main.activity_add_transaction.amountInput
import kotlinx.android.synthetic.main.activity_add_transaction.amountLayout
import kotlinx.android.synthetic.main.activity_add_transaction.closeBtn
import kotlinx.android.synthetic.main.activity_add_transaction.descriptionInput
import kotlinx.android.synthetic.main.activity_add_transaction.labelInput
import kotlinx.android.synthetic.main.activity_add_transaction.labelLayout
import kotlinx.android.synthetic.main.activity_detailed.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DetailedActivity : AppCompatActivity() {
    private lateinit var transaction: Transaction
    private lateinit var date: Date
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)


        transaction = intent.getSerializableExtra("transaction") as Transaction

        val dateFormated = SimpleDateFormat("EEE, MMM d, ''yy").format(transaction.date)
        labelInput.setText(transaction.label)
        amountInput.setText(transaction.amount.toString())
        descriptionInput.setText(transaction.description)
        autoComplete1.setText(transaction.description)
        date = transaction.date
        detailDate.setText(dateFormated)

        /*if (transaction.description=="Food"){
            roomView.setBackgroundColor(ContextCompat.getColor(this, R.color.c1))
        }
        else if (transaction.description=="Necessity"){
            roomView.setBackgroundColor(ContextCompat.getColor(this, R.color.c2))
        }
        else if (transaction.description=="Clothing"){
            roomView.setBackgroundColor(ContextCompat.getColor(this, R.color.c3))
        }
        else if (transaction.description=="Travel"){
            roomView.setBackgroundColor(ContextCompat.getColor(this, R.color.c4))
        }
        else if (transaction.description=="Entertainment"){
            roomView.setBackgroundColor(ContextCompat.getColor(this, R.color.c5))
        }*/



        val tags = resources.getStringArray(R.array.tags)
        val arrayAdapter = ArrayAdapter(this,R.layout.dropdown_item,tags)
        autoComplete1.setAdapter(arrayAdapter)

        roomView.setOnClickListener {
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        labelInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
            if (it!!.isNotEmpty())
                labelLayout.error = null
        }

        descriptionInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
        }

        autoComplete1.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
        }

        amountInput.addTextChangedListener {
            updateBtn.visibility = View.VISIBLE
            if (it!!.isNotEmpty())
                amountLayout.error = null
        }

        updateBtn.setOnClickListener {
            val label = labelInput.text.toString()
            val description = autoComplete1.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()


            if(label.isEmpty())
                labelLayout.error = "Please enter a valid label"

            else if (amount == null)
                amountLayout.error = "Please enter a valid amount"

            else{
                val transaction = Transaction(transaction.id,label,amount,description,date)
                update(transaction)
            }
        }

        closeBtn.setOnClickListener {
            finish()
            Animatoo.animateZoom(this)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        Animatoo.animateZoom(this)
    }

    private fun update(transaction: Transaction){
        val db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().update(transaction)
            finish()
            Animatoo.animateFade(this@DetailedActivity)
        }
    }
}