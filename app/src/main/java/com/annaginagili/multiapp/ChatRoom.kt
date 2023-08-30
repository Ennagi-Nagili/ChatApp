package com.annaginagili.multiapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annaginagili.multiapp.databinding.ActivityChatRoomBinding

class ChatRoom : AppCompatActivity() {
    lateinit var binding: ActivityChatRoomBinding
    lateinit var members: RecyclerView
    lateinit var adapter: MembersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        members = binding.members

        members.setHasFixedSize(true)
        members.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val imageList = mutableListOf(R.drawable.profile, R.drawable.profile, R.drawable.profile,
            R.drawable.profile, R.drawable.profile) as ArrayList<Int>
        val nameList = mutableListOf("Maciej Kowalski", "Maciej Kowalski", "Maciej Kowalski",
        "Maciej Kowalski", "Maciej Kowalski") as ArrayList<String>
        adapter = MembersAdapter(this, imageList, nameList)
        members.adapter = adapter
    }
}