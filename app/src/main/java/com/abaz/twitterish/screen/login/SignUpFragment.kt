package com.abaz.twitterish.screen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abaz.twitterish.R
import com.abaz.twitterish.screen.BaseTechTalkFragment
import com.abaz.twitterish.screen.HomeFeedMvRxActivity
import com.abaz.twitterish.utils.extensions.onTextChange
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import kotlinx.android.synthetic.main.fragment_login.email_edit_text
import kotlinx.android.synthetic.main.fragment_login.login_button
import kotlinx.android.synthetic.main.fragment_login.password_edit_text
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : BaseTechTalkFragment() {

    private val viewModel: LoginMvRxViewModel by activityViewModel()

    override fun onCreateView(inflater: LayoutInflater, c1: ViewGroup?, s1: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_up, c1, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        email_edit_text.onTextChange(viewModel::emailChanged)
        password_edit_text.onTextChange(viewModel::passwordChanged)
        password_confirmation_edit_text.onTextChange(viewModel::passwordConfirmationChanged)
        login_button.setOnClickListener { viewModel.signUp() }
    }

    private fun goToFeedFragment(){
        (activity as? HomeFeedMvRxActivity)?.goToFeedFragment()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun invalidate() = withState(viewModel) { state ->
        email_edit_text.apply {
            if (text.toString() != state.email) setText(state.email)
        }
        password_edit_text.apply {
            if (text.toString() != state.password) setText(state.password)
        }
        password_confirmation_edit_text.apply {
            if (text.toString() != state.passwordConfirmation) setText(state.password)
        }

        if(state.isLoggedIn){
            goToFeedFragment()
        }

        return@withState
    }
}