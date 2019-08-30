package com.abaz.twitterish.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abaz.twitterish.R
import com.abaz.twitterish.screen.BaseTechTalkFragment
import com.abaz.twitterish.screen.HomeFeedMvRxActivity
import com.abaz.twitterish.utils.TextChangedListener
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : BaseTechTalkFragment() {

    private val viewModel: LoginMvRxViewModel by activityViewModel()

    override fun onCreateView(inflater: LayoutInflater, c1: ViewGroup?, s1: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, c1, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        email_edit_text.addTextChangedListener(TextChangedListener(viewModel::emailChanged))
        password_edit_text.addTextChangedListener(TextChangedListener(viewModel::passwordChanged))
        login_button.setOnClickListener { viewModel.login() }
        sign_up_button.setOnClickListener { goToSignUpFragment() }
    }

    private fun goToSignUpFragment() {
        (activity as? HomeFeedMvRxActivity)?.showFragment(SignUpFragment(), "SignUpFragment")
    }

    private fun goToFeedFragment(){
        (activity as? HomeFeedMvRxActivity)?.goToFeedFragment()
    }

    override fun onBackPressed(): Boolean {
        return true
    }

    override fun invalidate() = withState(viewModel) { state ->
        email_edit_text.apply {
            if (text.toString() != state.email) setText(state.email)
        }
        password_edit_text.apply {
            if (text.toString() != state.password) setText(state.password)
        }

        if(state.isLoggedIn){
            goToFeedFragment()
        }

        return@withState
    }

}