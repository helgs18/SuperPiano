package no.uia.ikt205.superpiano // Pakkenavnet er viktig ved oppretting av App i Firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import no.uia.ikt205.superpiano.databinding.ActivityMainBinding

//class MainActivity(auth: FirebaseAuth) : AppCompatActivity() {
class MainActivity : AppCompatActivity() {

    private val TAG: String = "SuperPiano:MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    /*"private var auth: FirebaseAuth"    // Error: Property must be initialized or abstract, dersom
    // vi ikke bruker lateinit og har ikke lagt til "auth: FirebaseAuth" i konstruktøren. Fikk feil
    // da jeg prøvde å gjøre det på denne måten (se kommentert ut kode for MainActivity klassen).
    // Kunne prøvd:
    // "private var auth: FirebaseAuth? = null",
    // men da får vi et nytt problem, for da får vi en ting som kan være null eller ikke null.*/



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main) // Må bruke binding.root i stedet (se under)
        binding = ActivityMainBinding.inflate(layoutInflater)   // Er lik måten vi brukte ved bruk av fragments
        setContentView(binding.root)    // Fikk svart skjerm da denne manglet
        auth = Firebase.auth    // henter ut en instanse på denne
        signInAnonymously()
    }

    private fun signInAnonymously(){
        // Nokså likt promise pattern. Kunne pakket inn alt i en service og brukt signals og "slikt".
        auth.signInAnonymously().addOnSuccessListener {
            Log.d(TAG, "Login success ${it.user.toString()}")
        }.addOnFailureListener {
            Log.e(TAG, "Login failed", it)  // it = exception meldingen
        }
    }
}