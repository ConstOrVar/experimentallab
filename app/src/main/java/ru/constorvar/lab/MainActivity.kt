package ru.constorvar.lab

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.constorvar.lab.sections.exp_hostaware.TestHostAwareAcitivity
import ru.constorvar.lab.sections.exp_lifecycle.TestLifecycleActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = SectionAdapter(listOf(*ExperimentSection.values())) {
            it.action().invoke(this)
        }

        findViewById<RecyclerView>(R.id.recycler_view_sections).also {
            it.layoutManager = LinearLayoutManager(it.context)
            it.adapter = adapter
        }
    }

}

private enum class ExperimentSection {
    LIFECYCLE {
        override fun action(): (Activity) -> Unit {
            return { it.startActivity(Intent(it, TestLifecycleActivity::class.java)) }
        }
    },
    HOST_AWARE {
        override fun action(): (Activity) -> Unit {
            return { it.startActivity(Intent(it, TestHostAwareAcitivity::class.java)) }
        }
    }
    ;

    abstract fun action(): (Activity) -> Unit
}

private class SectionAdapter(
    private val sections: List<ExperimentSection>,
    private val onClick: (ExperimentSection) -> Unit
) : RecyclerView.Adapter<SectionAdapter.SectionViewHolder>() {

    override fun getItemCount(): Int {
        return sections.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SectionViewHolder(inflater.inflate(R.layout.item_section, parent, false))
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.txtTitle.text = sections[position].name
        holder.itemView.setOnClickListener {
            onClick(sections[holder.adapterPosition])
        }
    }

    private class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitle by lazy { view.findViewById<TextView>(R.id.txt_title) }
    }
}





