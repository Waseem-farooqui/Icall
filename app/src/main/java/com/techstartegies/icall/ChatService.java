package com.techstartegies.icall;

/**
 * Created by HP on 11/27/2015.
 */

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.techstartegies.util.SqliteUtil;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.roster.Roster;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatService extends Service {

    static int p_notification;
    AbstractXMPPConnection mConnection;
    Roster roster;
    static int offline = 0;
    static int mId = 0;
    static ChatConnection con;
    static MyTimerTask myTask;
    static TimerTask cTask, smsTask; // To check internet Connection
    static Timer myTimer, cTimer, smsTimer;
    static boolean active = false;
    public static long when;
    static Context context;
    static boolean stopTimer = false;
    static int joinFirstTime=0;
    int stp = 0;

    static int started = 0;
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;

    static NotificationManager mNotifyManager;
    static NotificationCompat.Builder mBuilder;


    /**
     * @param intent
     * @return
     */

    @Override


    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * @param intent
     * @param flags
     * @param startId
     * @return
     */


    public int onStartCommand(Intent intent, int flags, int startId) {


        started = 1;

        //timer to check network connection every 15 seconds
        try {
            cTimer = new Timer();
            cTimer.scheduleAtFixedRate(cTask, 1000, 5000);
        } catch (Exception e) {
            //    Toast.makeText(ChatService.this, "net Disconnected", Toast.LENGTH_LONG).show();

        }

        try{
            smsTimer =new Timer();
            smsTimer.scheduleAtFixedRate(smsTask,15000,15000);
        } catch (Exception e){
            Log.e("Sms Timer", e.getMessage());
        }
        return START_STICKY;
    }

//==================================================================================================

    /**
     * On Create Function of Service
     */
    public void onCreate() {
        super.onCreate();

        con = (ChatConnection) getApplicationContext();//m.connect();
        if (!ChatConnection.login) {
            /**
             *  Place Call to NetworkConnection the very first time service get started
             */
            new NetworkOperation().execute();
        }

        /**
         * Code will check the message status
         */

        smsTask = new TimerTask() {
            @Override
            public void run() {
                checkSmsStatus();
            }
        };

        /**
         *  Code to check internet connection repetedly just like ping
         */
        cTask = new TimerTask() {

            @Override
            public void run() {

                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {

                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.post(new Runnable() {

                        @Override
                        public void run() {


                            if (ChatConnection.login == false) {

                                Toast.makeText(ChatService.this, "net Connected", Toast.LENGTH_LONG).show();

                                ChatConnection.login = true;
                                ChatService.stopTimer = false;

                                new NetworkOperation().execute();
                                stp = 0;


                            }

                            //  for (int j = 0; j < ChatConnection.online_list.length; j++)
                            {
                                // Log.e("Online Contacts",""+ChatConnection.online_list[j]);
                            }
                        }
                    });


                    //saveData();
                } else {

                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.post(new Runnable() {

                        @Override
                        public void run() {

                            if (stp == 0) {
                                Toast.makeText(ChatService.this, "net Disconnected", Toast.LENGTH_LONG).show();
                                stp = 1;
                            }
                            // Disconnect Client on being offline so that we could reconnect on online
                            try {
                                con.mConnection.disconnect();

                            } catch (Exception e) {

                            }
                            con.mConnection = null;
                            ChatConnection.login = false;

                        }
                    });


                }

            }
        };
        context = ChatService.this;
        sharedPreferences = context.getSharedPreferences("app", MODE_WORLD_WRITEABLE);
        editor = sharedPreferences.edit();

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat.Builder(getApplicationContext());


        Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();

    }

    /**
     * @return
     */

    public static int getPropId() {
        int id = sharedPreferences.getInt("id", 0);
        Log.e("Getting Value of id ", id + "");
        return id;
    }

    /**
     * @param id
     */
    public static void setPropId(int id) {

        Log.e("Setting Value of id", id + "");
        editor.putInt("id", id);
        editor.commit();
    }


    //===================================== Setting Notifications and push Notifications=======================

    /**
     * To set and build notifications
     *
     * @param msg
     */

    public static void setNotification(String msg) {

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setContentTitle("New Message")
                .setContentText("" + msg)

                .setTicker("ICall Urgent Message")
                .setLights(Color.BLUE, 3000, 5000)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 500, 500, 500, 500})
                .setSmallIcon(R.drawable.icall)
                .setSound(soundUri);

        Intent myIntent = new Intent(context, chat.class);
        myIntent.putExtra("ChatId", "Fakhar");
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent2 = PendingIntent.getActivity(context, 0,
                myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent2);
        // change to stop push notification
        if ((p_notification == 1) && (active == false) && (stopTimer == false)) {

            Log.e("Building Notification", msg);
            mNotifyManager.notify((int) when, mBuilder.build());

            p_notification = 0;
        } else if ((!msg.equals("null")) && (!msg.equals("Urgent ICall Message"))) {
            mNotifyManager.notify(0, mBuilder.build());
            Log.e(" Notification", msg);
        }
    }

    /**
     *
     */

    static void pushNotification() {
        myTask = new MyTimerTask();
        myTimer = new Timer();

        myTimer.schedule(myTask, 1000, 5000); //start after 1 seconds repeat ever 15 sec

    }

    static class MyTimerTask extends TimerTask {
        public void run() {

            generateNotification();
        }
    }

    private static void generateNotification() {


        when = System.currentTimeMillis();

        //to stop push notification
        if (stopTimer == false)
            p_notification = 1;

        setNotification("Urgent ICall Message");


    }

