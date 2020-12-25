package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity
import timber.log.Timber


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    companion object {
        const val SIGN_IN_CODE = 1
    }

    private lateinit var binding: ActivityAuthenticationBinding
    private val viewModel: AuthenticationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel.authenticationState.observe(this, Observer { authenticationState ->
            if (authenticationState == AuthenticationState.UNAUTHENTICATED) {
                Timber.i("User is not authenticated. Starting the Sign In flow")
                binding.buttonLogin.setOnClickListener {
                    launchSignInFlow()
                }

            } else if (authenticationState == AuthenticationState.AUTHENTICATED) {
                binding.buttonLogin.setOnClickListener {
                    startRemindersActivity()
                }

                Timber.i("User is Authenticated. Starting the reminders activity")
            }

        })
//         TODO: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google

//          TODO: If the user was authenticated, send him to RemindersActivity

//          TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

    }

    private fun launchSignInFlow() {
        // we choose email and Goolge Sign In as providers. These are enabled in firebase console
        val providers =
            arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            SIGN_IN_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {

                Timber.i("Successful Signed in")
                startRemindersActivity()
            } else {
                if (response == null) {
                    Timber.i("Back button pressed")
                    return
                }
                if (response.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    Timber.i("Network issuer")
                }

            }
        }
    }

    fun startRemindersActivity() {
        val intent = Intent(this, RemindersActivity::class.java)
        startActivity(intent)
    }

}
