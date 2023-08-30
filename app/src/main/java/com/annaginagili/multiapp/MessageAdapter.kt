package com.annaginagili.multiapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.annaginagili.multiapp.databinding.MessagOtherBinding
import com.annaginagili.multiapp.databinding.MessageLayoutBinding
import com.annaginagili.multiapp.databinding.RoomLayoutBinding

class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>,
                     private val uid: String, private val mailList: ArrayList<String>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val send = 1
    private val receive = 2

    class MessageSendHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = MessageLayoutBinding.bind(itemView)
        private val text: TextView = binding.message

        fun setData(context: Context, text: String) {
            this.text.text = text

//            itemView.setOnClickListener {
//                context.startActivity(Intent(context, ChatRoom::class.java))
//            }
        }
    }

    class MessageReceiveHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = MessagOtherBinding.bind(itemView)
        private val text: TextView = binding.message
        private val mail: TextView = binding.name

        fun setData(context: Context, text: String, mail: String) {
            this.text.text = text
            this.mail.text = mail

//            itemView.setOnClickListener {
//                context.startActivity(Intent(context, ChatRoom::class.java))
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewSend: View = LayoutInflater.from(context).inflate(R.layout.message_layout, parent, false)
        val viewReceive: View = LayoutInflater.from(context).inflate(R.layout.messag_other, parent, false)
        return when (viewType) {
            send -> MessageSendHolder(viewSend)
            else -> MessageReceiveHolder(viewReceive)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       if (getItemViewType(position) == send && holder is MessageSendHolder) {
           holder.setData(context, messageList[position].message)
       }
       else if (getItemViewType(position) == receive && holder is MessageReceiveHolder) {
           holder.setData(context, messageList[position].message, mailList[position])
       }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].uid == uid) {
            send
        } else {
            receive
        }
    }
}