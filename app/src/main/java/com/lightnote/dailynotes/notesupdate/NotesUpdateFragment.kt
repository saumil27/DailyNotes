package com.lightnote.dailynotes.notesupdate

import android.app.AlertDialog
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.lightnote.dailynotes.R
import com.lightnote.dailynotes.database.Notes
import com.lightnote.dailynotes.database.NotesDao
import com.lightnote.dailynotes.database.NotesDatabase
import com.lightnote.dailynotes.databinding.NotesUpdateFragmentBinding
import com.lightnote.dailynotes.hideKeyboard
import com.lightnote.dailynotes.toast


class NotesUpdateFragment : Fragment() {


    private lateinit var notesUpdateViewModel: NotesUpdateViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //get reference to binding and infalte fragment
        val binding: NotesUpdateFragmentBinding = DataBindingUtil.inflate(inflater,R.layout.notes_update_fragment,container,false)

        val application: Application = requireNotNull(this.activity).application
        val datasource: NotesDao = NotesDatabase.getInstance(application).notesDao
        val arguments = NotesUpdateFragmentArgs.fromBundle(requireArguments())
        val viewModelFactory = NotesUpdateViewModelFactory(datasource,application,arguments.note)

        notesUpdateViewModel = ViewModelProvider(this,viewModelFactory).get(NotesUpdateViewModel::class.java)

        binding.lifecycleOwner = this

        binding.notesUpdateViewModel = notesUpdateViewModel



        binding.updateTitleEditText.setText(arguments.note.noteTitle)
        binding.updateNotesEditText.setText(arguments.note.noteDetail)
        binding.floatingUpdateButton.setOnClickListener(View.OnClickListener {

            var title = binding.updateTitleEditText.text.toString()
            var detail = binding.updateNotesEditText.text.toString()
            var note =Notes(noteTitle = title,noteDetail = detail)
            note.noteId = arguments.note.noteId

            if(title.isEmpty())
            {
                binding.titleUpdateTextInputLayout.error = getString(R.string.requireField)
                return@OnClickListener

            }
            //Upsert Insert onconflict Resolve
            notesUpdateViewModel.onAdded(note)
            context?.toast("Note updated")
            //hidekeyboard
            hideKeyboard(requireActivity(),requireView())
        })

        binding.floatingDeleteButton.setOnClickListener(View.OnClickListener {

            val builder = AlertDialog.Builder(context)

            builder.setMessage(getString(R.string.deleteAlertString)).setCancelable(false)
                .setPositiveButton("yes"){dialogInterface, _ ->
                    notesUpdateViewModel.onDelete(arguments.note)
                    context?.toast("Note Deleted")
                }.setNegativeButton("No"){dialogInterface, _ ->
                    dialogInterface.dismiss()
                }.create().show()

            hideKeyboard(requireActivity(),requireView())
        })

        notesUpdateViewModel.navigateToNotesDisplay.observe(viewLifecycleOwner, Observer {
            if(it)
            {
                findNavController().navigate(NotesUpdateFragmentDirections.actionNotesUpdateFragmentToNotesDisplayFragment())
                notesUpdateViewModel.onNavigateToNotesDisplayComplete()

            }
        })

        return binding.root
    }


}
