package com.techstartegies.icall;

import android.util.Log;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.util.List;

/**
 * Created by HP on 12/9/2015.
 */
public class CreateGroup {


    String mNickName=ChatConnection.sender;
    String mGroupRoomName="myroom2@conference.srv.techstrategiessolution.com";
    String msg_Id="0";



    MultiUserChat muc;

    ChatConnection con;
    MultiUserChatManager manager;


    public CreateGroup() {

        con = ChatService.con;

        // Get the MultiUserChatManager
        manager = MultiUserChatManager.getInstanceFor(ChatService.con.mConnection);

        // Get a MultiUserChat using MultiUserChatManager
        muc = manager.getMultiUserChat(mGroupRoomName);

        muc.addMessageListener(new GroupMessageListenerImpl());

        muc.addInvitationRejectionListener(new InvitationRejectionListener() {
            public void invitationDeclined(String invitee, String reason) {
                // Do whatever you need here...
            }
        });

        // User3 listens for MUC invitations
        MultiUserChatManager.getInstanceFor(con.mConnection).addInvitationListener(new InvitationListener() {
            @Override
            public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {

              //  ChatService.GChat = true;
                ChatService.setNotification("ICALL GROUP CHAT INVITATION");
               // ChatService.GChat = false;

            }


        });





    }

   public class GroupMessageListenerImpl implements MessageListener {
       @Override
       public void processMessage(Message message) {




           //ChatService.GChat = true;
               if (!message.getFrom().contains(ChatConnection.sender)) {
                   ChatService.setNotification(message.getBody());
                   if ((GroupChat.gc != null) && (!message.getStanzaId().equals("1")))
                   {
                       Log.e("ChatService lf", "" + message.getFrom());
                       GroupChat.gc.displayReceiver(message);


                   }
                   message.setStanzaId("1");
                   msg_Id = "1";

               }




          // ChatService.GChat = false;


       }
   }


        public void CreatingGroup() throws XMPPException.XMPPErrorException, SmackException {



            // Create the room
       muc.create(mNickName);
     // muc.create(mNickName, con.Password);


        // Send an empty room configuration form which indicates that we want
        // an instant room
        muc.sendConfigurationForm(new Form(DataForm.Type.submit));
    }

    public void joiningGroup()
    {

        // Get the MultiUserChatManager


        // Get a MultiUserChat using MultiUserChatManager
        try
        {
            // CreatingGroup();
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(0);

            ChatService.p_notification=0;
            muc.join(mNickName, con.Password, history, ChatService.con.mConnection.getPacketReplyTimeout());//, con.Password);


        }catch(Exception ex)
        {
            Log.e("Joining Group Exception", ex.getMessage());
        }


    }

    void sendGroupMessage(String message)
    {
        ChatService.joinFirstTime=1;
            List<String> occupant_list=muc.getOccupants();
            for(int i=0;i<occupant_list.size();i++)
                Log.e("Room Memebers",occupant_list.get(i));
           // muc.sendMessage("Hi");
// User2 invites user3 to join to the room
        try{


            Message m= new Message();
            msg_Id="0";
            m.setStanzaId(msg_Id);

            //m.setType(Message.Type.groupchat);
            m.setFrom(ChatConnection.sender);
            m.setBody(message);
            muc.sendMessage(m);
            msg_Id="1";
          //  muc.sendMessage("Not Again Plzz");

           //muc.invite(ChatConnection.receiver, "Meet me in this excellent room");
           //muc.join(mNickName, con.Password);
        }catch(Exception ex)
        {
            Log.e("Inviting Grp Exception", ex.getMessage());
        }

    }


}
