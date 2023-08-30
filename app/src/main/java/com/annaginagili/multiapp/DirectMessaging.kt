package com.annaginagili.multiapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annaginagili.multiapp.databinding.ActivityDirectMessagingBinding
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

class DirectMessaging : AppCompatActivity() {
    lateinit var binding: ActivityDirectMessagingBinding
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
        binding = ActivityDirectMessagingBinding.inflate(layoutInflater)
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

        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = ArrayList<Message>()
                val mailList = ArrayList<String>()

                for (i in snapshot.children) {
                    val message = Message()
                    message.message = i.child("message").value.toString()
                    message.uid = i.child("uid").value.toString()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val date = i.child("date").value as HashMap<*, *>
                        val time = LocalDateTime.ofInstant(Instant.ofEpochMilli(date["time"] as Long),
                            TimeZone.getDefault().toZoneId())
                        message.date = time
                    }
                    mailList.add(i.child("email").value.toString())
                    messageList.add(message)

                    adapter = MessageAdapter(this@DirectMessaging, messageList, uid, mailList)
                    messages.adapter = adapter
                }

                messageList.sortWith {obj1: Message, obj2: Message ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        obj1.date.compareTo(obj2.date)
                    } else {
                        TODO("VERSION.SDK_INT < O")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        reference.child(uid).child(help).addValueEventListener(listener)

        send.setOnClickListener {
            val email = GoogleSignIn.getLastSignedInAccount(this)!!.email
            if (messageArea.text.isNotEmpty()) {
                val data = HashMap<String, Any>()
                data["message"] = messageArea.text.toString()
                data["uid"] = uid
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    data["date"] = Date()
                }
                data["email"] = email.toString()

                reference.child(uid).child(help).child(Random.nextInt().toString()).setValue(data)
                    .addOnSuccessListener {
                        reference.child(help).child(uid).child(Random.nextInt().toString())
                            .setValue(data).addOnSuccessListener {
                                messageArea.text.clear()
                            }
                    }
            }
        }
    }
}