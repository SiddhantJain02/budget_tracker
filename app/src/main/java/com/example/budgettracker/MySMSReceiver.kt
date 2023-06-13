package com.example.budgettracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.SyncStateContract.Helpers.insert
import android.telephony.SmsMessage
import android.widget.Toast
import androidx.room.Room
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.regex.Pattern

class MySMSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        var str = ""
        if (bundle != null) {
            val pdus = bundle.get("pdus") as Array<Any>
            var x = ""
            for (onePdus : Any in pdus) {
                val oneSMS = SmsMessage.createFromPdu(onePdus as ByteArray)
                x = oneSMS.originatingAddress.toString()
                str += oneSMS.messageBody.toString()

            }

            val pattern1 = Pattern.compile("BOIUPI")
            val matcher1 = pattern1.matcher(x)

            val pattern2 = Pattern.compile("INDBNK")
            val matcher2 = pattern2.matcher(x)

            if (matcher1.find()) {

                val pattern = Pattern.compile("Rs")
                val matcher = pattern.matcher(str)
                var amount = ""
                if (matcher.find()) {
                    val y = str.indexOf("Rs")
                    val z = str.substring(y)
                    val twoWordsName: String =
                        z.substring(0, z.indexOf(' ', z.indexOf(' ')))

                    amount = twoWordsName.substring(2)
                    amount = amount.replace(",", "")
                }

                var d = amount.toDouble()


                var name = ""

                val patternx = Pattern.compile("credited")
                val matcherx = patternx.matcher(str)
                if (matcherx.find()){
                    val k = str.indexOf("(")
                    val m = str.indexOf(")")
                    val l = str.substring(k + 1, m)
                    name = l
                    Toast.makeText(context, d.toString()+" CREDITED from "+name, Toast.LENGTH_SHORT).show()
                }

                else{
                    val k = str.indexOf("to")
                    val m = str.indexOf("Ref")
                    val l = str.substring(k + 2, m - 1)
                    name = l
                    d = d.unaryMinus()
                    Toast.makeText(context, d.unaryMinus().toString()+" DEBITED from "+name, Toast.LENGTH_SHORT).show()
                }



                val db = Room.databaseBuilder(context,
                    AppDatabase::class.java,
                    "transactions").allowMainThreadQueries().build()




                val transaction = Transaction(0,name,d,"",Calendar.getInstance().time)

                GlobalScope.launch {
                    db.transactionDao().insertAll(transaction)
                }
            }



            else if (matcher2.find()) {

                val pattern = Pattern.compile("Rs")
                val matcher = pattern.matcher(str)
                var amount = ""
                if (matcher.find()) {
                    val y = str.indexOf("Rs")
                    val z = str.substring(y+3)
                    val twoWordsName: String =
                        z.substring(0, z.indexOf(' ', z.indexOf(' ')))

                    amount = twoWordsName
                    amount = amount.replace(",", "")
                }

                var d = amount.toDouble()

                var name = ""


                if (str[0] == 'Y'){
                    val k = str.indexOf("credited")
                    val m = str.indexOf("(")
                    val l = str.substring(k + 12, m)
                    name = l
                    d = d.unaryMinus()
                    Toast.makeText(context, d.unaryMinus().toString()+" DEBITED from "+name, Toast.LENGTH_SHORT).show()
                }

                else{
                    val k = str.indexOf("VPA")
                    val m = str.indexOf("(")
                    val l = str.substring(k + 3, m)
                    name = l
                    Toast.makeText(context, d.toString()+" CREDITED from "+name, Toast.LENGTH_SHORT).show()
                }


                val db = Room.databaseBuilder(context,
                    AppDatabase::class.java,
                    "transactions").allowMainThreadQueries().build()


                val transaction = Transaction(0,name,d,"",Calendar.getInstance().time)

                GlobalScope.launch {
                    db.transactionDao().insertAll(transaction)
                }

            }


        }

    }

}