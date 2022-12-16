package com.example.budgettracker

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.budgettracker.databinding.TransactionLayoutBinding
import java.text.SimpleDateFormat

class BudgetAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<BudgetAdapter.HolderBudget>() {

    class HolderBudget(view: View) : RecyclerView.ViewHolder(view){
        val label : TextView = view.findViewById(R.id.label)
        val amount : TextView = view.findViewById(R.id.amount)
        val date1 : TextView = view.findViewById(R.id.date1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderBudget {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout,parent,false)
        return BudgetAdapter.HolderBudget(view)
    }

    override fun onBindViewHolder(holder: HolderBudget, position: Int) {

        val budget = transactions[position]
        val context = holder.amount.context

        holder.amount.text = "₹%.2f".format(Math.abs(budget.amount))
        holder.label.text = budget.label
        holder.amount.setTextColor(ContextCompat.getColor(context,R.color.green))

        val dateFormated = SimpleDateFormat("EEE, MMM d, ''yy").format(budget.date)
        holder.date1.text = dateFormated

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailedActivity::class.java)
            intent.putExtra("transaction", budget)
            context.startActivity(intent)
            Animatoo.animateZoom(context)
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun setData(transactions: List<Transaction>) {
        this.transactions = transactions
        notifyDataSetChanged()
    }


}