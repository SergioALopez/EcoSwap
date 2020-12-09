package com.example.ecoswap.ui.vendor

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.ecoswap.R
import com.example.ecoswap.ui.admin.AdminActivity
import com.example.ecoswap.ui.user.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100
    private lateinit var ref: DatabaseReference
    private lateinit var data: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        ref = FirebaseDatabase.getInstance().reference.child("roles")
        data = FirebaseDatabase.getInstance().reference.child("Users")

        // Analytics Event
        val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

        // Setup
        setup()
        session()
    }

    override fun onStart() {
        super.onStart()

        //authLayout.visibility = View.VISIBLE
    }

    private fun session() {

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val name = prefs.getString("name", null)
        //val id = prefs.getString("id", null)
        //val provider = prefs.getString("provider", null)

        if(email != null && name != null) {
            //authLayout.visibility = View.INVISIBLE
            val userUID = FirebaseAuth.getInstance().currentUser?.uid
            println("regresando : ${userUID}")
            ref.child(userUID ?: "").addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val role = snapshot.value.toString()
                    if(role.equals("admin")) {
                        println("Bienvenido admin")
                        //showAdmin(email, name, ProviderType.valueOf(provider))
                        showAdmin(email, name)
                    } else if(role.equals("vendor")) {
                        showVendor(email, name)
                    } else {
                        showHome(email, name, userUID ?: "")
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }
    }

    private fun setup() {
        title = "Autentificacion"

        signUpBtn.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailEditText.text.toString(),
                    passwordEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userUID = it.result?.user?.uid ?: ""
                        val userEmail = it.result?.user?.email ?: ""
                        val userName = it.result?.user?.displayName ?: "Mi Pantalla"
                        dataColl(userUID ?: "", userEmail)
                        //showHome(userEmail, userName,ProviderType.BASIC)
                        showHome(userEmail, userName, userUID)
                    } else {
                        showAlert()
                    }
                }

            }
        }

        logInBtn.setOnClickListener {
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEditText.text.toString(),
                    passwordEditText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userUID = it.result?.user?.uid ?: ""
                        val userEmail = it.result?.user?.email ?: ""
                        val userName = it.result?.user?.displayName ?: "Mi Pantalla"
                        //dataColl(userUID ?: "", userEmail)
                        //showHome(userEmail, userName, ProviderType.BASIC)
                        showHome(userEmail, userName, userUID)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        googleBtn.setOnClickListener {
            val googleConf =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autentificando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, name: String, id: String) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("name", name)
            putExtra("id", id)
        }
        startActivity(homeIntent)
    }

    private fun showVendor(email: String, name: String) {
        val vendIntent = Intent(this, VendActivity::class.java).apply {
            putExtra("email", email)
            putExtra("name", name)
        }
        startActivity(vendIntent)
    }

    private fun showAdmin(email: String, name: String) {
        val adminIntent = Intent(this, AdminActivity::class.java).apply {
            putExtra("email", email)
            putExtra("name", name)
        }
        startActivity(adminIntent)
    }

    private fun dataColl(uid: String, email: String) {
        val map: HashMap<String, Any> = HashMap<String, Any>()
        data.child("Users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children.toString()) {
                    if (postSnapshot.equals(uid)) {
                        return
                    } else {
                        map.put("Email", email)
                        map.put("Points", 0)
                    }
                }
                data.child(uid).updateChildren(map)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if(account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if(it.isSuccessful) {

                            val userUID = it.result?.user?.uid ?: ""
                            val userEmail = it.result?.user?.email ?: ""
                            val userName = it.result?.user?.displayName ?: "Mi Pantalla"
                            println("el uid es: " + userUID)
                            println("display name: " + it.result?.user?.displayName)
                            println("email: " + it.result?.user?.email)
                            // imagen -> it.result?.user?.photoUrl

                            ref.child(userUID ?: "").addValueEventListener(object: ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val role = snapshot.value.toString()
                                    if(role.equals("admin")) {
                                        //ProviderType.GOOGLE
                                        showAdmin(account.email ?: "", account.displayName ?: "Mi Pantalla")
                                    } else if(role.equals("vendor")){
                                        //ProviderType.GOOGLE
                                        showVendor(account.email ?: "", account.displayName ?: "Mi Pantalla")
                                    } else {
                                        dataColl(userUID ?: "", userEmail)
                                        //ProviderType.BASIC
                                        showHome(userEmail, userName, userUID)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })

                        } else {
                            showAlert()
                        }
                    }
                }
            } catch (e: ApiException) {
                showAlert()
            }

        }
    }
}




