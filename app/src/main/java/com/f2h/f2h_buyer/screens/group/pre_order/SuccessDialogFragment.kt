package com.f2h.f2h_buyer.screens.group.pre_order

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.f2h.f2h_buyer.R

class SuccessDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            builder.setView(inflater.inflate(R.layout.fragment_pre_order_success_dialog, null))
                .setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
//                .setNegativeButton("My Orders",
//                    DialogInterface.OnClickListener { dialog, id ->
//                        // Open my orders page
//                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}