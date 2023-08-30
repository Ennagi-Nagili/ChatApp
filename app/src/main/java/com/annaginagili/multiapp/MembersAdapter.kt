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
import com.annaginagili.multiapp.databinding.MembersLayoutBinding
import com.annaginagili.multiapp.databinding.RoomLayoutBinding

class MembersAdapter(private val context: Context, private val imageList: ArrayList<Int>,
                  private val nameList: ArrayList<String>):
    RecyclerView.Adapter<MembersAdapter.MemberHolder>() {

    class MemberHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = MembersLayoutBinding.bind(itemView)
        private val imageView: ImageView = binding.image
        private val name: TextView = binding.name

        fun setData(context: Context, image: Int, name: String) {
            imageView.setImageResource(image)
            this.name.text = name

            itemView.setOnClickListener {
//                val intent = Intent(context, DoctorDetailsActivity::class.java)
//                intent.putExtra("image", bigImage)
//                intent.putExtra("name", name)
//                intent.putExtra("special", special)
//                intent.putExtra("hospital", hospital)
//                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.members_layout, parent, false)
        return MemberHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: MemberHolder, position: Int) {
        holder.setData(context, imageList[position], nameList[position])
    }
}