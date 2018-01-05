package com.techstartegies.icall;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.techstartegies.util.SqliteUtil;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class chat extends Activity  {

    String s1;
    boolean r_t_f;


    EditText messageET;
    ListView messagesContainer;
    Button sendBtn;
    ChatAdapter adapter;
    ArrayList<ChatMessage> chatHistory;
    Handler mHandler = new Handler();

    static int reset_flag=0;



    int ii=0;
    int k=0;

    Message msg;
    static String m_Id;
    static String s = "";

    MotionEvent event;
    static int mId;



    SqliteUtil q;


    ChatConnection con;

    public static Activity activity;

    // Function 1:================================On-Create()======================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle(ChatConnection.sender.toUpperCase());
        setContentView(R.layout.chat_screen);


        Log.e("OnCreate", "");


        mId=ChatService.getPropId();

        ChatService.active=true;


        String chat_Id= getIntent().getExtras().getString("ChatId");
        setTitle(chat_Id.toUpperCase());

        chat.activity=this;
        if(ChatService.myTimer!=null) {
            ChatService.stopTimer = true;
            ChatService.myTimer.cancel();

        }
        con=(ChatConnection) getApplicationContext();


        initControls();

    }



    // Function 3:================================initControls()======================================

    private void initControls() {

        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);


        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);

        companionLabel.setText("User 2");// Hard Coded


        q=new SqliteUtil(chat.this);
        if(q.getFullRecord()!=null) {
            chatHistory = q.getFullRecord();
            q.close();
        }
        else
            chatHistory=new ArrayList<ChatMessage>();

        adapter = new ChatAdapter(chat.this, chatHistory);



        messagesContainer.setAdapter(adapter);
        //messagesContainer.setStackFromBottom(true);// show last messages firstr
        receiveAdapterStateChanged();

        if(q.getFullRecord()!=null)
        {
            reset_flag=1;
            //     for(int i=0;i<chatHistory.size();i++)
            receiveAdapterStateChanged();
        }
        messagesContainer.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (ChatService.myTimer != null)
                    ChatService.myTimer.cancel();

                chat.this.event = event;
                r_t_f = false; //for function requ
                q = new SqliteUtil(chat.this);
                int r = q.numberOfRows();
                Cursor res = null;
                if (r != 0) {
                    res = q.getAllRecords();
                    res.moveToFirst();

                    while (res.isAfterLast() == false)

                    {
                        if (res.getInt(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_SR)) == 0) {


                            int i = res.getInt(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_ID));
                            String status = res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_STATUS));
                            if (status.equals("p"))
                                q.updateStatus(i, "v");
                        }
                        res.moveToNext();

                    }
                    q.close();
                }


                mHandler.post(new Runnable() {

                    public void run() {


                        if ((chat.this.event.getAction() == MotionEvent.ACTION_DOWN) || (chat.this.event.getAction() == MotionEvent.ACTION_UP)) {
                            Log.e("Entering", "");

                            Message message = new Message();
                            message.setTo(ChatConnection.receiver);


                            message.setType(Message.Type.chat); // must req otherwise message will not get send
                            message.setBody("" + "_Read_");
                            Log.e("Message Send INFO" + message.getBody(), message.getStanzaId());


                            try {
                                con.mConnection.sendPacket(message);
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }

                            r_t_f = true;


                        }


                    }


                });
                if (r_t_f == true)
                    return true;
                else
                    return false;

            }

        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                ChatMessage chatMessage = new ChatMessage();
                mId = ChatService.getPropId();
                chatMessage.setId(mId);//dummy
                chatMessage.setMessage(messageText);


                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);
                chatMessage.setStatus("s");

                // ChatService.q.enqueue(mId, 's', 1);
                // 1 is for sender , s is message sent status
                q = new SqliteUtil(chat.this);
                q.insertContact(mId, ChatConnection.sender, ChatConnection.receiver, messageText, "s", 1, DateFormat.getDateTimeInstance().format(new Date()));
                q.close();
                displayMessage(chatMessage);

                runOnUiThread(new Runnable() {

                    public void run() {

                        try {

                            sendMessage(chat.this.mId);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    messageET.setText("");

                                }
                            });

                        } catch (Exception e) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    messageET.setText("");

                                }
                            });

                            // Log.e("Sending Message Problem",e.toString());
                        }

                    }
                });
                mId++;
                ChatService.setPropId(mId);
                //mId++;

                //ChatService.editor.commit();
            }
        });


    }
