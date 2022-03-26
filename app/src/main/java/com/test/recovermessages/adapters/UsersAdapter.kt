package com.test.recovermessages.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.test.recovermessages.App
import com.test.recovermessages.R
import com.test.recovermessages.activities.MessagesActivity
import com.test.recovermessages.models.UserModel
import com.test.recovermessages.utils.AdmobHelper

class UsersAdapter(
    private val context: Context,
    private val list: List<UserModel>,
    private val pack: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class userHolder (view: View) : RecyclerView.ViewHolder(view) {
        var list: ConstraintLayout = view.findViewById<View>(R.id.list) as ConstraintLayout
        var msg: TextView = view.findViewById<View>(R.id.msg) as TextView
        var name: TextView = view.findViewById<View>(R.id.name) as TextView
        var readunread: TextView = view.findViewById<View>(R.id.unread) as TextView
        var time: TextView = view.findViewById<View>(R.id.time) as TextView

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return userHolder(
            LayoutInflater.from(context).inflate(R.layout.users_home, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val userHolder = viewHolder as userHolder
        val userModel = list[i]
        userHolder.name.text = userModel.name
        userHolder.msg.text = userModel.lastmsg
        userHolder.time.text = userModel.time
        userHolder.list.setOnClickListener {
            if (App.adCount % 4 == 0) {
                AdmobHelper.showInterstitialAd(context, AdmobHelper.ADSHOWN)
            }
            App.adCount++
            val intent = Intent(context, MessagesActivity::class.java)
            intent.putExtra("name", userModel.name)
            intent.putExtra("pack", pack)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}