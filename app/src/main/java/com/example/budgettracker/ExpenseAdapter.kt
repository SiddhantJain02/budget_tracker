package com.example.budgettracker

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import java.text.SimpleDateFormat

class ExpenseAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseHolder>() {

    class ExpenseHolder(view: View) : RecyclerView.ViewHolder(view){
        val label : TextView = view.findViewById(R.id.label)
        val amount : TextView = view.findViewById(R.id.amount)
        val date1 : TextView = view.findViewById(R.id.date1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout,parent,false)
        return ExpenseHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseHolder, position: Int) {

        val pos: Int = transactions.size - 1 - position

        val expense = transactions[pos]
        val context = holder.amount.context


            holder.amount.text = "- ₹%.2f".format(Math.abs(expense.amount))
            holder.amount.setTextColor(ContextCompat.getColor(context,R.color.red))

            holder.label.text = expense.label

        val dateFormated = SimpleDateFormat("EEE, MMM d, ''yy").format(expense.date)
        holder.date1.text = dateFormated



            holder.itemView.setOnClickListener {
                val intent = Intent(context, DetailedActivity::class.java)
                intent.putExtra("transaction", expense)
                context.startActivity(intent)
                Animatoo.animateZoom(context)
            }



    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun setData(transactions: List<Transaction>){
        this.transactions = transactions
        notifyDataSetChanged()
    }
}