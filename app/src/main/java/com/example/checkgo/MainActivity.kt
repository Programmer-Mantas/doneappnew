package com.example.checkgo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkgo.databinding.ActivityMainBinding
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var eventRecyclerView: RecyclerView
    private lateinit var eventList: ArrayList<EventModel>
    ////////////////////////////////////////////////////////
    private lateinit var tempSearchList: ArrayList<EventModel>
    ///////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventRecyclerView = findViewById(R.id.recyclerView_showEvents)
        eventRecyclerView.layoutManager = LinearLayoutManager(this)
        eventRecyclerView.setHasFixedSize(true)
        eventList = arrayListOf<EventModel>()
        ///////////////////////////////////////////////////////
        tempSearchList = arrayListOf<EventModel>()
        ///////////////////////////////////////////////////////
        getEventData()

        binding.buttonMainCreateEvent.setOnClickListener{
            val intent = Intent(this,CreateEvent::class.java)
            startActivity(intent)
        }
        binding.buttonMainMeniu.setOnClickListener{
            val intent = Intent(this,UserProfile::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_item,menu)
        val item = menu?.findItem(R.id.search_action)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Toast.makeText(this,"Uploading...", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempSearchList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if(searchText.isNotEmpty()){
                    eventList.forEach{
                        if(it.name!!.toLowerCase(Locale.getDefault()).contains(searchText) or it.date!!.toLowerCase(Locale.getDefault()).contains(searchText) or it.location!!.toLowerCase(Locale.getDefault()).contains(searchText)){
                            tempSearchList.add(it)
                        }
                    }
                    eventRecyclerView.adapter!!.notifyDataSetChanged()

                }else{
                    tempSearchList.clear()
                    tempSearchList.addAll(eventList)
                    eventRecyclerView.adapter!!.notifyDataSetChanged()
                }
//                val adapter = EventAdapter(tempSearchList)
//                eventRecyclerView.adapter = adapter
                return false
            }


        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun getEventData() {
        //if u want to add some kind of loading circle set it on here
        eventRecyclerView.visibility = View.GONE

        dbRef = FirebaseDatabase.getInstance().getReference("Event")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventList.clear()
                for (childSnapshot in snapshot.children) {
                    val event = childSnapshot.getValue(EventModel::class.java)
                    eventList.add(event!!)
                    //////////////////////////////////////////////////
                    tempSearchList.add(event!!)
                    //////////////////////////////////////////////////

                }
                val mAdapter = EventAdapter(tempSearchList)
                eventRecyclerView.adapter = mAdapter

                mAdapter.setOnItemClickListener(object : EventAdapter.interonItemClickListener {
                    override fun onItemClickListener(position: Int) {
                        val intent = Intent(this@MainActivity,EventShowDetails::class.java)
                        intent.putExtra("eventID",eventList[position].eventID)
                        intent.putExtra("eventName", eventList[position].name)
                        intent.putExtra("eventType", eventList[position].type)
                        intent.putExtra("eventDate", eventList[position].date)
                        intent.putExtra("eventDetails", eventList[position].details)
                        intent.putExtra("eventLocation", eventList[position].location)
                        startActivity(intent)
                    }

                })
                //if u want to add some kind of loading circle set that loading off and set recycleview visible
                eventRecyclerView.visibility = View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}

