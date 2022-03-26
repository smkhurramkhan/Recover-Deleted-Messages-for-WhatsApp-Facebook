package com.test.recovermessages.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.test.recovermessages.R
import com.test.recovermessages.models.DataModel

class MessagesAdapter(private val context: Context, private val list: List<DataModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
   private inner class userHolder (view: View) : RecyclerView.ViewHolder(view) {
        var linearLayout: LinearLayout = view.findViewById<View>(R.id.msg_parent) as LinearLayout
       var msg: TextView = view.findViewById<View>(R.id.msg) as TextView
       var time: TextView = view.findViewById<View>(R.id.time) as TextView

   }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return userHolder(
            LayoutInflater.from(
                context
            ).inflate(R.layout.msg_layout, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val userHolder = viewHolder as userHolder
        val dataModel = list[i]
        userHolder.msg.text = dataModel.msg as CharSequence
        userHolder.time.text = dataModel.time as CharSequence
        userHolder.linearLayout.setOnClickListener {
            val equals = if (userHolder.msg.text == "\ud83d\udcf7 Photo") 1 else 0
            if (equals == 0) {
                copymsg(dataModel.msg)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(equals: Int): Int {
        return equals
    }

    private fun copymsg(s: String) {
        try {
            (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                ClipData.newPlainText("msg" as CharSequence, s as CharSequence)
            )
            Toast.makeText(context, "Message Copied" as CharSequence, Toast.LENGTH_SHORT).show()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}