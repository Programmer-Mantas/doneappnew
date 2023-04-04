package com.example.checkgo

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.checkgo.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.upsemail.setOnClickListener{
            Toast.makeText(this,"Better luck next time", Toast.LENGTH_SHORT).show()
        }

        binding.buttonSignInWithGoogle.setOnClickListener{
            auth = FirebaseAuth.getInstance()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("565690502141-g200ib4t7jhbfqc2jd5k5lp0ifipddk9.apps.googleusercontent.com")
                .requestEmail()
                .requestProfile()
                .build()

            //AIzaSyBVQ25U4WUGNXDQNIdvUp8ydGcIzc65mAE(api key)

            googleSignInClient = GoogleSignIn.getClient(this, gso)
            signIn()
        }

        binding.buttonRegister.setOnClickListener{
           val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.buttonLogin.setOnClickListener {
            val email = binding.usernameLogin.editText?.text.toString()
            val pass = binding.passwordLogin.editText?.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty()){
                    firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener{
                        if(it.isSuccessful){
                            val intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener{
                        Toast.makeText(this,"Wrong information", Toast.LENGTH_SHORT).show()
                        binding.usernameLogin.editText?.text?.clear()
                        binding.passwordLogin.editText?.text?.clear()
                    }
            }else{
                Toast.makeText(this,"All fields must be not empty", Toast.LENGTH_SHORT).show()
            }
        }
    }




    /////////////////////////////////////////////////////////////////////////////////
    //Sign in with google
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    companion object{
        const val RC_SIGN_IN=1001
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode,resultCode,data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException){
                Log.w(ContentValues.TAG, "Google sign in failed",e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this){ task ->
            if(task.isSuccessful){
                val user = auth.currentUser
                updateUI(user)
            } else {
                Log.w(ContentValues.TAG, "Sign in with credential failed", task.exception)
                updateUI(null)
            }

        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user!=null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    //////////////////////////////////////////////////////////////////////////////////





    //auto start so if user was already logged in it would not ask him to log in again
    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

    }
}