package com.example.checkgo


import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.checkgo.databinding.ActivityCreateEventBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*


class CreateEvent : AppCompatActivity(), AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    lateinit var binding: ActivityCreateEventBinding
    private val calendar = Calendar.getInstance()
    private val formatData = SimpleDateFormat("MMM. dd. yyyy",Locale.ENGLISH)
    private lateinit var dbRef: DatabaseReference
    private lateinit var imageUri: Uri
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbRef = FirebaseDatabase.getInstance().getReference("Event")
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinner: Spinner = findViewById(R.id.dropbox_createEvent)
        val spinner2: Spinner = findViewById(R.id.dropbox_createEventLocation)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>, arg1: View?,
                arg2: Int, arg3: Long
            )
            { binding.eventType.editText?.setText(spinner.selectedItem.toString()) }
            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
       //////////////////////////////////////////////////
        ArrayAdapter.createFromResource(
            this,
            R.array.city,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner2.adapter = adapter
        }

        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>, arg1: View?,
                arg2: Int, arg3: Long
            )
            { binding.eventLocationCity.editText?.setText(spinner2.selectedItem.toString()) }
            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
        /////////////////////////////

        binding.eventDate.setOnClickListener{
            DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()

        }
        binding.buttonCreateEventUploadPhoto.setOnClickListener{
            selectImage()
        }

        binding.buttonCreateEventSubmitInfo.setOnClickListener{
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Uploading Event ...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val name = binding.eventName.editText?.text.toString()
            val selectedItem = spinner.selectedItem.toString()
            val eventDate = binding.eventDate.text.toString()
            val eventDetails = binding.eventDetails.editText?.text.toString()
            val eventLocation = spinner2.selectedItem.toString()
            //connect event to a user which created the event

            if(name.isNotEmpty() && selectedItem.isNotEmpty() && eventDate.isNotEmpty() && eventDetails.isNotEmpty() && eventLocation.isNotEmpty()){
                val eventID = dbRef.push().key!!
                val event = EventModel(eventID,name,selectedItem,eventDate,eventDetails,eventLocation)
                dbRef.child(eventID).setValue(event).addOnCompleteListener {
                        if (it.isSuccessful) {
                            storageReference = FirebaseStorage.getInstance().getReference("Event/$eventID")
                            storageReference.putFile(imageUri)
                            progressDialog.dismiss()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

            }else{
                progressDialog.dismiss()
                Toast.makeText(this,"All fields must be not empty", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && resultCode == RESULT_OK){
            imageUri = data?.data!!
            binding.imageCreateEvent.setImageURI(imageUri)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.adapter?.getItem(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year,month,dayOfMonth)
        displayFormattedDate(calendar.timeInMillis)
    }
    private fun displayFormattedDate(timeInMillis: Long) {
        binding.eventDate.text = formatData.format(timeInMillis)
    }
}

