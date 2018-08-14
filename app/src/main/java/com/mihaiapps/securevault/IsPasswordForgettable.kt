package com.mihaiapps.securevault


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.mihaiapps.securevault.bl.enc.PasswordManager
import kotlinx.android.synthetic.main.fragment_is_password_forgettable.*
import org.koin.android.ext.android.inject


class IsPasswordForgettable : Fragment() {

    val passwordManager: PasswordManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_is_password_forgettable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        forget_btn.setOnClickListener {
            passwordManager.setIsPasswordForgettable(true)
            navigateToLogin()
        }

        not_forget_btn.setOnClickListener{
            passwordManager.setIsPasswordForgettable(false)
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_isPasswordForgetable_to_login)
    }


}
