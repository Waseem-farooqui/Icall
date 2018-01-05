package com.techstartegies.icall;

/**
 * Created by HP on 8/20/2015.
 */
public class ChatMessage {


        private int id;
        String sender;
        String receiver;
        String message;
        String status;
        int sr;

        private boolean isMe;


        private String dateTime;
      //  private static char status[]=new char[100];


        public long getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }

        public boolean getIsme() {
            return isMe;
        }
        public void setMe(boolean isMe) {
            this.isMe = isMe;
        }

    public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender= sender;
        }

        public String getDate() {
            return dateTime;
        }

        public void setDate(String dateTime) {
            this.dateTime = dateTime;
        }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver= receiver;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status=status;
    }

    public void setSr(int sr)
    {
        this.sr=sr;

    }

    public int getSr()
    {
        return sr;
    }

     /*    public static char getStatus(int mId) {
            return status[mId];
    }
        public static void setStatus(int mId,char stat)

        {
            Log.e("status", mId+"" + stat);
                status[mId] = stat;
    }*/

}
