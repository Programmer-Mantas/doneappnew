package com.example.checkgo

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class EventAdapter (private val eventList: ArrayList<EventModel>): RecyclerView.Adapter<EventAdapter.ViewHolder>(){

    private lateinit var mListener: interonItemClickListener
    private lateinit var storageReference: StorageReference

    interface interonItemClickListener{
        fun onItemClickListener(position: Int)
    }
    fun setOnItemClickListener(clickListener: interonItemClickListener){
        mListener = clickListener
    }

    class ViewHolder(itemView: View, clickListener: interonItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val tvEventName : TextView = itemView.findViewById(R.id.tvEventName)
        val ivEventPicture : ImageView = itemView.findViewById(R.id.ivEventPhoto)
        val tvEventDate : TextView = itemView.findViewById(R.id.tvEventDate)
        val tvEventLocation : TextView = itemView.findViewById(R.id.tvEventLocation)
        init {
            itemView.setOnClickListener{
                clickListener.onItemClickListener(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =LayoutInflater.from(parent.context)
            .inflate(R.layout.event_list_items,parent,false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEvent = eventList[position]
        holder.tvEventName.text = currentEvent.name
        holder.tvEventDate.text = currentEvent.date
        holder.tvEventLocation.text = currentEvent.location
        val uid = currentEvent.eventID.toString()
        if(uid.isNotEmpty()){
            storageReference = FirebaseStorage.getInstance().reference.child("Event/$uid")
            val localFile = File.createTempFile("tempImage","jpg")
            storageReference.getFile(localFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                holder.ivEventPicture.setImageBitmap(bitmap)
            }
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }


}