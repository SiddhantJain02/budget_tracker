package com.example.budgettracker

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import java.text.SimpleDateFormat
import kotlin.math.exp


class BudgetAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<BudgetAdapter.HolderBudget>() {

    class HolderBudget(view: View) : RecyclerView.ViewHolder(view){
        val label : TextView = view.findViewById(R.id.label)
        val amount : TextView = view.findViewById(R.id.amount)
        val date1 : TextView = view.findViewById(R.id.date1)
        val num : TextView = view.findViewById(R.id.num)
        val day : TextView = view.findViewById(R.id.day)
        val tag : TextView = view.findViewById(R.id.tag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderBudget {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout,parent,false)
        return BudgetAdapter.HolderBudget(view)
    }

    override fun onBindViewHolder(holder: HolderBudget, position: Int) {

        val pos: Int = transactions.size - 1 - position

        val budget = transactions[pos]
        val context = holder.amount.context

        holder.amount.text = "₹%.2f".format(Math.abs(budget.amount))
        holder.label.text = budget.label
        holder.amount.setTextColor(ContextCompat.getColor(context,R.color.green))

        if(budget.description.isNotEmpty()){
            holder.tag.visibility = View.VISIBLE
        }
        else{
            holder.tag.visibility = View.GONE
        }

        holder.amount.text = "+ ₹%.2f".format(budget.amount)
        holder.amount.setTextColor(ContextCompat.getColor(context,R.color.green))
        //holder.tag.setBackgroundColor(ContextCompat.getColor(context, R.color.green2))
        holder.tag.setBackgroundResource(R.drawable.bg_tag)

        val dateFormated = SimpleDateFormat("EEE, MMM d, ''yy").format(budget.date)
        holder.date1.text = dateFormated
        holder.num.text = budget.date.day.toString()
        holder.date1.visibility = View.GONE
        holder.tag.text = budget.description
        val i: Int = budget.date.toString().indexOf(' ')
        holder.day.text = budget.date.toString().substring(0, i)

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