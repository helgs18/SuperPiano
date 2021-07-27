package no.uia.ikt205.superpiano

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_piano.view.*
import no.uia.ikt205.superpiano.data.Note
import no.uia.ikt205.superpiano.databinding.FragmentPianoBinding
import java.io.File
import java.io.FileOutputStream


class PianoLayout : Fragment() {
    var onSave:((file: Uri)->Unit)? = null  // ?(nullable) pga. dette er noe som kan settes externt

    private var _binding:FragmentPianoBinding? = null
    private val binding get() = _binding!!

    private val fullTones = listOf("C","D","E","F","G","A","B","C2","D2","E2","F2","G2")

    private var score:MutableList<Note> = mutableListOf<Note>() // Score == Noteark?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentPianoBinding.inflate(layoutInflater)
        val view = binding.root

        val fm = childFragmentManager
        val ft = fm.beginTransaction()

        fullTones.forEach { orgNoteValue ->
            val fullTonePianoKey = FullTonePianoKeyFragment.newInstance(orgNoteValue)
            var startPlay:Long = 0

            // .onKeyUp og .onKeyDown er delegat handlere. Dette er ikke funksjoner som pinaokey
            // har selv, den må få de fra oss.

            fullTonePianoKey.onKeyDown =  { note ->
                startPlay = System.nanoTime()
                println("Piano key down $note")
            }

            fullTonePianoKey.onKeyUp = {
                var endPlay = System.nanoTime()
                val note = Note(it, startPlay,endPlay)
                score.add(note)
                println("Piano key up $note")
            }

            ft.add(view.pianoKeys.id,fullTonePianoKey,"note_$orgNoteValue")
        }

        ft.commit()


        view.saveScoreBt.setOnClickListener {
            var fileName = view.fileNameTextEdit.text.toString()
            if (score.count() > 0 && fileName.isNotEmpty()){
                fileName = "$fileName.music"
                /* Map etterfulgt av en reduce. Dette her er funksjonell programmering.
                    Map = tar en liste og konverterer til en annen liste / mapper om. Her mapper vi
                        om en liste med noter til en liste med strings.
                    Reduce = tar en liste og gjør den om til en ting.
                    For å vise mere, valgte Christian File->Scratch File->Kotlin. Se bunnen av denne
                    filen, for å se map og reduce kode som jeg testet i Kotlin scratch-fil.
                 */
                val content: String = score.map{
                    it.toString()
                }.reduce {
                        acc, s -> acc + s + "\n"
                }
                saveFile(fileName, content)
            } else {
                /// TODO: no music or missing filename
            }
        }

        return view
    }

    private fun saveFile(fileName: String, content: String){
        val path = this.activity?.getExternalFilesDir(null)
        if (path != null){
            val file = File(path, fileName)
            FileOutputStream(file,true).bufferedWriter().use { writer ->
                writer.write(content)
            }

            this.onSave?.invoke(file.toURI());

        } else {
            // Else: could not get external path. Warn user?
        }
    }

}

/* map og reduce kode som jeg testet ut i scratch.kts:
val test = listOf<Int>(1, 2, 3, 4, 5)
val res = test.reduce{ acc:Int, i:Int -> acc + i } // res:Int = 15

// List<Int> to List<Double> ([1.0, 2.0, 3.0, 4.0, 5.0])
val rapper: List<Double> = test.map{
    it.toDouble()
}

// List<Int> to List<String> (["1","2","3","4","5"])
val strapper: List<String> = test.map {
    it.toString()
}

// List<Int> to List<Int?> ([null, 2, null, 4, null])
val boler: List<Int?> = test.map{
    if (it % 2 == 0)
        it.toInt()
    else
        null
}
 */

/* Delegater
fullTonePianoKey.onKeyUp{} og fullTonePianoKey.onKeyDown{} er delegat-handlere.
Dette er ikke funksjoner som fullTonePianoKey har selv, og PianoKey må få de fra oss.
 */