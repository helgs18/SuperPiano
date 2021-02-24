package no.uia.ikt205.superpiano

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
            val path = this.activity?.getExternalFilesDir(null)
            if (score.count() > 0 && fileName.isNotEmpty() && path != null){
                fileName = "$fileName.musikk"
                FileOutputStream(File(path,fileName),true).bufferedWriter().use { writer ->
                    // bufferdWriter lever her
                    score.forEach {
                        writer.write("${it.toString()}\n")
                    }
                }
            } else {
                /// TODO: What to do?
            }
        }

        return view
    }

}