package com.jay.nearbysample.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * To add fragment from activity
 */
fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int, backStackTag: String? = null) {
    supportFragmentManager.inTransaction {
        add(frameId, fragment)
        backStackTag?.let { addToBackStack(it) }
    }
}

/**
 * To remove fragment from activity
 */
fun AppCompatActivity.removeFragment(frameId: Int) {
    val fragment = supportFragmentManager.findFragmentById(frameId)
    if (fragment != null)
        supportFragmentManager.beginTransaction().remove(fragment).commit()
}

/**
 * To replace fragment from activity
 */
fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    frameId: Int,
    backStackTag: String? = null
) {
    supportFragmentManager.inTransaction {
        replace(frameId, fragment)
        backStackTag?.let { addToBackStack(it) }
    }
}

/**
 * To get the fragment transaction
 */
inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}

/**
 * To replace fragment from fragment
 */
fun Fragment.replaceFragment(
    context: AppCompatActivity,
    fragment: Fragment,
    frameId: Int,
    backStackTag: String? = null
) {
    context.supportFragmentManager.inTransaction {
        replace(frameId, fragment)
        backStackTag?.let { addToBackStack(it) }
    }
}
