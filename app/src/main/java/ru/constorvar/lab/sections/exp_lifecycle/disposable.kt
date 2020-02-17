package ru.constorvar.lab.sections.exp_lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.disposables.Disposable

@JvmOverloads
internal fun <D: Disposable> D.destroyedBy(lifecycleOwner: LifecycleOwner,
                                           finalAction: (D) -> Unit = Disposable::dispose) {
    lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {

        override fun onDestroy(owner: LifecycleOwner) {
            finalAction(this@destroyedBy)
            owner.lifecycle.removeObserver(this)
        }

    })
}