//========================================== Implementing Tick==================================

    public  void receiveAdapterStateChanged()
    {
        runOnUiThread(new Runnable() {

            public void run() {

                Log.e("In here", "");
                adapter.notifyDataSetChanged();

            }


        });
    }


    //=======================================  readMessage on NotificationClick======================

    public void readMessageOnClickNotification()
    {

        Message message = new Message();
        message.setTo(ChatConnection.receiver);

        if(ChatService.myTimer!=null)
            ChatService.myTimer.cancel();

        message.setType(Message.Type.chat); // must req otherwise message will not get send
        message.setBody("" + "_Read_");
        Log.e("Message Send INFO" + message.getBody(), message.getStanzaId());


        try {
            con.mConnection.sendPacket(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }


    }


//====================================display Receiver Message on Screen Chat Bubble=================================

    public void displayReceiver(Message message) {


        k=0;

        msg = message;
        ii = 0;
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (msg.getBody() == null)
                    return;
                else {
                    s = msg.getBody();


                }

                Log.e("Display Message", s);

                q = new SqliteUtil(getApplicationContext());

                int r = q.numberOfRows();
                Cursor res = null;
                if (r != 0) {
                    res = q.getAllRecords();
                    res.moveToFirst();

                    while (res.isAfterLast() == false)

                    {
                        if (res.getInt(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_SR)) == 0) {


                            String status = res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_STATUS));
                            int id = res.getInt(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_ID));
                            if (status.equals("p")) {
                                // q.updateStatus(id, "v");
                                loadDummyHistory(res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_MESSAGE)));
                                k = 1;

                                //  ChatMessage.setStatus(id, 'r');
                            }
