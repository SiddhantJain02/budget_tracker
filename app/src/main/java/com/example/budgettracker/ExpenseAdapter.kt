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

class ExpenseAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<ExpenseAdapter.TransactionHolder>() {

    class TransactionHolder(view: View) : RecyclerView.ViewHolder(view){
        val label : TextView = view.findViewById(R.id.label)
        val amount : TextView = view.findViewById(R.id.amount)
        val tag : TextView = view.findViewById(R.id.tag)
        val loy : RelativeLayout = view.findViewById(R.id.loy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout,parent,false)
        return TransactionHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = transactions[position]
        val context = holder.amount.context

        if(transaction.amount <0){
            holder.amount.text = "- ₹%.2f".format(Math.abs(transaction.amount))
            holder.amount.setTextColor(ContextCompat.getColor(context,R.color.red))

            holder.label.text = transaction.label
            holder.tag.text = transaction.description

            if(transaction.description.isNotEmpty()){
                holder.tag.visibility = View.VISIBLE
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, DetailedActivity::class.java)
                intent.putExtra("transaction", transaction)
                context.startActivity(intent)
                Animatoo.animateZoom(context)
            }

            holder.loy.visibility = View.VISIBLE
        }
        else{
            holder.loy.visibility = View.GONE
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