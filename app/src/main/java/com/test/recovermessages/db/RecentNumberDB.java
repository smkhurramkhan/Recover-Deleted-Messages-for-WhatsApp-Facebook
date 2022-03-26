package com.test.recovermessages.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.test.recovermessages.models.DataModel;
import com.test.recovermessages.models.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RecentNumberDB {
    private Context context;

    private class datadb extends SQLiteOpenHelper {
        private static final String CREATE_TABLE_ADDED_PACKAGES = "CREATE TABLE table_packages (ID  INTEGER PRIMARY KEY AUTOINCREMENT, package TEXT unique);";
        static final String CREATE_TABLE_FILES = "CREATE TABLE files (_id INTEGER PRIMARY KEY AUTOINCREMENT, files TEXT, whole_time LONG);";
        static final String CREATE_TABLE_MSG = "CREATE TABLE messeges (_id INTEGER PRIMARY KEY AUTOINCREMENT, package TEXT, username TEXT, msg TEXT, small_time TEXT, whole_time LONG);";
        private static final String CREATE_TEBLE_COOL = "CREATE TABLE cool_table (_id INTEGER PRIMARY KEY AUTOINCREMENT, cool_text TEXT unique);";
        private static final String CREATE_TEBLE_QUICK_REPLY = "CREATE TABLE quick_table (_id INTEGER PRIMARY KEY AUTOINCREMENT, quick_reply TEXT);";
        private static final String CREATE_TEBLE_REPEATER = "CREATE TABLE repeater_table (_id INTEGER PRIMARY KEY AUTOINCREMENT, text_repeater TEXT);";
        private static final String CREATE_TEBLE_UNSAVED = "CREATE TABLE num_table (_id INTEGER PRIMARY KEY AUTOINCREMENT, nums TEXT unique);";
        static final String CREATE_USER_WITH_ID = "CREATE TABLE users (_id INTEGER PRIMARY KEY AUTOINCREMENT, package TEXT, username TEXT UNIQUE, read_unread boolean, whole_time DATETIME DEFAULT CURRENT_TIMESTAMP);";
        private static final String ID = "_id";
        private static final String NAME = "recover.db";
        private static final int version = 1;

        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }

        public datadb(Context context) {
            super(context, NAME, null, version);
        }

        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.execSQL(CREATE_TEBLE_UNSAVED);
            sQLiteDatabase.execSQL(CREATE_TEBLE_QUICK_REPLY);
            sQLiteDatabase.execSQL(CREATE_TEBLE_REPEATER);
            sQLiteDatabase.execSQL(CREATE_TEBLE_COOL);
            sQLiteDatabase.execSQL(CREATE_USER_WITH_ID);
            sQLiteDatabase.execSQL(CREATE_TABLE_MSG);
            sQLiteDatabase.execSQL(CREATE_TABLE_FILES);
            sQLiteDatabase.execSQL(CREATE_TABLE_ADDED_PACKAGES);
        }
    }

    public RecentNumberDB(Context context) {
        this.context = context;
    }

    public long addData(String str, String str2, String str3, String str4, String str5) {
        Long l = null;
        try {
            addUser(str2, str, str5);
            SQLiteDatabase writableDatabase = new datadb(this.context).getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("username", str);
            contentValues.put("small_time", str4);
            contentValues.put("whole_time", str5);
            contentValues.put(NotificationCompat.CATEGORY_MESSAGE, str3);
            contentValues.put("package", str2);
            l = writableDatabase.insert("messeges", null, contentValues);
            writableDatabase.close();
        } catch (Exception str6) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("error: ");
            stringBuilder.append(str6.toString());
            Log.d("dblog", stringBuilder.toString());
        }
        return l;
    }

    public void addUser(String str, String str2, String str3) {
        RecentNumberDB view_deleted_messages_dbs_recentNumberDB = this;
        String str4 = str;
        String str5 = str2;
        String str6 = str3;
        String str7 = "package";
        String str8 = "read_unread";
        String str9 = "whole_time";
        String str10 = "username";
        String str11 = "addedusrsnum";
        Log.d(str11, "adding started");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("current time ");
        stringBuilder.append(str6);
        Log.d(str11, stringBuilder.toString());
        try {
            SQLiteDatabase writableDatabase = new datadb(view_deleted_messages_dbs_recentNumberDB.context).getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(str10, str5);
            contentValues.put(str9, str6);
            contentValues.put(str8, Boolean.valueOf(false));
            contentValues.put(str7, str4);
            String str12 = "users";
            if (writableDatabase.query("users", new String[]{str10, str7}, "username=? AND package=?", new String[]{str5, str4}, null, null, null).getCount() == 0) {
                writableDatabase.insert(str12, null, contentValues);
                Log.d(str11, "greater 0");
            } else {
                contentValues.clear();
                contentValues.put(str9, str6);
                contentValues.put(str8, Boolean.valueOf(false));
                writableDatabase.update(str12, contentValues, "username=? AND package=?", new String[]{str5, str4});
                Log.d(str11, "updates");
            }
            writableDatabase.close();
        } catch (Exception e) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("error: ");
            stringBuilder2.append(e.toString());
            Log.d(str11, stringBuilder2.toString());
        }
    }

    public List<HashMap> getUsers(String str) {
        String str2 = "username";
        String str3 = "read_unread";
        List<HashMap> arrayList = new ArrayList();
        try {
            SQLiteDatabase readableDatabase = new datadb(this.context).getReadableDatabase();
            SQLiteDatabase sQLiteDatabase = readableDatabase;
            Cursor query = sQLiteDatabase.query("users", new String[]{str3, str2}, "package=?", new String[]{str}, null, null, "whole_time DESC");
            while (query.moveToNext()) {
                HashMap hashMap = new HashMap();
                Log.d("readunr", query.getString(query.getColumnIndex(str3)));
                hashMap.put("boolean", query.getString(query.getColumnIndex(str3)));
                hashMap.put("string", query.getString(query.getColumnIndex(str2)));
                arrayList.add(hashMap);
            }
            query.close();
            readableDatabase.close();
        } catch (Exception str4) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("error: ");
            stringBuilder.append(str4.toString());
            Log.d("dblog", stringBuilder.toString());
        }
        return arrayList;
    }

    public List<DataModel> getMsg(String str, String str2) {
        String str3 = "small_time";
        String str4 = NotificationCompat.CATEGORY_MESSAGE;
        List<DataModel> arrayList = new ArrayList();
        try {
            SQLiteDatabase writableDatabase = new datadb(this.context).getWritableDatabase();
            SQLiteDatabase sQLiteDatabase = writableDatabase;
            Cursor query = sQLiteDatabase.query("messeges", new String[]{str4, str3}, "username=? AND package=?", new String[]{str, str2}, null, null, null);
            while (query.moveToNext()) {
                arrayList.add(new DataModel(query.getString(query.getColumnIndex(str4)), query.getString(query.getColumnIndex(str3))));
            }
            query.close();
            writableDatabase.close();
        } catch (Exception str5) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("error: ");
            stringBuilder.append(str5.toString());
            Log.d("dblog", stringBuilder.toString());
        }
        return arrayList;
    }

    public boolean isPresent(String str, String str2, String str3) {
        RecentNumberDB view_deleted_messages_dbs_recentNumberDB = this;
        String str4 = str;
        String str5 = NotificationCompat.CATEGORY_MESSAGE;
        String str6 = "dblog";
        boolean z = false;
        try {
            SQLiteDatabase readableDatabase = new datadb(view_deleted_messages_dbs_recentNumberDB.context).getReadableDatabase();
            SQLiteDatabase sQLiteDatabase = readableDatabase;
            Cursor query = sQLiteDatabase.query("messeges", new String[]{str5}, "username=? AND package=?", new String[]{str4, str3}, null, null, "_id DESC", "1");
            if (query.getCount() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("isPresentusername=");
                stringBuilder.append(str4);
                stringBuilder.append("Size=");
                stringBuilder.append(String.valueOf(query.getCount()));
                Log.d(str6, stringBuilder.toString());
                while (query.moveToNext()) {
                    str4 = query.getString(query.getColumnIndex(str5));
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("ispresend greater 0. chat = ");
                    stringBuilder.append(str4);
                    Log.d(str6, stringBuilder.toString());
                    z = str4.equals(str2);
                }
            } else {
                Log.d(str6, "ispresent is 0");
            }
            query.close();
            readableDatabase.close();
        } catch (Exception e) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("error: ");
            stringBuilder2.append(e.toString());
            Log.d(str6, stringBuilder2.toString());
        }
        return z;
    }

    public List<UserModel> getHomeList(String s) {
        RecentNumberDB view_deleted_messages_dbs_recentNumberDB = this;
        ArrayList<UserModel> list = new ArrayList<UserModel>();
        try {
            List<HashMap> users = this.getUsers(s);
            SQLiteDatabase readableDatabase = new RecentNumberDB.datadb(view_deleted_messages_dbs_recentNumberDB.context).getReadableDatabase();
            for (int i = 0; i < users.size(); ++i) {
                Cursor query = readableDatabase.query("messeges", new String[] { "msg", "small_time" }, "username=? AND package=?", new String[] { users.get(i).get("string").toString(), s }, (String)null, (String)null, "_id DESC", "1");
                if (query != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("users lenght ");
                    sb.append(users.size());
                    Log.d("emptylog", sb.toString());
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("cursor lenght ");
                    sb2.append(String.valueOf(query.getCount()));
                    Log.d("emptylog", sb2.toString());
                    if (query.getCount() == 0) {
                        list.add(new UserModel(users.get(i).get("string").toString(), "no recent msg", "", 1));
                    }
                    else {
                        while (true) {
                            int moveToNext = query.moveToNext() ? 1 : 0;
                            if (moveToNext == 0) {
                                break;
                            }
                            list.add(new UserModel(users.get(i).get("string").toString(), query.getString(0), query.getString(moveToNext), Integer.parseInt(users.get(i).get("boolean").toString())));
                            Log.d("emptylog", query.getString(0));
                        }
                    }
                    query.close();
                }
                else {
                    Log.d("emptylog", "cursor null");
                }
            }
            readableDatabase.close();
            return list;
        }
        catch (Exception ex) {
            return list;
        }
    }

    public void addPackages(String s) {
        try {
            SQLiteDatabase writableDatabase = new RecentNumberDB.datadb(this.context).getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("package", s);
            writableDatabase.insert("table_packages", (String)null, contentValues);
            writableDatabase.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<String> getAllPackages() {
        String[] array = { "com.whatsapp", "com.whatsapp.w4b", "com.gbwhatsapp", "com.facebook.lite", "com.facebook.orca", "com.facebook.mlite", "org.telegram.messenger" };
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> list2 = new ArrayList<String>();
        try {
            Cursor query = new RecentNumberDB.datadb(this.context).getReadableDatabase().query("table_packages", new String[] { "package" }, (String)null, (String[])null, (String)null, (String)null, (String)null);
            boolean moveToNext;
            while (true) {
                moveToNext = query.moveToNext();
                if (!moveToNext) {
                    break;
                }
                list.add(query.getString(0));
            }
            query.close();
            int n = moveToNext ? 1 : 0;
            int i;
            while (true) {
                i = (moveToNext ? 1 : 0);
                if (n >= array.length) {
                    break;
                }
                if (list.contains(array[n])) {
                    list2.add(array[n]);
                }
                ++n;
            }
            while (i < list.size()) {
                if (!list2.contains(list.get(i))) {
                    list2.add(list.get(i));
                }
                ++i;
            }
            return list2;
        }
        catch (Exception ex) {
            return list2;
        }
    }

    public void removePackageAndMsg(ArrayList<String> list) {
        try {
            SQLiteDatabase writableDatabase = new RecentNumberDB.datadb(this.context).getWritableDatabase();
            Iterator<String> iterator = list.iterator();
            while (true) {
                int hasNext = iterator.hasNext() ? 1 : 0;
                if (hasNext == 0) {
                    break;
                }
                String s = iterator.next();
                String[] array = new String[hasNext];
                array[0] = s;
                writableDatabase.delete("table_packages", "package=?", array);
                String[] array2 = new String[hasNext];
                array2[0] = s;
                writableDatabase.delete("users", "package=?", array2);
                String[] array3 = new String[hasNext];
                array3[0] = s;
                writableDatabase.delete("messeges", "package=?", array3);
            }
            writableDatabase.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
