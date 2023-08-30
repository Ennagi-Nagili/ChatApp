package com.annaginagili.multiapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annaginagili.multiapp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var rooms: RecyclerView
    lateinit var adapter: RoomAdapter
    lateinit var chats: RecyclerView
    lateinit var chatAdapter: ChatAdapter
    lateinit var preferences: SharedPreferences
    lateinit var auth: FirebaseAuth
    lateinit var signInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions
    lateinit var reference: DatabaseReference
    lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rooms = binding.rooms
        chats = binding.chats
        preferences = getSharedPreferences("MultiApp", MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().reference

        if (preferences.getString("token", null) == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        else {
            uid = auth.currentUser!!.uid
        }

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.api_key)).requestEmail().build()
        signInClient = GoogleSignIn.getClient(this, gso)

        binding.add.setOnClickListener {
            signInClient.signOut().addOnSuccessListener {
                preferences.edit().clear().apply()
            }
        }

        rooms.setHasFixedSize(true)
        rooms.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        reference.child("rooms").get().addOnSuccessListener {
            val idList = ArrayList<String>()
            val imageList = ArrayList<Int>()
            val list = mutableListOf(R.drawable.blue_green, R.drawable.blue_purple, R.drawable.purple_green,
                R.drawable.green_white) as ArrayList<Int>

            var t = 0
            for (i in it.children) {
                if (t == 4) {
                    t = 0
                }
                idList.add(i.key.toString())
                imageList.add(list[t])
                t++
            }
            adapter = RoomAdapter(this, imageList, idList)
            rooms.adapter = adapter

            rooms.addItemDecoration(DotsIndicator(8, 30, 20, ContextCompat
                .getColor(this, R.color.light_grey), ContextCompat.getColor(this, R.color.dark_grey)))
        }

        chats.setHasFixedSize(true)
        chats.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        reference.child("users").get().addOnSuccessListener {
            val nameList = ArrayList<String>()
            val imageList2 = ArrayList<Int>()
            val lastList = ArrayList<String>()
            val timeList = ArrayList<String>()
            val uidList = ArrayList<String>()
            for (i in it.children) {
                if (i.key != uid) {
                    nameList.add(i.value.toString())
                    imageList2.add(R.drawable.profile)
                    lastList.add("maciej.kowalski@email.com")
                    timeList.add("08:43")
                    uidList.add(i.key.toString())
                }
            }
            chatAdapter = ChatAdapter(this, imageList2, nameList, lastList, timeList, uidList)
            chats.adapter = chatAdapter
        }
    }
}