package com.example.checkgo

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class EventShowDetails : AppCompatActivity() {
    private lateinit var tvName: TextView
    private lateinit var tvType: TextView
    private lateinit var tvData: TextView
    private lateinit var tvDetails: TextView
    private lateinit var tvLocation: TextView
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button
    private lateinit var ivPicture: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_show_details)

        initView()
        setValuesToViews()
        showImage()
    }
    private fun showImage() {

        auth = FirebaseAuth.getInstance()
        uid = intent.getStringExtra("eventID").toString()
        if(uid.isNotEmpty()){
            storageReference = FirebaseStorage.getInstance().reference.child("Event/$uid")
            val localFile = File.createTempFile("tempImage","jpg")
            storageReference.getFile(localFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                ivPicture.setImageBitmap(bitmap)
            }
        }
    }

    private fun initView() {
        tvName = findViewById(R.id.textView_showEventDetails_name)
        tvType = findViewById(R.id.textView_showEventDetails_type)
        tvData = findViewById(R.id.textView_showEventDetails_date)
        tvDetails = findViewById(R.id.textView_showEventDetails_details)
        tvLocation = findViewById(R.id.textView_showEventDetails_location)
        btnUpdate = findViewById(R.id.button_showEventDetails_update)
        btnDelete = findViewById(R.id.button_showEventDetails_delete)
        ivPicture = findViewById(R.id.imageView)
    }
    private fun setValuesToViews(){
        tvName.text = intent.getStringExtra("eventName")
        tvType.text = intent.getStringExtra("eventType")
        tvData.text = intent.getStringExtra("eventDate")
        tvDetails.text = intent.getStringExtra("eventDetails")
        tvLocation.text = intent.getStringExtra("eventLocation")
    }
}