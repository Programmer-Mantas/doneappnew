package com.example.checkgo

import android.R
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.checkgo.databinding.ActivityUserProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*


class UserProfile : AppCompatActivity() {

    private lateinit var binding : ActivityUserProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var dbRef : DatabaseReference
    private lateinit var storageReference: StorageReference
    //private lateinit var imageUri: Uri
    private lateinit var uid :String
    private lateinit var userInfo: UserInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        dbRef = FirebaseDatabase.getInstance().getReference("User")
        if( GoogleSignIn.getLastSignedInAccount(applicationContext) != null){getUserDataFromGoogle()}

//        if(uid.isNotEmpty()){
//            getUserDataFromGoogle()
//            }else{ getUserData()}
        getUserData()

        binding.buttonUserProfileGoBack.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        binding.buttonUserProfileSignOut.setOnClickListener{
//            if(uid.isNotEmpty()){signOutFromGoogle()}
            signOutFromGoogle()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getUserData() {
        dbRef.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userInfo = snapshot.getValue(UserInfo::class.java)!!
                binding.tvUserProfileName.text = userInfo.username
                binding.tvUserProfileEmail.text = userInfo.email
                binding.tvUserProfileAge.text = userInfo.age
                binding.tvUserProfileGender.text = userInfo.gender
                getUserProfilePic()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun getUserProfilePic() {
        storageReference = FirebaseStorage.getInstance().reference.child("User/$uid")
        val localFile = File.createTempFile("tempImage","jpg")
        storageReference.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.profilePicRegistration.setImageBitmap(bitmap)
        }
    }

    private fun getUserDataFromGoogle() {
        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (acct != null) {
            val personName = acct.displayName
            val personEmail = acct.email
            val personPhoto: Uri? = acct.photoUrl
            val age = "21"
            val gender = "Male"
            val photoUrl  = acct.photoUrl

            val userInfo = UserInfo(personName, personEmail, age, gender)
            dbRef.child(uid).setValue(userInfo).addOnSuccessListener {
                storageReference = FirebaseStorage.getInstance().getReference("User/${uid.toString()}")
                if (personPhoto != null) {
                    if (photoUrl != null) {
                        storageReference.putFile(photoUrl)
                    } //error unknown source
                }
            }
//            dbRef.child(personId.toString()).setValue(userInfo).addOnSuccessListener {
//                storageReference = FirebaseStorage.getInstance().getReference("User/${personId.toString()}")
//                if (personPhoto != null) {
//                    storageReference.putFile(personPhoto) //error unknown source $lambda-3 not existing
//                }
//            }
        }
    }


    private fun signOutFromGoogle(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("565690502141-g200ib4t7jhbfqc2jd5k5lp0ifipddk9.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
    }
}