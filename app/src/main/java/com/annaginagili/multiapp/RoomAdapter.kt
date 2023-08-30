package com.annaginagili.multiapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.annaginagili.multiapp.databinding.RoomLayoutBinding

class RoomAdapter(private val context: Context, private val imageList: ArrayList<Int>,
                    private val textList: ArrayList<String>):
    RecyclerView.Adapter<RoomAdapter.RoomHolder>() {

    class RoomHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = RoomLayoutBinding.bind(itemView)
        private val imageView: ImageView = binding.image
        private val text: TextView = binding.text

        fun setData(context: Context, image: Int, text: String) {
            imageView.setImageResource(image)
            this.text.text = text

            itemView.setOnClickListener {
                val intent = Intent(context, RoomMessaging::class.java)
                intent.putExtra("uid", text)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.room_layout, parent, false)
        return RoomHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: RoomHolder, position: Int) {
        holder.setData(context, imageList[position], textList[position])
    }
}