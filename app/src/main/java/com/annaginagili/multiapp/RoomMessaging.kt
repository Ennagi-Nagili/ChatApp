package com.annaginagili.multiapp

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annaginagili.multiapp.databinding.ActivityDirectMessagingBinding
import com.annaginagili.multiapp.databinding.ActivityRoomMessagingBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.Instant
import java.time.LocalDateTime
import java.util.Date
import java.util.TimeZone
import kotlin.random.Random

class RoomMessaging : AppCompatActivity() {
    lateinit var binding: ActivityRoomMessagingBinding
    lateinit var messages: RecyclerView
    lateinit var adapter: MessageAdapter
    lateinit var reference: DatabaseReference
    lateinit var send: ImageButton
    lateinit var uid: String
    lateinit var messageArea: EditText
    private lateinit var receiver1: String
    private lateinit var receiver2: String
    private lateinit var help: String
    lateinit var listener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomMessagingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        messages = binding.messages
        reference = FirebaseDatabase.getInstance().reference
        send = binding.send
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        messageArea = binding.messageArea
        receiver1 = intent.getStringExtra("uid")!!
        receiver2 = uid

        help = if (uid == receiver1) {receiver2} else{receiver1}

        messages.setHasFixedSize(true)
        messages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val textList = mutableListOf("I commented on Figma, I want to add some fancy icons. Do you have any icon set?",
            "I am in a process of designing some. When do you need them?",
            "Next month?", "I am almost finish. Please give me your email, I will ZIP them and send you as son as im finish.",
            "?", "maciej.kowalski@email.com") as ArrayList<String>
        //messages.adapter = MessageAdapter(this, textList)

        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = ArrayList<Message>()
                val mailList = ArrayList<String>()

                for (i in snapshot.children) {
                    for (j in i.children) {
                        val message = Message()
                        Log.e("hello", j.child("message").value.toString() + "aaa")
                        message.message = j.child("message").value.toString()
                        message.uid = j.child("uid").value.toString()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val date = j.child("date").value as HashMap<*, *>
                            val time = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(date["time"] as Long),
                                TimeZone.getDefault().toZoneId())
                            message.date = time
                        }
                        messageList.add(message)
                        mailList.add(j.child("email").value.toString())
                    }
                }

                messageList.sortWith {obj1: Message, obj2: Message ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        obj1.date.compareTo(obj2.date)
                    } else {
                        TODO("VERSION.SDK_INT < O")
                    }
                }

                reference.child("users").get().addOnSuccessListener {
                    for (i in it.children) {
                        mailList.add(i.value.toString())
                    }
                }

                adapter = MessageAdapter(this@RoomMessaging, messageList, uid, mailList)
                messages.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        reference.child("roomMessages").child(intent.getStringExtra("uid")!!)
            .addValueEventListener(listener)

        send.setOnClickListener {
            if (messageArea.text.isNotEmpty()) {
                val data = HashMap<String, Any>()
                data["message"] = messageArea.text.toString()
                data["uid"] = uid
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    data["date"] = Date()
                }
                val email = GoogleSignIn.getLastSignedInAccount(this)!!.email
                data["email"] = email.toString()

                reference.child("roomMessages").child(intent.getStringExtra("uid")!!)
                    .child(uid).child(Random.nextInt().toString()).setValue(data)
                    .addOnSuccessListener {
                        messageArea.text.clear()
                    }
            }
        }
    }
}