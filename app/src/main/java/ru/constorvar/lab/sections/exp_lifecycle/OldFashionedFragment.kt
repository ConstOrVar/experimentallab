package ru.constorvar.lab.sections.exp_lifecycle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import ru.constorvar.lab.R
import java.util.concurrent.TimeUnit

internal class OldFashionedFragment : Fragment() {
    private var btn1: Button? = null
    private var recyclerView: RecyclerView? = null
    private var scrollListener: RecyclerView.OnScrollListener? = null

    private var disposables: CompositeDisposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn1 = view.findViewById<Button>(R.id.btn_action1).apply {
            setOnClickListener {
                performAction1()
            }
        }

        val btn2 = view.findViewById<Button>(R.id.btn_action2)

        disposables = CompositeDisposable().apply {
            add(
                RxView.clicks(btn2)
                    .debounce(250, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        performAction2()
                    }, Throwable::printStackTrace)
            )
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        val scrollListener = object : RecyclerView.OnScrollListener() {}
        recyclerView.addOnScrollListener(scrollListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        disposables?.dispose()
        disposables = null

        btn1?.setOnClickListener(null)
        btn1 = null

        scrollListener?.let { listener ->
            recyclerView?.removeOnScrollListener(listener)
            scrollListener = null
        }
        recyclerView = null
    }

    private fun performAction1() {
        Toast.makeText(requireContext(), "Action1 performed", Toast.LENGTH_SHORT).show()
    }

    private fun performAction2() {
        Toast.makeText(requireContext(), "Action2 performed", Toast.LENGTH_SHORT).show()
    }
}