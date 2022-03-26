package com.test.recovermessages.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.test.recovermessages.R;
import com.test.recovermessages.db.RecentNumberDB;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SaveMsg {
    private int del;

    static class savetoDb extends AsyncTask<String, Void, Long> {
        WeakReference<Context> contextWeakReference;
        int del;

        public savetoDb(WeakReference<Context> weakReference, int i) {
            this.contextWeakReference = weakReference;
            this.del = i;
        }

        protected Long doInBackground(String... strArr) {
            Long n = null;
            String s = strArr[2];
            String s2 = strArr[0];
            Long value;
            try {
                Log.d("newtitilelog", "oldtitle:" + s2);
                String s3 = strArr[1];
                Log.d("newmsglog", s2 + " " + s3);
                String s4 = s3;
                if (s3.equals((contextWeakReference.get()).getString(R.string.thismsgwasdeleted))) {
                    s4 = "\ud83d\udc46 new deleted message detected\u26a0";
                }
                int contains = s2.contains("messages):") ? 1 : 0;
                String s5;
                if (contains != 0) {
                    s5 = s2.substring(0, s2.indexOf(" ("));
                    Log.d("newtitilelog", "newtitle:" + s5);
                } else if (s2.contains(":")) {
                    s5 = s2.substring(contains, s2.indexOf(":"));
                } else {
                    s5 = s2;
                }
                value = n;
                if (!s5.equals("WhatsApp")) {
                    value = n;
                    if (!s4.contains("new messages") && !s4.contains("deleted")) {
                        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd", Locale.US);
                        String substring = String.valueOf(format).substring(11, 13);
                        String substring2 = String.valueOf(format).substring(14, 16);
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append(simpleDateFormat.format(Calendar.getInstance().getTime()));
                        sb4.append(" ");
                        sb4.append(substring);
                        sb4.append(":");
                        sb4.append(substring2);
                        String string = sb4.toString();
                        RecentNumberDB recentNumberDB = new RecentNumberDB(contextWeakReference.get());
                        value = n;
                        if (!recentNumberDB.isPresent(s5, s4, s)) {
                            value = recentNumberDB.addData(s5, s, s4, string, format);
                        }
                    } else if (s4.contains("deleted")) {
                        value = 1L;
                    }
                }
            } catch (Exception ex) {
                Log.d("errorlog", "savemsg bg" + ex.toString());
                value = n;
            }
            return value;
        }

        protected void onPostExecute(Long l) {
            super.onPostExecute(l);
            Log.d("errorlog", "numadded: " + String.valueOf(l));
            if (l != null && l > 0L) {
                try {
                    if (del == 1) {
                        new sendNotification().sendBackground(contextWeakReference.get(), (contextWeakReference.get()).getString(R.string.message_deleted), (contextWeakReference.get()).getString(R.string.message_was_deleted));
                    }
                    Intent intent = new Intent("refresh");
                    intent.putExtra("refresh", "refresh");
                    LocalBroadcastManager.getInstance(contextWeakReference.get()).sendBroadcast(intent);
                } catch (Exception ex) {
                    Log.d("errorlog", "savemsg post" + ex.toString());
                }
            }
        }
    }

    public SaveMsg(Context context, String str, String str2, String str3) {
        boolean equals = str2.equals(context.getString(R.string.thismsgwasdeleted));
        if (equals) {
            del = 1;
        } else {
            del = 0;
        }
        new savetoDb(new WeakReference(context), del).execute(str, str2, str3);
    }
}
