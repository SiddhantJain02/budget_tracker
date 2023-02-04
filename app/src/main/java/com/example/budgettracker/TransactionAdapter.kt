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

class TransactionAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    class TransactionHolder(view: View) : RecyclerView.ViewHolder(view){
        val label : TextView = view.findViewById(R.id.label)
        val amount : TextView = view.findViewById(R.id.amount)
        val tag : TextView = view.findViewById(R.id.tag)
        val date1 : TextView = view.findViewById(R.id.date1)
        val num : TextView = view.findViewById(R.id.num)
        val day : TextView = view.findViewById(R.id.day)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout,parent,false)
        return TransactionHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {

        val pos: Int = transactions.size - 1 - position

        val transaction = transactions[pos]
        val context = holder.amount.context

        if(transaction.description.isNotEmpty()){
            holder.tag.visibility = View.VISIBLE
        }
        else{
            holder.tag.visibility = View.GONE
        }

        if(transaction.amount >=0){
            holder.amount.text = "+ ₹%.2f".format(transaction.amount)
            holder.amount.setTextColor(ContextCompat.getColor(context,R.color.green))
            //holder.tag.setBackgroundColor(ContextCompat.getColor(context, R.color.green2))
            holder.tag.setBackgroundResource(R.drawable.bg_tag)

        }
        else{
            holder.amount.text = "- ₹%.2f".format(Math.abs(transaction.amount))
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
            //holder.tag.setBackgroundColor(ContextCompat.getColor(context, R.color.red2))
            holder.tag.setBackgroundResource(R.drawable.bg_tag2)
        }

        val dateFormated = SimpleDateFormat("EEE, MMM d, ''yy").format(transaction.date)
        holder.label.text = transaction.label
        holder.tag.text = transaction.description
        holder.date1.visibility = View.GONE
        val str = transaction.date.toString()
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
        //holder.day.text = transaction.date.toString()






        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailedActivity::class.java)
            intent.putExtra("transaction", transaction)
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