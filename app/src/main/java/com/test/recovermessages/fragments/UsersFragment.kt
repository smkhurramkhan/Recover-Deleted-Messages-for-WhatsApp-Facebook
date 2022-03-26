package com.test.recovermessages.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.recovermessages.R
import com.test.recovermessages.adapters.UsersAdapter
import com.test.recovermessages.db.RecentNumberDB
import com.test.recovermessages.models.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersFragment : Fragment() {
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("onreceivelog", intent.getStringExtra("refresh")!!)
            updateList()
        }
    }

    private var linearLayout: LinearLayout? = null
    private var list: List<UserModel>? = null
    private var pack: String? = null
    private var recyclerView: RecyclerView? = null
    private var usersAdapter: UsersAdapter? = null
    private var waiting: LinearLayout? = null
    private var emptyText: TextView? = null
    fun newInstance(str: String?): UsersFragment {
        val users_fragment = UsersFragment()
        val bundle = Bundle()
        bundle.putString("pack", str)
        users_fragment.arguments = bundle
        return users_fragment
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
        linearLayout!!.orientation = LinearLayout.VERTICAL
        return linearLayout
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pack = requireArguments().getString("pack")
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter("refresh"))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        doBackgroundTask()
        waiting = requireView().findViewById(R.id.waiting)
    }

    private fun doBackgroundTask() {
        lifecycleScope.launch {
            doInBackground()
            onPostExecute()
        }

    }

    private fun onPostExecute() {
        lifecycleScope.launch(Dispatchers.Main) {
            if (list?.isNotEmpty() == true) {
                linearLayout?.addView(mRecyclerView)
                usersAdapter = UsersAdapter(requireContext(), list!!, pack!!)
                recyclerView?.adapter = usersAdapter
                if (waiting != null) {
                    linearLayout?.removeView(waiting)
                }
            }
            addWaiting()
        }
    }

    private fun doInBackground() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                Thread.sleep(200)
                list = RecentNumberDB(context).getHomeList(requireArguments().getString("pack"))
            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            }
        }
    }

    private fun updateList() {
        lifecycleScope.launch {
            val updateListBackgroundTask = updateListBackgroundTask()
            updateListOnPostExecute(updateListBackgroundTask)
        }

    }

    private suspend fun updateListBackgroundTask(): List<UserModel>? {
        return withContext(Dispatchers.IO) {
            RecentNumberDB(context).getHomeList(requireArguments().getString("pack"))
        }
    }

    private fun updateListOnPostExecute(UserModels: List<UserModel>?) {
        lifecycleScope.launch {
            if (UserModels != null) {
                if (UserModels.isNotEmpty()) {
                    if (recyclerView == null) {
                        linearLayout?.addView(mRecyclerView)
                        linearLayout?.removeView(emptyText)
                    }
                    recyclerView?.adapter = UsersAdapter(requireContext(), UserModels, pack!!)
                }
            }
        }

    }

    private val mRecyclerView: RecyclerView
        get() {
            recyclerView = RecyclerView(requireContext())
            recyclerView?.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            recyclerView?.layoutManager = LinearLayoutManager(context)
            return recyclerView!!
        }

    private fun addWaiting() {
        emptyText = TextView(context)
        emptyText?.text = requireContext().getString(R.string.empty_message)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        emptyText?.layoutParams = layoutParams
        emptyText?.gravity = Gravity.CENTER
        layoutParams.setMargins(8, 8, 8, 8)
        linearLayout?.addView(emptyText)
    }
}