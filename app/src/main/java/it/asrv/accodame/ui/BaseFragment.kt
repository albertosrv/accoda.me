package it.asrv.accodame.ui

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import it.asrv.accodame.R

open class BaseFragment : Fragment() {

    protected fun showMessage(title : String, message: String, positiveButton: String, positiveListener : DialogInterface.OnClickListener?, negativeButton: String?, negativeListener : DialogInterface.OnClickListener?){
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }
        builder?.setMessage(message)?.setTitle(title)
        builder?.apply {
            setPositiveButton(positiveButton, positiveListener)
            setNegativeButton(negativeButton, negativeListener)
        }
        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
    }

}