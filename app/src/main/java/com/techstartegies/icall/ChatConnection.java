package com.techstartegies.icall;

import android.app.Application;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

import com.techstartegies.util.SqliteUtil;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;

/**
 * Created by HP on 11/27/2015.
 */

public class ChatConnection extends Application {


    static boolean accept=false;
    int iid;
    String msg,rec;
    Exception e1;
    int flag = 0;
    static int reset_flag=0;
    Message message;
    static boolean rMOC=false; //read Message On Click Notification

    public String HOST ="115.186.132.21";//"124.109.61.32";//"162.246.17.56";//server local ip 192.168.8.104 ,192.168.1.9
    public static String sender = "ali@srv.techstrategiessolution.com";
    public static String receiver = "saad@srv.techstrategiessolution.com";
    public static String receiver2 = "asad@srv.techstrategiessolution.com"; // for group chat


    public String Password = "abc";
    public String ServiceName = "srv.techstrategiessolution.com";
    static String online_list[], online_status[], online_jid[];

    private Handler handler;
    Thread resend;

    SqliteUtil q;

    public static boolean login=false;
    Roster roster;

    AbstractXMPPConnection mConnection;

    int i = 0;



    chat obj;

    public void establishConnection() {


        connect();

    }



    public AbstractXMPPConnection getConnection() {



        return mConnection;


    }

    public Roster getRoster() {

        return roster;


    }

    public void connect() {

        //
        Log.e("In service again","");
        // server configurations

        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        ///System.setProperty("java.net.preferIPv4Addresses|java.net.preferIPv6Addresses", "false");

        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        //config.setResource("Fakhar_Login");
        config.setUsernameAndPassword(sender, Password);
        config.setServiceName(ServiceName);
        config.setHost(HOST);
        config.setPort(5222);
        // config.setConnectTimeout(100000);

        // Set the status to available
        Presence presence = new Presence(Presence.Type.available);

        config.setSendPresence(true);
        config.setDebuggerEnabled(true);


        SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
        SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");


        mConnection = new XMPPTCPConnection(config.build());
        mConnection.setPacketReplyTimeout(10000);


        mConnection.addConnectionListener(new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                Log.e("Connecting", "Connected");

            }

            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {

                Log.e("Authenticating", "Authenticated");
            }

            @Override
            public void connectionClosed() {
                Log.e("Reconnecting", "connection closed");
                //mConnection.disconnect();
                //ChatConnection.login=false;
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                Log.e("Closing", "connection closed due to error" + e.toString());

            }

            @Override
            public void reconnectionSuccessful() {

                Log.e("Reconnecting", "Reconnection successful");

                login=true;
            }

            @Override
            public void reconnectingIn(int i) {

                Log.e("Reconnecting", "Reconnection attempts");
                if (i < 4) {
                    //TODO notify
                    ReconnectionManager manager = ReconnectionManager.getInstanceFor(mConnection);
                    manager.enableAutomaticReconnection();

                }
            }

            @Override
            public void reconnectionFailed(Exception e) {

                Log.e("Reconnecting", "Reconnection Failed " + e.toString());



            }

        });


        presence.setMode(Presence.Mode.available);
        try {
            mConnection.sendPacket(presence);
        } catch (Exception r) {
        }



        try

        {

            mConnection.connect();

            Log.e("Connecting", e1.toString());

        } catch (Exception e)

        {
            e1 = e;

            Log.e("Error Connecting", e1.toString());
            //login=false;

        }

        // code to login
        try

        {

            mConnection.login();
            ChatManager cm = ChatManager.getInstanceFor(mConnection);
            cm.addChatListener(
                    new ChatManagerListener() {
                        @Override
                        public void chatCreated(Chat chat, boolean createdLocally) {
                            if (!createdLocally) {
                                chat.addMessageListener(new MessageListenerImpl());
                            }
                        }
                    });


            Log.e("Login Successfully", e1.toString());
            login=true;
        } catch (SmackException ex)

        {
            Log.e("Error Login", ex.toString());
            //login=false;
        }catch (IOException ex)

        {
            Log.e("Error Login", ex.toString());
            //login=false;
        }catch (XMPPException ex)

        {
            Log.e("Error Login", ex.toString());
            //login=false;
        }

    }

