package com.test.recovermessages.adapters

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.recovermessages.App
import com.test.recovermessages.R
import com.test.recovermessages.models.FilesModel
import com.test.recovermessages.utils.AdmobHelper
import java.io.File

class SaverAdapter(private val context: Context, private val fileslist: MutableList<FilesModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class itemHolder(view: View) : RecyclerView.ViewHolder(view) {
        var icon: ImageView = view.findViewById<View>(R.id.icon) as ImageView
        var main: LinearLayout = view.findViewById<View>(R.id.main) as LinearLayout
        var name: TextView = view.findViewById<View>(R.id.name) as TextView
        var play: ImageView = view.findViewById<View>(R.id.play) as ImageView
        var size: TextView = view.findViewById<View>(R.id.size) as TextView
        var tick: ImageView = view.findViewById<View>(R.id.tick) as ImageView
        var shareID: ImageView = view.findViewById<View>(R.id.shareID) as ImageView

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        return itemHolder(
            LayoutInflater.from(context).inflate(R.layout.grid_jtem, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val itemHolder = viewHolder as itemHolder
        val filesModel = fileslist[i]
        Glide.with(context).load(filesModel.file).centerCrop().placeholder(R.drawable.placeholder)
            .into(itemHolder.icon)
        val type = filesModel.type
        itemHolder.main.setOnClickListener {
            if (App.adCount % 4 == 0) {
                AdmobHelper.showInterstitialAd(context, AdmobHelper.ADSHOWN)
            }
            App.adCount++
            val file = File(filesModel.file.path)
            val uri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                file
            )
            if (type == "image") {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intent.setDataAndType(uri, "image/*")
                context.startActivity(intent)
            } else if (type == "video") {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intent.setDataAndType(uri, "video/*")
                context.startActivity(intent)
            }
        }
        itemHolder.shareID.setOnClickListener {
            if (App.adCount % 4 == 0) {
                AdmobHelper.showInterstitialAd(context, AdmobHelper.ADSHOWN)
            }
            App.adCount++
            val mainUri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                filesModel.file
            )
            val sharingIntent: Intent
            sharingIntent = Intent("android.intent.action.SEND")
            sharingIntent.type = "image/*"
            sharingIntent.putExtra("android.intent.extra.STREAM", mainUri)
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                context.startActivity(Intent.createChooser(sharingIntent, "Share Image using"))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    "No application found to open this file.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return fileslist.size
    }
}