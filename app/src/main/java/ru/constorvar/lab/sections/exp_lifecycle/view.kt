package ru.constorvar.lab.sections.exp_lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

@JvmOverloads
internal fun <T: Any> T.managedBy(lifecycleOwner: LifecycleOwner,
                                  onCreate: ((T) -> Unit)? = null,
                                  onStart: ((T) -> Unit)? = null,
                                  onResume: ((T) -> Unit)? = null,
                                  onPause: ((T) -> Unit)? = null,
                                  onStop: ((T) -> Unit)? = null,
                                  onDestroy: ((T) -> Unit)? = null) {
    lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {

        override fun onCreate(owner: LifecycleOwner) {
            onCreate?.invoke(this@managedBy)
        }

        override fun onStart(owner: LifecycleOwner) {
            onStart?.invoke(this@managedBy)
        }

        override fun onResume(owner: LifecycleOwner) {
            onResume?.invoke(this@managedBy)
        }

        override fun onPause(owner: LifecycleOwner) {
            onPause?.invoke(this@managedBy)
        }

        override fun onStop(owner: LifecycleOwner) {
            onStop?.invoke(this@managedBy)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            onDestroy?.invoke(this@managedBy)
            owner.lifecycle.removeObserver(this)
        }

    })
}