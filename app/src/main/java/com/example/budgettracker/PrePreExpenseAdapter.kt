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

class PrePreExpenseAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<PrePreExpenseAdapter.PrePreExpenseHolder>() {

    class PrePreExpenseHolder(view: View) : RecyclerView.ViewHolder(view){
        val label : TextView = view.findViewById(R.id.label)
        val amount : TextView = view.findViewById(R.id.amount)
        val date1 : TextView = view.findViewById(R.id.date1)
        val num : TextView = view.findViewById(R.id.num)
        val day : TextView = view.findViewById(R.id.day)
        val tag : TextView = view.findViewById(R.id.tag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrePreExpenseHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout,parent,false)
        return PrePreExpenseHolder(view)
    }


    override fun onBindViewHolder(holder: PrePreExpenseHolder, position: Int) {

        val pos: Int = transactions.size - 1 - position

        val expense = transactions[pos]
        val context = holder.amount.context


        holder.amount.text = "- ₹%.2f".format(Math.abs(expense.amount))
        holder.amount.setTextColor(ContextCompat.getColor(context,R.color.red))

        holder.label.text = expense.label

        if(expense.description.isNotEmpty()){
            holder.tag.visibility = View.VISIBLE
        }
        else{
            holder.tag.visibility = View.GONE
        }

        holder.amount.text = "- ₹%.2f".format(Math.abs(expense.amount))
        holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
        //holder.tag.setBackgroundColor(ContextCompat.getColor(context, R.color.red2))
        holder.tag.setBackgroundResource(R.drawable.bg_tag2)

        val dateFormated = SimpleDateFormat("EEE, MMM d, ''yy").format(expense.date)
        holder.date1.text = dateFormated
        holder.num.text = expense.date.day.toString()
        holder.date1.visibility = View.GONE
        holder.tag.text = expense.description
        val str = expense.date.toString()
        val sp = str.split(" ").toTypedArray()
        holder.day.text = sp[0]
        holder.num.text = sp[2]

        if(sp[0] == "Sun"){
            holder.day.setTextColor(ContextCompat.getColor(context, R.color.red3))
        }
        else if(sp[0] == "Sat"){
            holder.day.setTextColor(ContextCompat.getColor(context, R.color.blue))
        }
        else{
            holder.day.setTextColor(ContextCompat.getColor(context, R.color.white2))
        }



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