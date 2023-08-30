package com.annaginagili.multiapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.annaginagili.multiapp.databinding.ChatsLayoutBinding

class ChatAdapter(private val context: Context, private val imageList: ArrayList<Int>,
                  private val nameList: ArrayList<String>, private val lastList: ArrayList<String>,
                  private val timeList: ArrayList<String>, private val uidList: ArrayList<String>):
    RecyclerView.Adapter<ChatAdapter.ChatHolder>() {

    class ChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ChatsLayoutBinding.bind(itemView)
        private val imageView: ImageView = binding.profile
        private val name: TextView = binding.name
        private val last: TextView = binding.last
        private val time: TextView = binding.time

        fun setData(context: Context, image: Int, name: String, last: String, time: String, uid: String) {
            imageView.setImageResource(image)
            this.name.text = name
            this.last.text  = last
            this.time.text = time

            itemView.setOnClickListener {
                val intent = Intent(context, DirectMessaging::class.java)
                intent.putExtra("uid", uid)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.chats_layout, parent, false)
        return ChatHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ChatHolder, position: Int) {
        holder.setData(context, imageList[position], nameList[position], lastList[position],
            timeList[position], uidList[position])
    }
}