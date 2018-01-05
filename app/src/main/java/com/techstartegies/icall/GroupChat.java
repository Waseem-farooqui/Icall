package com.techstartegies.icall;

import android.app.Activity;
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

import org.jivesoftware.smack.packet.Message;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


public class GroupChat extends Activity {

    EditText messageET;
    ListView messagesContainer;
    Button sendBtn;
     GroupChatAdapter adapter;
    ArrayList<ChatMessage> chatHistory;
    Handler mHandler = new Handler();
    MotionEvent event;
    boolean r_t_f;
    Message msg;
    String s;
    ChatMessage cm;
    static GroupChat gc;
    int msg_id=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen);
        gc=this;
        initControls();

    }

    private void initControls() {

        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
       // RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        meLabel.setText("Me");
        companionLabel.setText("User 2");// Hard Coded


        chatHistory = new ArrayList<ChatMessage>();
        adapter = new GroupChatAdapter(GroupChat.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);
        runOnUiThread(new Runnable() {

            public void run() {

                Log.e("In here", "");
                adapter.notifyDataSetChanged();

            }


        });
        messagesContainer.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {


                 GroupChat.this.event = event;
                 r_t_f=false;
                 mHandler.post(new Runnable() {

                     public void run() {
                        // scroll();

                         if ((GroupChat.this.event.getAction() == MotionEvent.ACTION_DOWN) || (GroupChat.this.event.getAction() == MotionEvent.ACTION_UP)) {
                             //Log.e("Entering", "");

                            /*Message message = new Message();
                            message.setTo(ChatConnection.receiver);


                            message.setType(Message.Type.chat); // must req otherwise message will not get send
                            message.setBody("" + "_Read_");
                            Log.e("Message Send INFO" + message.getBody(), message.getStanzaId());


                            try {
                                con.mConnection.sendPacket(message);
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }*/

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

                try {

                    ChatMessage chatMessage = new ChatMessage();
                    //  chatMessage.setId(mId);//dummy
                    chatMessage.setMessage(messageText);
                    chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                    chatMessage.setMe(true);
                     chatMessage.setStatus("s");
                    displayMessage(chatMessage);


                    CreateGroup cg = new CreateGroup();
                    cg.sendGroupMessage(messageText);
                    messageET.setText("");

                    Log.e("Sending", "Successful");
                } catch (Exception e) {
                    Log.e("Sending Exception", e.getMessage());
                }

            }


        });


    }

    public void updateStatus() {
        for (int i = 0; i < chatHistory.size(); i++) {

             chatHistory.get(i).setStatus("d");
            adapter.notifyDataSetChanged();

        }
    }

    public void displayReceiver(Message message) {


         msg = message;
        cm=new ChatMessage();

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (msg.getBody() == null)
                    return;
                else {
                    s = msg.getBody();
                    cm.setMessage(s);
                    cm.setMe(false);
                    String abc=msg.getFrom().split("/")[1];
                    String sender=abc.split("@")[0];
                    cm.setSender(sender);
                    cm.setStatus("p");
                    cm.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                  //  loadDummyHistory(s);


                }

                displayMessage(cm);
            }
        });

    }


    public void displayMessage(ChatMessage message) {

        adapter.add(message);

        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {

        messagesContainer.post(new Runnable() {
            public void run() {
                messagesContainer.setSelection(messagesContainer.getCount() - 1);
            }
        });
        //messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    void loadDummyHistory(String text){

        ChatMessage msg = new ChatMessage();
        Log.e("Dummy History", text);
        msg_id++;
        msg.setId(msg_id);
        msg.setMe(false);
        msg.setMessage(text);
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);
        displayMessage(msg);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
