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
import ru.constorvar.lab.R
import java.util.concurrent.TimeUnit

internal class LifecycleFashionedFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btn2 = view.findViewById<Button>(R.id.btn_action2)

        RxView.clicks(btn2)
            .debounce(250, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                performAction2()
            }, Throwable::printStackTrace)
            .destroyedBy(viewLifecycleOwner)

        val btn1 = view.findViewById<View>(R.id.btn_action1)
        btn1.managedBy(
            viewLifecycleOwner,
            onCreate = { it.setOnClickListener{ performAction1() } },
            onDestroy = { it.setOnClickListener(null) }
        )

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        val scrollListener = object : RecyclerView.OnScrollListener() {}

        recyclerView.managedBy(
            viewLifecycleOwner,
            onCreate = { it.addOnScrollListener(scrollListener) },
            onDestroy = { it.removeOnScrollListener(scrollListener) }
        )
    }

    private fun performAction1() {
        Toast.makeText(requireContext(), "Action1 performed", Toast.LENGTH_SHORT).show()
    }

    private fun performAction2() {
        Toast.makeText(requireContext(), "Action2 performed", Toast.LENGTH_SHORT).show()
    }
}