package ru.constorvar.lab.sections.exp_hostaware

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import ru.constorvar.lab.R

private const val ARG_TITLE = "title"
private const val ARG_QUESTION = "question"

interface ApprovementHost {
    fun onApproved(dialogFragment: DialogFragment)
    fun onDismissed(dialogFragment: DialogFragment) = dialogFragment.dismiss()
}

@Suppress("unused")
fun <T> T.createApprovementDialog(title: String, question: String): DialogFragment
        where T: Activity,
              T: ApprovementHost {
    return createDialog(title, question)
}

@Suppress("unused")
fun <T> T.createApprovementDialog(title: String, question: String): DialogFragment
        where T: Fragment,
              T: ApprovementHost {
    return createDialog(title, question)
}

private fun createDialog(title: String, question: String): DialogFragment {
    val args = Bundle().apply {
        putString(ARG_TITLE, title)
        putString(ARG_QUESTION, question)
    }
    return ApprovementDialogFragment().apply {
        arguments = args
    }
}

internal class ApprovementDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_approvement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setTitle(arguments?.getString(ARG_TITLE) ?: "")

        view.findViewById<TextView>(R.id.txt_question).text = arguments?.getString(ARG_QUESTION)

        view.findViewById<View>(R.id.btn_ok).setOnClickListener {
            findHost().onApproved(this)
        }

        view.findViewById<View>(R.id.btn_cancel).setOnClickListener {
            findHost().onDismissed(this)
        }
    }

    private fun findHost(): ApprovementHost {
        val parent = parentFragment
        val activity = activity

        return when {
            parent is ApprovementHost -> parent
            activity is ApprovementHost -> activity
            else -> throw IllegalStateException("No host found")
        }
    }
}