package com.example.checkgo


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.checkgo.databinding.ActivitySingUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySingUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("User")

        binding.buttonLoginRegistration.setOnClickListener{
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }

        binding.profilePicRegistration.setOnClickListener(){
            selectImage()
        }

        binding.buttonRegisterRegistration.setOnClickListener {

            val email = binding.emailRegistration.editText?.text.toString()
            val pass = binding.passwordRegistration.editText?.text.toString()
            val confirmPass = binding.repeatpasswordRegistration.editText?.text.toString()
            val username = binding.usernameRegistration.editText?.text.toString()
            val age = binding.ageRegistration.editText?.text.toString()
            val gender = binding.genderRegistration.editText?.text.toString()


            if(email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && username.isNotEmpty() && age.isNotEmpty() && gender.isNotEmpty()){
                if(pass == confirmPass){
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener{
                        if(it.isSuccessful){
                            /////////////////////////////////////
                            val userInfo = UserInfo(username,email,age,gender)
                            val uid = firebaseAuth.currentUser?.uid
                            if (uid != null) {
                                databaseReference.child(uid).setValue(userInfo).addOnSuccessListener {
                                    storageReference = FirebaseStorage.getInstance().getReference("User/"+firebaseAuth.currentUser?.uid)
                                    storageReference.putFile(imageUri)
                                }
                            }

                            val intent = Intent(this,SignInActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                }else{
                    Toast.makeText(this,"Password is not matching ", Toast.LENGTH_SHORT).show()
                }

            }else{
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
            binding.profilePicRegistration.setImageURI(imageUri)
        }
    }


}