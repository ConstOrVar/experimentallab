package ru.constorvar.lab.sections.exp_hostaware

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.constorvar.lab.R
import java.util.*
import kotlin.properties.Delegates

data class User(
    val uuid: UUID,
    val name: String,
    val age: Int
)

interface UserDataSource {
    fun loadUsers(): Single<List<User>>
}

interface UserClickConsumer {
    fun onUserClicked(fragment: Fragment, user: User)
}

interface UserRenderDelegate {
    fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun bind(viewHolder: RecyclerView.ViewHolder, item: User, onClick: (User) -> Unit)
}

interface UsersHost {
    fun userDataSource(fragment: Fragment): UserDataSource

    fun userClickConsumer(): UserClickConsumer? = null

    fun userRenderDelegate(fragment: Fragment): UserRenderDelegate? = null
}

@Suppress("unused")
fun <T> T.createUsersFragment(): Fragment
        where T: UsersHost,
              T: Activity {
    return UsersFragment()
}

internal class UsersFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProviders
            .of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return when(modelClass) {
                        UserListViewModel::class.java -> {
                            val dataSource = findHost().userDataSource(this@UsersFragment)
                            UserListViewModel(dataSource) as T
                        }
                        else ->
                            throw IllegalArgumentException(modelClass.simpleName)
                    }
                }
            })
            .get(UserListViewModel::class.java)

        val renderDelegate = findHost().userRenderDelegate(this)
            ?: DefaultUserRenderDelegate()

        val clickConsumer: UserClickConsumer = findHost().userClickConsumer()
            ?: object : UserClickConsumer {
                override fun onUserClicked(fragment: Fragment, user: User) {
                    Toast
                        .makeText(
                            fragment.requireContext(),
                            "${user.name} clicked",
                            Toast.LENGTH_LONG
                        )
                        .show()
                }
            }

        val adapter = UserAdapter(
            emptyList(),
            renderDelegate) { user ->
            clickConsumer.onUserClicked(this, user)
        }

        view.findViewById<RecyclerView>(R.id.recycler_view).also {
            it.layoutManager = LinearLayoutManager(it.context)
            it.adapter = adapter
            it.addItemDecoration(DividerItemDecoration(it.context, DividerItemDecoration.HORIZONTAL))
        }

        viewModel.users.observe(viewLifecycleOwner, Observer {
            adapter.items = it ?: emptyList()
        })
    }

    private fun findHost(): UsersHost {
        val parent = parentFragment
        val activity = activity

        return when {
            parent is UsersHost -> parent
            activity is UsersHost -> activity
            else -> throw IllegalStateException()
        }
    }
}

private class UserListViewModel(
    private val dataSource: UserDataSource
) : ViewModel() {
    private val _users = MutableLiveData<List<User>>()
    private val disposable: Disposable

    init {
        disposable = dataSource
            .loadUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ items ->
                _users.value = items
            }, Throwable::printStackTrace)
    }

    val users: LiveData<List<User>> = _users

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}

private class UserAdapter(
    initialItems: List<User>,
    private val renderDelegate: UserRenderDelegate,
    private val onClick: (User) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<User> by Delegates.observable(initialItems) { _, old, new ->
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return renderDelegate.createViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        renderDelegate.bind(holder, items[position], onClick)
    }
}

private class DefaultUserRenderDelegate : UserRenderDelegate {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return UserViewHolder(inflater.inflate(R.layout.item_user, parent, false))
    }

    override fun bind(viewHolder: RecyclerView.ViewHolder, item: User, onClick: (User) -> Unit) {
        (viewHolder as UserViewHolder).apply {
            txtName.text = item.name
            txtAge.text = item.age.toString()
            itemView.setOnClickListener { onClick(item) }
        }
    }

    private class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName by lazy { view.findViewById<TextView>(R.id.txt_name) }
        val txtAge by lazy { view.findViewById<TextView>(R.id.txt_age) }
    }
}