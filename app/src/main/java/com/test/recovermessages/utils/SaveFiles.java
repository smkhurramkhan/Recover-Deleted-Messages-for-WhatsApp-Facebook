package com.test.recovermessages.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.test.recovermessages.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SaveFiles {

    public static String IMAGE = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "WhatsApp/Media/WhatsApp Images").getAbsolutePath();
    public static String AUDIO = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "WhatsApp/Media/WhatsApp Audio").getAbsolutePath();
    public static String VIDEO = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "WhatsApp/Media/WhatsApp Video").getAbsolutePath();
    public static String DOCUMENT = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "WhatsApp/Media/WhatsApp Documents").getAbsolutePath();
    public static String GIF = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "WhatsApp/Media/WhatsApp Animated Gifs").getAbsolutePath();
    public static String[] ArrayFiles = new String[]{IMAGE, VIDEO, GIF, DOCUMENT, AUDIO};

    public void save(final String s, final Context context) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                String newFileName;
                try {
                    if (ArrayFiles.length >= 0) {
                        if (s.endsWith(".lock")) {
                            String s0 = s.substring(1);
                            int i = s0.indexOf(".lock");
                            newFileName = s0.substring(0, i);
                        } else {
                            newFileName = s;
                        }
                        File externalStorageDirectory = Environment.getExternalStorageDirectory();
                        File file = new File(ArrayFiles[0] + "/" + newFileName);
                        if (file.exists()) {
                            File file1 = new File(externalStorageDirectory, context.getResources().getString(R.string.app_name) + "/.Cached Files");
                            if (!file1.exists()) {
                                file1.mkdirs();
                            }
                            File file2 = new File(file1.getAbsolutePath() + "/" + newFileName + ".cached");
                            if (!file2.exists()) {
                                InputStream fileInputStream = new FileInputStream(file);
                                OutputStream fileOutputStream = new FileOutputStream(file2);
                                byte[] bArr = new byte[1024];
                                while (true) {
                                    int read = fileInputStream.read(bArr);
                                    if (read <= 0) {
                                        break;
                                    }
                                    fileOutputStream.write(bArr, 0, read);
                                }
                                fileInputStream.close();
                                fileOutputStream.close();
                            }
                        } else {
                            Log.d("savefileslog", "wa file not exists");
                        }
                    }
                } catch (Exception e) {
                    Log.d("savefileslog", "copy error: " + e.toString());
                }
                return null;
            }
        }.execute();
    }

    public void move(final String str, final Context context) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                int i = 0;
                boolean exists;
                try {
                    File externalStorageDirectory = Environment.getExternalStorageDirectory();
                    File file = new File(externalStorageDirectory, (context.getResources().getString(R.string.app_name) + "/.Cached Files/") + str + ".cached");
                    exists = file.exists();
                    if (exists) {
                        try {
                            File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getResources().getString(R.string.app_name) + "/" + str);
                            InputStream fileInputStream = new FileInputStream(file);
                            OutputStream fileOutputStream = new FileOutputStream(file2);
                            byte[] bArr = new byte[1024];
                            while (true) {
                                int read = fileInputStream.read(bArr);
                                if (read <= 0) {
                                    break;
                                }
                                fileOutputStream.write(bArr, 0, read);
                            }
                            fileInputStream.close();
                            fileOutputStream.close();
                            file.delete();
                        } catch (Exception e2) {
                            Log.d("savefileslog", "error moving- " + e2.toString());
                        }
                        return exists ? 1 : 0;
                    }
                    return exists ? 1 : 0;
                } catch (Exception e3) {
                    exists = false;
                    Log.d("savefileslog", "error moving- " + e3.toString());
                    return exists ? 1 : 0;
                }
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                try {
                    if (integer == 1) {
                        new sendNotification().sendBackground(context, "Deleted File Found", "Tap to check deleted message now.");
                    }
                    final Intent intent = new Intent(context.getString(R.string.files));
                    intent.putExtra(context.getString(R.string.files), context.getString(R.string.refresh_files));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }
}
