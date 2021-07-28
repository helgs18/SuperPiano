package no.uia.ikt205.superpiano // Pakkenavnet er viktig ved oppretting av App i Firebase

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
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

    private lateinit var piano:PianoLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_main) // Må bruke binding.root i stedet (se under)
        binding = ActivityMainBinding.inflate(layoutInflater)   // Er lik måten vi brukte ved bruk av fragments
        setContentView(binding.root)    // Fikk svart skjerm da denne manglet
        auth = Firebase.auth    // henter ut en instanse på denne
        signInAnonymously()

        // binding.piano er et containerFragmentView
        piano = supportFragmentManager.findFragmentById(binding.piano.id) as PianoLayout

        piano.onSave = { fileUri ->
            this.upload(fileUri)
        }
        // eller piano.onSave = { this.upload(it) }
        // piano.onSave kommer fra PianoLayout.kt (husk at piano er en PianoLayout type):
        //  var onSave:((file: Uri)->Unit)? = null
    }

    private fun upload(file: Uri){
        Log.d(TAG, "Upload file $file")

        // Firebase krever at du lagerreferanser til ting før du laster de opp
        // sleit med å få lagt til lastPathSegment, så jeg skrevi $file.lastPathSegment og deretter
        // formaterte Android Studio det til brukendes kode.
        // Mappen melodies blei opprettet automatisk i Firebase.
        val ref = FirebaseStorage.getInstance().reference.child("melodies/${file.lastPathSegment}")
        var uploadTask = ref.putFile(file)
        /* kunne brukt brukernavn:
        //     val ref = FirebaseStorage.getIntance().reference.child(
        //          "melodies/$user/${file.lastPathSegment}")
        // I tillegg kunne du brukt Firebase Functions (i Firebase menyen for prosjektet), og
        // her er det tester som du kunne ha brukt. Eller du kunne brukt Cloud Firestore for å
        // lagre en liste over brukernavn, og så sjekker du om brukernavnet er tatt.*/

        uploadTask.addOnSuccessListener {
            Log.d(TAG, "Saved file ${it.toString()}")
        }.addOnFailureListener() {
            Log.e(TAG, "Error saving file to fb", it)
        }
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