//===============================reading Online user s through roster=====================================


    public void readRoster() {


        roster = Roster.getInstanceFor(mConnection);

        roster.addRosterListener(new RosterListener() {
            // Ignored events public void entriesAdded(Collection<String> addresses) {}
            public void entriesDeleted(Collection<String> addresses) {
            }

            @Override
            public void entriesAdded(Collection<String> addresses) {


                online_list = addresses.toArray(new String[addresses.size()]);
                online_status = new String[online_list.length];
                online_jid = new String[online_list.length];
                Log.e("T", "Entries Added" + online_list[1]);
                for (i = 0; i < online_list.length; i++) {
                    String s = roster.getPresence(online_list[i]).getType().toString().trim();

                    Log.e("T", online_list[i] + "\t" + roster.getPresence(online_list[i]).getType() + "\n");
                    if (s.equalsIgnoreCase("unavailable")) {
                        Log.e("Offline_Status", "User Offline");
                        online_status[i] = "Offline";
                    } else if (s.equalsIgnoreCase("available")) {
                        online_status[i] = "Online";
                    }
                }
            }

            public void entriesUpdated(Collection<String> addresses) {


            }

            public void presenceChanged(Presence presence) {


                Log.e("status", "Presence changed: " + presence.getFrom() + " " + presence);
                for (i = 0; i < online_list.length; i++) {

                    String s = roster.getPresence(online_list[i]).getType().toString().trim();

                    Log.e("T", online_list[i] + "\t" + roster.getPresence(online_list[i]).getType() + "\n");
                    if (s.equalsIgnoreCase("unavailable")) {
                        Log.e("Offline_Status", "Offline");
                        online_status[i] = "Offline";
                    } else if (s.equalsIgnoreCase("available")) {
                        online_status[i] = "Online";


                        Log.e("Statuses", "" + online_status[i]);


                    }
                }

            }
        });

    }


    //==================================JJJJ=====================================================================



    class MessageListenerImpl implements MessageListener, ChatStateListener {


        @Override
        public void processMessage(Chat arg0, Message arg1) {


            Log.e("Testing Received msge: ", "" + arg1.getBody());

            if((arg1.getBody()!=null)&&(!arg1.getBody().equals("_Read_"))&&(!arg1.getBody().equals("_online_"))) {
                ChatService.setNotification(arg1.getBody());

                //to stop push notification
                if((ChatService.active==false)&&(ChatService.stopTimer==false))
                    ChatService.pushNotification();

            }
            if(arg1.getBody() != null)
                read_modify(arg1);


        }

        @Override
        public void stateChanged(Chat arg0, ChatState arg1) {
            if (ChatState.composing.equals(arg1)) {
                Log.d("Chat State", arg0.getParticipant() + " is typing..");
            } else if (ChatState.gone.equals(arg1)) {
                Log.d("Chat State", arg0.getParticipant() + " has left the conversation.");
            } else {
                Log.d("Chat State", arg0.getParticipant() + ": " + arg1.name());
            }

        }

        @Override
        public void processMessage(Message message) {

        }


        public void read_modify(Message message) {

            obj=(chat)chat.activity;

            if((ChatService.myTimer!=null)&&(ChatService.active == true)) {
                ChatService.myTimer.cancel();
                ChatService.p_notification=0;
            }

            if(ChatService.active==false) {
                ChatService.stopTimer = false;
                ChatService.p_notification = 1;
            }

            if(message.getBody().equals("_online_"))
            {



                resendOfflineMessages();


                if(ChatService.active==true)
                    SendMessageReadToSender();

                return;
            }

            else if (message.getBody().equals("_Read_")) {

                //ChatService.active=true;
                //if((ChatService.myTimer!=null))
                //  ChatService.myTimer.cancel();



                Log.e("Message Read", "Successful");

                q=new SqliteUtil(getApplicationContext());

                int r=q.numberOfRows();
                Cursor res=null;
                if(r!=0) {
                    res= q.getAllRecords();
                    res.moveToFirst();

                    while(res.isAfterLast() == false)

                    {
                        if(res.getInt (res.getColumnIndex(SqliteUtil.ICALL_COLUMN_SR))==1)
                        {


                            String status=res.getString (res.getColumnIndex(SqliteUtil.ICALL_COLUMN_STATUS));
                            int id= res.getInt(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_ID));
                            if(status.equals("d")) {
                                q.updateStatus(id, "r");
                                //  ChatMessage.setStatus(id, 'r');
                            }
                        }
                        res.moveToNext();
                        //
                        if(ChatService.active==true)
                            obj.receiveAdapterStateChanged();
                    }



                }

                q.close();

                return;

            }




            q=new SqliteUtil(getApplicationContext());
            ChatService.setNotification(message.getBody());

            q.insertContact(Integer.parseInt(message.getStanzaId()), ChatConnection.sender, ChatConnection.receiver, message.getBody(), "p", 0, DateFormat.getDateTimeInstance().format(new Date()));

            q.close();
            // code to implement read double tick on receiving msgs
            if(obj!=null)
                obj.displayReceiver(message);

            if(ChatService.active==true)
                SendMessageReadToSender();
            // ChatService.q.enqueue(Integer.parseInt(message.getStanzaId()),'p',0);
          /*  if(obj!=null)
                obj.displayReceiver(message);*/

        }





    }

    public void SendMessageReadToSender()
    {

        Message message = new Message();
        message.setTo(ChatConnection.receiver);

        if(ChatService.myTimer!=null)
            ChatService.myTimer.cancel();

        message.setType(Message.Type.chat); // must req otherwise message will not get send
        message.setBody("" + "_Read_");
        ChatService.p_notification=0;
        ChatService.stopTimer=true;
        Log.e("Message Send INFO" + message.getBody(), message.getStanzaId());


        try {
            mConnection.sendPacket(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }


    }


    void resendOfflineMessages()  {



        q=new SqliteUtil(getApplicationContext());

        int r=q.numberOfRows();
        Cursor res=null;
        if(r!=0) {


            res= q.getAllRecords();
            res.moveToFirst();

            while(res.isAfterLast() == false)

            {
                if(res.getInt (res.getColumnIndex(SqliteUtil.ICALL_COLUMN_SR))==1)
                {

                    String status=res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_STATUS));
                    iid= res.getInt(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_ID));
                    msg= res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_MESSAGE));
                    rec= res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_RECEIVER));
                    //   int i=ChatConnection.online_list.length;

                    if(status.equals("s"))
                    {

                        {

                            try {



                                resendMessage(iid, msg, rec);


                            }
                            catch(Exception ex)
                            {
                                Log.e("Message Resend","Exception"+ex.getMessage());
                            }

                        }
                        //  ChatMessage.setStatus(id, 'r');
                    }
                }
                res.moveToNext();
                //

                // if(obj!=null)
                //     obj.receiveAdapterStateChanged();
            }

            // obj.receiveAdapterStateChanged();

        }

        q.close();


    }

    public void resendMessage(int Id,String msgText,String receiver) {
        message = new Message();


        Log.e("Offline messages", "Resending" + Id + "   " + msgText + "    " + receiver);
        try {


            message.setTo(receiver);


            message.setType(Message.Type.chat); // must req otherwise message will not get send


            message.setStanzaId("" + Id);
            message.setThread("My First Message");


            delievery_Status();

            message.setBody("" + msgText);
            Log.e("Message Send INFO" + message.getBody(), message.getStanzaId());


            DeliveryReceiptManager.addDeliveryReceiptRequest(message);


            mConnection.sendPacket(message);


        }
        catch(Exception e){}
    }






    public void delievery_Status() {

        DeliveryReceiptManager.getInstanceFor(mConnection).addReceiptReceivedListener(new ReceiptReceivedListener() {

            public void onReceiptReceived(String arg0, String arg1, String arg2, Stanza s){

                //arg0 contains receiver information fareeha@chatts/testservices
                //arg0 contains sender who will get message delivery report ali@chatts/testservices
                //arg 2 contains message Id set using message setStanzaId()
                // chat.m_Id = arg2;
                String s1=s.toString();

//Packet received=new Message(s1);
//PacketExtension statusExtension = (PacketExtension) received.getExtension("urn:xmpp:receipts");


                final String xmlMessage=s.toString();
                if(xmlMessage.contains("<received xmlns=")){
                    Log.e("I AM Receiving","RECEIVED notification arrived!");
                }

                Log.e("Message Delivered Info ",arg2+" "+s1+"  "+s.getStanzaId());

                int i=Integer.parseInt(arg2.trim());
                //   ChatMessage.setStatus"d");

                // Log.e("Message", "" + i + ChatMessage.getStatus(i));
                q=new SqliteUtil(getApplicationContext());
                //String uri = "@drawable/R.drawable.tick";
                //int imageResource = getResources().getIdentifier(uri, null, getPackageName());

                q.updateStatus(i,"d");
                q.close();

                if(ChatService.active==true) {
                    obj.receiveAdapterStateChanged();
                    ChatService.p_notification=0;
                }


                //ChatService.q.update(i, 'd', chat.this.s1);

            }
        });
    }

    void sendStatus(String sender)
    {

        Message message=new Message();
        message.setTo(ChatConnection.receiver);


        message.setType(Message.Type.chat); // must req otherwise message will not get send
        message.setBody("" + "_online_");
        Log.e("Message Send INFO" + message.getBody(), message.getStanzaId());


        try {
            mConnection.sendPacket(message);
            //resendOfflineMessages();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

    }


}