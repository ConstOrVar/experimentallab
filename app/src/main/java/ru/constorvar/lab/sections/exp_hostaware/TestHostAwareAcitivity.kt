package ru.constorvar.lab.sections.exp_hostaware

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import io.reactivex.Single
import ru.constorvar.lab.R
import java.util.*

internal class TestHostAwareAcitivity : AppCompatActivity(),
    ApprovementHost,
    UsersHost {

    //region ApprovementHost
    override fun onApproved(dialogFragment: DialogFragment) {
        finish()
    }
    //endregion

    //region UsersHost
    override fun userDataSource(fragment: Fragment): UserDataSource {
        return ExternalUserDataSource()
    }

    override fun userClickConsumer(): UserClickConsumer? {
        return object : UserClickConsumer {
            override fun onUserClicked(fragment: Fragment, user: User) {
                Toast
                    .makeText(fragment.requireContext(), "Intercepted click on ${user.name} with age ${user.age}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitvity_test_hostaware)

        if(savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.frame_container, createUsersFragment())
                .commit()
        }
    }

    override fun onBackPressed() {
        createApprovementDialog("Notification", "Do you really want do that?")
            .show(supportFragmentManager, TAG_DIALOG)
    }

    internal companion object {
        private const val TAG_DIALOG = "notification_dialog"
    }
}

private class ExternalUserDataSource : UserDataSource {
    override fun loadUsers(): Single<List<User>> {
        return Single.just(
            listOf(
                User(UUID.randomUUID(),"John", 30),
                User(UUID.randomUUID(),"Elvis", 25),
                User(UUID.randomUUID(),"Mike", 27)
            )
        )
    }
}