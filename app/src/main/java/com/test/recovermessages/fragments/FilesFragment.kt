package com.test.recovermessages.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.recovermessages.R
import com.test.recovermessages.adapters.SaverAdapter
import com.test.recovermessages.models.FilesModel
import com.test.recovermessages.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class FilesFragment : Fragment() {
    var booleanArrayList: ArrayList<Boolean> = arrayListOf()
    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("fragfiles", "received broadcast:--> " + intent.getStringExtra("files"))
            if (intent.getStringExtra(context.getString(R.string.files)) == context.getString(R.string.refresh_files)) {
                backgroundTask(false)
            } else if (intent.getStringExtra(context.getString(R.string.files)) == context.getString(
                    R.string.remove_permission_framgent
                )
            ) {
                removePermissionFragment()
            }
        }
    }
    var context: Activity? = null
    var emptyTextView: TextView? = null
    var filesList: MutableList<FilesModel> = mutableListOf()
    var files_adapter: SaverAdapter? = null
    var linearLayout: LinearLayout? = null
    var mProgressBar: ProgressBar? = null
    var recyclerView: RecyclerView? = null
    var size = 0
    var utils: Utils? = null
    fun newInstance(str: String?): FilesFragment {
        val view_deleted_messages_Files_fragment_ = FilesFragment()
        val bundle = Bundle()
        bundle.putString("pack", str)
        view_deleted_messages_Files_fragment_.arguments = bundle
        return view_deleted_messages_Files_fragment_
    }

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        linearLayout = LinearLayout(activity)
        linearLayout!!.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        linearLayout!!.gravity = Gravity.CENTER
        return linearLayout
    }

    override fun onActivityCreated(bundle: Bundle?) {
        super.onActivityCreated(bundle)
        utils = Utils(context)
        try {
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                broadcastReceiver, IntentFilter(
                    requireContext().getString(R.string.files)
                )
            )
            if (VERSION.SDK_INT >= 23) {
                val checkStoragePermission = utils!!.checkStoragePermission()
                if (checkStoragePermission) {
                    backgroundTask(checkStoragePermission)
                } else {
                    utils!!.isNeedGrantPermission
                }
            } else {
                backgroundTask(true)
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            context = activity
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun backgroundTask(b: Boolean) {
        doBackgroundTask(b)
    }

    private val files: Array<File?>
        get() {
            val file: File = if (requireArguments().getString("pack") == "com.whatsapp") {
                File(
                    Environment.getExternalStorageDirectory().absolutePath,
                    requireContext().getString(R.string.app_name)
                )
            } else {
                val absolutePath = Environment.getExternalStorageDirectory().absolutePath
                val sb =
                    requireContext().getString(R.string.app_name) + "/" + requireArguments().getString(
                        "pack"
                    )
                File(absolutePath, sb)
            }
            val exists = if (file.exists()) 1 else 0
            if (exists != 0) {
                val listFiles: Array<File?> = try {
                    file.listFiles()
                } catch (ex: Exception) {
                    arrayOfNulls(0)
                }
                return listFiles
            }
            return arrayOfNulls(exists)
        }

    private fun removePermissionFragment() {
        backgroundTask(true)
        sendBroadcast(true)
    }

    private fun sendBroadcast(z: Boolean) {
        val intent = Intent(requireContext().getString(R.string.noti_obserb))
        intent.putExtra(requireContext().getString(R.string.noti_obserb), z.toString())
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
    }

    private val mRecyclerView: RecyclerView
        get() {
            recyclerView = RecyclerView(requireContext())
            recyclerView!!.layoutParams = LinearLayout.LayoutParams(-1, -1)
            recyclerView!!.layoutManager = GridLayoutManager(context, 2)
            return recyclerView!!
        }

    private fun getProgressBar(): ProgressBar {
        mProgressBar = ProgressBar(context)
        val layoutParams = LinearLayout.LayoutParams(-2, -2)
        mProgressBar!!.layoutParams = layoutParams
        layoutParams.gravity = 17
        return mProgressBar!!
    }

    private fun getEmptyText(): TextView {
        emptyTextView = TextView(context)
        emptyTextView!!.text = requireContext().getString(R.string.only_empty_text)
        val layoutParams = LinearLayout.LayoutParams(-1, -2)
        emptyTextView!!.layoutParams = layoutParams
        emptyTextView!!.gravity = 17
        layoutParams.setMargins(8, 8, 8, 8)
        return emptyTextView!!
    }

    private fun doBackgroundTask(b: Boolean) {
        lifecycleScope.launch {
            onPreExecute(b)
            doInBackground(b)
            onPostExecute(b)
        }

    }

    private fun doInBackground(load_data: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Thread.sleep(200)
            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            }
            if (load_data) {
                filesList = mutableListOf()
            } else {
                filesList.clear()
            }
            try {
                val list = Arrays.asList(*files)
                Collections.sort(list, Comparator { o1, o2 ->
                    val n = o2!!.lastModified() - o1!!.lastModified()
                    if (n > 0L) {
                        return@Comparator 1
                    }
                    if (n == 0L) {
                        0
                    } else -1
                })
                size = list.size
                for (i in list.indices) {
                    val directory = list[i]!!.isDirectory
                    if (!directory) {
                        val name = list[i]?.name
                        val s: String = if (!list[i]?.name!!.endsWith(".jpg")
                            && !list[i]?.name!!.endsWith(".jpeg")
                            && !list[i]?.name!!.endsWith(".png")
                        ) {
                            if (!list[i]?.name!!.endsWith(".mp4")
                                && !list[i]?.name!!.endsWith(".3gp")
                            ) {
                                "unknown"
                            } else {
                                "video"
                            }
                        } else {
                            "image"
                        }
                        val n = list[i]!!.length() / 1024L
                        var n2 = 0L
                        var b: Boolean
                        if (n > 1024L) {
                            n2 = n / 1024L
                            b = directory
                        } else {
                            b = true
                        }
                        val s2: String = if (b) {
                            val sb = "$n KB"
                            sb
                        } else {
                            val sb2 = "$n2 MB"
                            sb2
                        }
                        filesList!!.add(
                            FilesModel(
                                name!!,
                                s,
                                s2,
                                list[i] as File,
                                directory
                            )
                        )
                    }
                }
                for (j in filesList.indices) {
                    booleanArrayList.add(false)
                }

            } catch (ex2: Exception) {
                ex2.printStackTrace()
            }
        }
    }

    private fun onPostExecute(load_data: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (load_data) {
                if (filesList.size > 0) {
                    linearLayout!!.addView(mRecyclerView as View)
                    files_adapter = SaverAdapter((context as Context?)!!, filesList)
                    recyclerView!!.adapter = files_adapter
                    linearLayout!!.removeView(mProgressBar as View?)
                } else {
                    linearLayout!!.removeView(mProgressBar as View?)
                    linearLayout!!.addView(getEmptyText() as View)
                }
            } else {
                if (recyclerView != null) {
                    files_adapter?.notifyDataSetChanged()
                } else {
                    linearLayout?.addView(mRecyclerView)
                    files_adapter = SaverAdapter((context as Context?)!!, filesList)
                    recyclerView?.adapter = files_adapter
                    linearLayout?.removeView(emptyTextView as View?)
                }
            }
            Log.d("fflog", "below post")
        }
    }

    private fun onPreExecute(load_data: Boolean) {
        if (load_data) {
            linearLayout?.addView(getProgressBar())
        }
    }

}