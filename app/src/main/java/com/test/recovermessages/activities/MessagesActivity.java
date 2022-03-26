package com.test.recovermessages.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.test.recovermessages.R;
import com.test.recovermessages.adapters.MessagesAdapter;
import com.test.recovermessages.db.RecentNumberDB;
import com.test.recovermessages.models.DataModel;

import java.lang.ref.WeakReference;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    private static class loadMsg extends AsyncTask<String, Void, List<DataModel>> {
        WeakReference<MessagesActivity> mwr;

        private loadMsg(WeakReference<MessagesActivity> weakReference) {
            this.mwr = weakReference;
        }

        protected List<DataModel> doInBackground(String... strArr) {
            return new RecentNumberDB((mwr.get()).getApplicationContext()).getMsg(strArr[0], strArr[1]);
        }

        protected void onPostExecute(List<DataModel> list) {
            super.onPostExecute(list);
            try {
                ((MessagesActivity) mwr.get()).findViewById(R.id.progressBar).setVisibility(View.GONE);
                RecyclerView recyclerView = (RecyclerView) ((MessagesActivity) mwr.get()).findViewById(R.id.recyclerView);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(((MessagesActivity) mwr.get()).getApplicationContext());
                linearLayoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(new MessagesAdapter((mwr.get()).getApplicationContext(), list));
            } catch (Exception list2) {
                list2.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messeges);
        String stringExtra = getIntent().getStringExtra("name");
        new MessagesActivity.loadMsg(new WeakReference(this)).execute(stringExtra, getIntent().getStringExtra("pack"));
        setTitle(stringExtra);
    }

}
