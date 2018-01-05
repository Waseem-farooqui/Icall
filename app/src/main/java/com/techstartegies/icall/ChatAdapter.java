package com.techstartegies.icall;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.techstartegies.util.SqliteUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by HP on 8/20/2015.
 */
public class ChatAdapter extends BaseAdapter {

  //  private final List<ChatMessage> chatMessages;
    private final ArrayList<ChatMessage> chatMessages;
    private Activity context;
    ViewHolder holder;
    long id;
    String status;


    public ChatAdapter(Activity context, ArrayList<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;

    }



    @Override
    public int getCount() {
        if (chatMessages != null) {

            return chatMessages.size();
        } else {

            return 0;
        }
    }

    @Override
    public ChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {



        ChatMessage chatMessage = getItem(position);
        id=getItemId(position);
        Log.e("ID",""+chatMessage.getId()+"   "+id);

        // retreiving status from  database

       SqliteUtil q=new SqliteUtil(chat.activity);

        if(chat.reset_flag==1) {


        }

        int r=q.numberOfRows();
        Cursor res=null;
        if(r!=0) {
            res = q.getAllRecords();
            res.moveToFirst();

            while (res.isAfterLast() == false)

            {
                if (res.getInt(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_SR)) == 1) {


                    String status1 = res.getString(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_STATUS));
                    int id1 = res.getInt(res.getColumnIndex(SqliteUtil.ICALL_COLUMN_ID));
                    if (id1 == chatMessage.getId()) {

                        status = status1;
                        break;
                    }

                    else
                        status="receiving";

                }

                res.moveToNext();

            }
            if(!res.isClosed())
                res.close();
        }

      //  status=chatMessage.getStatus();

        Log.e("Implementing Tick","Item Id of Message"+chatMessage+""+id+" is " +" status="+status);

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
           // convertView = vi.inflate(R.layout.list_item_chat_message, null);
            convertView = vi.inflate(R.layout.list_item_schat_message,parent,false);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        boolean myMsg = chatMessage.getIsme() ;//Just a dummy check
        //to simulate whether it me or other sender
        setAlignment(holder, myMsg);

        holder.txtMessage.setText(chatMessage.getMessage());

        holder.txtInfo.setText(chatMessage.getDate());
      //  if(position==0)
         //   ChatAdapter.this.holder.img.setImageResource(R.drawable.tick);
      /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ChatAdapter.this.holder.img.setImageResource(R.drawable.tick);
            }
        }, 20 * 1000);*/



        return convertView;
    }

    public void add(ChatMessage message) {
        chatMessages.add(message);
    }

    public void add(List<ChatMessage> messages) {
        chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isMe) {
        if (!isMe) {

            Log.e("Its not me","");
            holder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);



            ChatAdapter.this.holder.img.setImageResource(0);
           ChatAdapter.this.holder.img1.setImageResource(0);



        } else {
            Log.e("Its  me","");
            holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);




            if(status.equals("d")) {
                ChatAdapter.this.holder.img.setImageResource(R.drawable.tick);
                ChatAdapter.this.holder.img1.setImageResource(0);
            }
            else if(status.equals("r")) {
                ChatAdapter.this.holder.img.setImageResource(R.drawable.tick);
                ChatAdapter.this.holder.img1.setImageResource(R.drawable.tick);
            }
            else {
                ChatAdapter.this.holder.img.setImageResource(0);
                ChatAdapter.this.holder.img1.setImageResource(0);
            }
        }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
      //  holder.senderInfo=(TextView) v.findViewById(R.id.senderInfo);
        holder.img = (ImageView)v.findViewById(R.id.img);
        holder.img1 = (ImageView)v.findViewById(R.id.img1);
        return holder;
    }



    private static class ViewHolder {
        public ImageView img;
        public ImageView img1;
        public TextView txtMessage;
        public TextView txtInfo;

        public LinearLayout content;
        public LinearLayout contentWithBG;
    }
}