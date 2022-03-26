package com.test.recovermessages.adapters

import android.content.pm.PackageInfo
import com.test.recovermessages.activities.Setup
import androidx.recyclerview.widget.RecyclerView
import android.graphics.drawable.Drawable
import android.content.pm.PackageManager
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.test.recovermessages.R
import android.view.ViewGroup
import com.test.recovermessages.adapters.AppListAdapter.AppHolder
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.util.ArrayList

class AppListAdapter(
    private val list_info: List<PackageInfo>,
    private val context: Setup,
    private val addedlist: MutableList<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val checklist = ArrayList<Boolean>()
    var drawable: Drawable? = null
    private val packageManager: PackageManager

    private inner class AppHolder(view: View) : RecyclerView.ViewHolder(view) {
        var check: ImageView = view.findViewById<View>(R.id.check) as ImageView
        var icon: ImageView = view.findViewById<View>(R.id.icon) as ImageView
        var main: LinearLayout = view.findViewById<View>(R.id.main) as LinearLayout
        var name: TextView = view.findViewById<View>(R.id.name) as TextView

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return AppHolder(
            LayoutInflater.from(context).inflate(R.layout.item_app_list, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val appHolder = viewHolder as AppHolder
        appHolder.name.text =
            packageManager.getApplicationLabel(list_info[i].applicationInfo).toString()
        appHolder.icon.setImageDrawable(packageManager.getApplicationIcon(list_info[i].applicationInfo))
        val contains = addedlist.contains(list_info[i].packageName)
        val str1 = "contlog"
        if (contains) {
            checklist[i] = contains
            addedlist.remove(list_info[i].packageName)
        } else {
            Log.d(str1, "fal")
        }
        if (checklist[i]) {
            Glide.with(context).load(R.drawable.correct).into(appHolder.check)
        } else {
            Glide.with(context).load(R.drawable.circle_stroke).into(appHolder.check)
        }
        appHolder.main.setOnClickListener {
            val sb = "clicked " + viewHolder.getAdapterPosition()
            val str = "cheloh"
            Log.d(str, sb)
            if (checklist[viewHolder.getAdapterPosition()]) {
                checklist[viewHolder.getAdapterPosition()] = false
                Glide.with(context).load(R.drawable.circle_stroke).into(appHolder.check)
            } else {
                Glide.with(context).load(R.drawable.correct).into(appHolder.check)
                checklist[viewHolder.getAdapterPosition()] = true
                val str2 =
                    "click = " + viewHolder.getAdapterPosition() + " " + checklist[viewHolder.getAdapterPosition()]
                Log.d(str, str2)
            }
            context.addtolist(list_info[viewHolder.getAdapterPosition()].packageName)
        }
    }

    override fun getItemCount(): Int {
        return list_info.size
    }

    init {
        for (i in list_info.indices) {
            checklist.add(false)
        }
        packageManager = context.packageManager
    }
}