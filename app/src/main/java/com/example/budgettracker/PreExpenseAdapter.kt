package com.example.budgettracker

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import java.text.SimpleDateFormat
//ADAPTER
class PreExpenseAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<PreExpenseAdapter.PreExpenseHolder>() {

    class PreExpenseHolder(view: View) : RecyclerView.ViewHolder(view){
        val label : TextView = view.findViewById(R.id.label)
        val amount : TextView = view.findViewById(R.id.amount)
        val date1 : TextView = view.findViewById(R.id.date1)
        val num : TextView = view.findViewById(R.id.num)
        val day : TextView = view.findViewById(R.id.day)
        val tag : TextView = view.findViewById(R.id.tag)
        val imgPng : ImageView = view.findViewById(R.id.imgPng)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreExpenseHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout,parent,false)
        return PreExpenseHolder(view)
    }


    override fun onBindViewHolder(holder: PreExpenseHolder, position: Int) {

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
        holder.tag.setTextColor(ContextCompat.getColor(context, R.color.black))
        if (expense.description=="Food"){
            holder.tag.setBackgroundResource(R.drawable.food_bg)
            holder.imgPng.setImageResource(R.drawable.dish2)
            holder.imgPng.visibility = View.VISIBLE
        }
        else if (expense.description=="Necessity"){
            holder.tag.setBackgroundResource(R.drawable.nes_bg)
            holder.imgPng.setImageResource(R.drawable.expectation2)
            holder.imgPng.visibility = View.VISIBLE
        }
        else if (expense.description=="Clothing"){
            holder.tag.setBackgroundResource(R.drawable.cloth_bg)
            holder.imgPng.setImageResource(R.drawable.laundry2)
            holder.imgPng.visibility = View.VISIBLE
        }
        else if (expense.description=="Travel"){
            holder.tag.setBackgroundResource(R.drawable.travel_bg)
            holder.imgPng.setImageResource(R.drawable.travel2)
            holder.imgPng.visibility = View.VISIBLE
        }
        else if (expense.description=="Entertainment"){
            holder.tag.setBackgroundResource(R.drawable.ent_bg)
            holder.imgPng.setImageResource(R.drawable.cinema2)
            holder.imgPng.visibility = View.VISIBLE
        }
        else{
            holder.tag.setBackgroundResource(R.drawable.bg_tag2)
            holder.imgPng.visibility = View.GONE
        }

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