//                                loadDummyHistory(res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_MESSAGE)));
                        }
                        res.moveToNext();
                        //
                        // receiveAdapterStateChanged();
                    }

                    // obj.receiveAdapterStateChanged();

                }


                if (k != 1)
                    loadDummyHistory(s);
                q.close();
                //     mId++;


            }
        });

    }

    // Function 3:================================display Message On GUI Sender Side======================================

    public void displayMessage(ChatMessage message) {

        adapter.add(message);

        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    // Function 4:================================display Receiving side messages on GUI======================================

    void loadDummyHistory(String text){

        ChatMessage msg = new ChatMessage();
        Log.e("Dummy History", text);

        msg.setMe(false);
        msg.setMessage(text);
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        //  chatHistory.add(msg);
        displayMessage(msg);

    }

    // Function 5:================================sendMessage()======================================

    public void sendMessage(int m_Id) throws Exception {

        Message message = new Message();


        for (int j = 0; j < ChatConnection.online_list.length; j++) {

            Log.e("Sending Message Problem", "" + ChatConnection.online_status[j]);
            //if ((ChatConnection.online_status[j].equalsIgnoreCase("Online")) && ((ChatConnection.online_list[j].equals("fakhar@srv.techstrategiessolution.com"))|| (ChatConnection.online_list[j].equals("ali@srv.techstrategiessolution.com"))))
            if ((ChatConnection.online_status[j].equalsIgnoreCase("Online")) && ((ChatConnection.online_list[j].equals(con.sender))|| (ChatConnection.online_list[j].equals(con.receiver))))
            {
                try {


                    //  message.setTo(ChatConnection.online_list[j]);
                    message.setTo(ChatConnection.receiver);


                    message.setType(Message.Type.chat); // must req otherwise message will not get send


                    message.setStanzaId("" + m_Id);
                    message.setThread("My First Message");


                    delievery_Status();

                    message.setBody("" + messageET.getText());
                    Log.e("Message Send INFO" + message.getBody(), message.getStanzaId());


                    DeliveryReceiptManager.addDeliveryReceiptRequest(message);

                    con. mConnection.sendPacket(message);

                    //  m_Id++;

                }
                catch (Exception ee) {


                }

            }

        }//end of for

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                messageET.setText("");

            }
        });

    }



    // Function 7:================================Delievery Receipts======================================

    public void delievery_Status() {

        DeliveryReceiptManager.getInstanceFor(con.mConnection).addReceiptReceivedListener(new ReceiptReceivedListener() {

            public void onReceiptReceived(String arg0, String arg1, String arg2, Stanza s) {

                //arg0 contains receiver information fareeha@chatts/testservices
                //arg0 contains sender who will get message delivery report ali@chatts/testservices
                //arg 2 contains message Id set using message setStanzaId()
                chat.m_Id = arg2;
                s1 = s.toString();

                //Packet received=new Message(s1);
                //PacketExtension statusExtension = (PacketExtension) received.getExtension("urn:xmpp:receipts");


                final String xmlMessage = s.toString();
                if (xmlMessage.contains("<received xmlns=")) {
                    Log.e("I AM Receiving", "RECEIVED notification arrived!");
                }

                Log.e("Message Delivered Info ", chat.m_Id + " " + chat.this.s1 + "  " + s.getStanzaId());

                int i = Integer.parseInt(chat.m_Id.trim());
                //   ChatMessage.setStatus"d");

                // Log.e("Message", "" + i + ChatMessage.getStatus(i));
                q = new SqliteUtil(chat.this);
                //String uri = "@drawable/R.drawable.tick";
                //int imageResource = getResources().getIdentifier(uri, null, getPackageName());

                q.updateStatus(i, "d");
                q.close();
                receiveAdapterStateChanged();


                //ChatService.q.update(i, 'd', chat.this.s1);

            }
        });
    }


    // Function 11:================================On-Create Options Menu()======================================



    // Function Extras:================================On Destroy=====================================
    public void onDestroy()
    {
        super.onDestroy();

        if(ChatService.myTimer!=null)// to generate push notifications if user is not on chat activity
        {
            //to stop push notification
            ChatService.stopTimer=true;
            ChatService.myTimer.cancel();
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancelAll();
        }
        ChatService.active=false;
        q.close();
    }

    /**
     * On Resume
     */
    public void onResume() {
        super.onResume();

        ChatService.active = true;
        if((ChatService.myTimer!=null)) {
            ChatService.myTimer.cancel();
            ChatService.stopTimer=true;
            readMessageOnClickNotification();
        }

        // clear all notifications on clicking one
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();



        q=new SqliteUtil(chat.this);
        if(q.getFullRecord()!=null)
            chatHistory=q.getFullRecord();
        else
            chatHistory=new ArrayList<ChatMessage>();

        adapter = new ChatAdapter(chat.this, chatHistory);


        messagesContainer.setAdapter(adapter);

        if(q.getFullRecord()!=null)
        {
            reset_flag=1;
            for(int i=0;i<chatHistory.size();i++)
                receiveAdapterStateChanged();
        }
        //receiveAdapterStateChanged();

    }

    /**
     *
     * @param item
     * @return
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_LogOut)
        {

            try {


                ChatConnection.login=false;
                con.mConnection.disconnect();
                con.mConnection=null;
                stopService(new Intent(chat.this,ChatService.class));
                ChatService.started=0;
                runOnUiThread(new Runnable() {

                    public void run() {

                        Toast.makeText(chat.this, ChatConnection.sender + " Logging Out ", Toast.LENGTH_LONG).show();

                    }
                });

            }
            catch(Exception e) {

            }
        }


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