//==========================================================================================================================

    /**
     * On service Destroy() Add start Service here as well in case if service get stop then restart it and establish connection again
     */

    public void onDestroy() {

        super.onDestroy();
        ChatService.this.startService(new Intent(getApplication(), ChatService.class));

    }

    //++==================================== NetworK Operation Removed From Connection to Service+=========

    /**
     * Need to place this Network Operation Class here without ay Constructor , Copy it As I did
     */

    //============================================================================================

    public class NetworkOperation extends AsyncTask<Object, Void, Boolean> {


        protected Boolean doInBackground(Object... obj) {

            con.establishConnection();
            mConnection = con.getConnection();
            con.readRoster();
            roster = con.getRoster();


            con.flag = 1;
            con.sendStatus(ChatConnection.sender);
            //con.resendOfflineMessages();

            return true;
        }


        protected void onPostExecute(Boolean result) {

        }
    }

    public void checkSmsStatus(){


        SqliteUtil database = new SqliteUtil(getApplicationContext());
        List<String> messages = new ArrayList<String>();
        int numberOfRows=database.numberOfRows();
        Cursor res=null;
        if(numberOfRows!=0) {
            res = database.getAllRecords();
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                if (res.getInt(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_SR)) == 1) {

                    String status = res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_STATUS));
                    if (status.equals("s") ) {
                        Log.e("Sms Testing", "In check Sms Status");
                        messages.add((res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_MESSAGE))));
                        database.updateStatus(res.getInt(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_ID)), "v");

                    }
                }
                res.moveToNext();
            }
            database.close();
            for (String message : messages){
                sendMessage("03125343828",message);


            }
        }
    }

    public void sendMessage(String phoneNo, String msg) {

        try {

            String SENT = "sent";
            String DELIVERED = "delivered";

            Intent sentIntent = new Intent(SENT);
     /*Create Pending Intents*/
            PendingIntent sentPI = PendingIntent.getBroadcast(
                    getApplicationContext(), 0, sentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Intent deliveryIntent = new Intent(DELIVERED);

            PendingIntent deliverPI = PendingIntent.getBroadcast(
                    getApplicationContext(), 0, deliveryIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
     /* Register for SMS send action */
            registerReceiver(new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String result = "";

                    switch (getResultCode()) {

                        case Activity.RESULT_OK:
                            result = "Transmission successful";
                            break;
                        case android.telephony.SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            result = "Transmission failed";
                            break;
                        case android.telephony.SmsManager.RESULT_ERROR_RADIO_OFF:
                            result = "Radio off";
                            break;
                        case android.telephony.SmsManager.RESULT_ERROR_NULL_PDU:
                            result = "No PDU defined";
                            break;
                        case android.telephony.SmsManager.RESULT_ERROR_NO_SERVICE:
                            result = "No service";
                            break;
                    }

                    Toast.makeText(getApplicationContext(), result,
                            Toast.LENGTH_LONG).show();
                }

            }, new IntentFilter(SENT));
     /* Register for Delivery event */
            registerReceiver(new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Toast.makeText(getApplicationContext(), "Deliverd",
                            Toast.LENGTH_LONG).show();
                }

            }, new IntentFilter(DELIVERED));

      /*Send SMS*/
            android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, sentPI,
                    deliverPI);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),
                    ex.getMessage().toString(), Toast.LENGTH_LONG)
                    .show();
            ex.printStackTrace();
        }
    }


    /**
     * *********************************************************************************************
     * ******************** Inner Class for Managinging BroadCast **********************************
     * *********************************************************************************************
     */
    class ReceiveMessage extends BroadcastReceiver {

        private Intent myIntent = null;
        private Context myContext = null;

        // ***********************On Receive Function of BroadCast *********************************
        @Override
        public void onReceive(Context context, Intent intent) {
            myIntent = intent;
            myContext = context;
            //checkSmsStatus();
        }

        public void checkSmsStatus(){

            SqliteUtil database = new SqliteUtil(getApplicationContext());
            List<String> messages = new ArrayList<String>();
            int numberOfRows=database.numberOfRows();
            Cursor res=null;
            if(numberOfRows!=0) {
                res = database.getAllRecords();
                res.moveToFirst();
                while (res.isAfterLast() == false) {
                    if (res.getInt(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_SR)) == 0) {
                        String status = res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_STATUS));
                        if (status.equals("s")) {
                            messages.add((res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_MESSAGE))));

                        }
                    }
                    res.moveToNext();
                }
                for (String message : messages){

                    sendMessage("03125343828",message);
                }
            }
        }

        public void sendMessage(String phoneNo, String msg) {

            try {

                String SENT = "sent";
                String DELIVERED = "delivered";

                Intent sentIntent = new Intent(SENT);
     /*Create Pending Intents*/
                PendingIntent sentPI = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, sentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Intent deliveryIntent = new Intent(DELIVERED);

                PendingIntent deliverPI = PendingIntent.getBroadcast(
                        getApplicationContext(), 0, deliveryIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
     /* Register for SMS send action */
                registerReceiver(new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String result = "";

                        switch (getResultCode()) {

                            case Activity.RESULT_OK:
                                result = "Transmission successful";
                                break;
                            case android.telephony.SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                result = "Transmission failed";
                                break;
                            case android.telephony.SmsManager.RESULT_ERROR_RADIO_OFF:
                                result = "Radio off";
                                break;
                            case android.telephony.SmsManager.RESULT_ERROR_NULL_PDU:
                                result = "No PDU defined";
                                break;
                            case android.telephony.SmsManager.RESULT_ERROR_NO_SERVICE:
                                result = "No service";
                                break;
                        }

                        Toast.makeText(getApplicationContext(), result,
                                Toast.LENGTH_LONG).show();
                    }

                }, new IntentFilter(SENT));
     /* Register for Delivery event */
                registerReceiver(new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Toast.makeText(getApplicationContext(), "Deliverd",
                                Toast.LENGTH_LONG).show();
                    }

                }, new IntentFilter(DELIVERED));

      /*Send SMS*/
                android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, msg, sentPI,
                        deliverPI);
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(),
                        ex.getMessage().toString(), Toast.LENGTH_LONG)
                        .show();
                ex.printStackTrace();
            }
        }

        // ************************ Is Sms Received Function for Broadcast receiver ****************
        private boolean isSmsReceived(Context context, Intent intent) {

            final Bundle bundle = intent.getExtras();

            try {

                if (bundle != null) {

                    Object[] pdusObj = (Object[]) bundle.get("pdus");
                    if (pdusObj.length == 0) {
                        Toast.makeText(context, "No Sms", Toast.LENGTH_LONG).show();
                        return false;
                    }

                    SmsMessage[] currentMessage = new SmsMessage[pdusObj.length];
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i = 0; i < pdusObj.length; i++) {
                        currentMessage[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        stringBuilder.append(currentMessage[i].getMessageBody());
                    } // end for loop

                    String phoneNumber = currentMessage[0].getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = stringBuilder.toString();

                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);
                } // bundle is null

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" + e);

            }


            return false;
        }


    }

}// End of ChatService.java