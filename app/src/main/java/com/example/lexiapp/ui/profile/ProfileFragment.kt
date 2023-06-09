package com.example.lexiapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lexiapp.ui.profile.edit.EditProfileActivity
import com.example.lexiapp.ui.profile.link.LinkPatientActivity
import com.example.lexiapp.domain.model.User
import androidx.fragment.app.viewModels
import com.example.lexiapp.databinding.FragmentProfileBinding
import com.example.lexiapp.ui.login.LoginActivity
import com.example.lexiapp.ui.profesionalhome.ProfesionalHomeActivity
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var logout: MaterialButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViews()
        setListeners()
        setObserver()
    }

    private fun setObserver() {
        viewModel.profileLiveData.observe(viewLifecycleOwner) { user ->
                setProfile(user!!)
        }
    }

    private fun setProfile(user: User) {
        binding.txtEmail.text = user!!.email
        Log.v("USER_NAME_FIRESTORE_FRAGMENT", "${user.userName}")
        user.userName.let { binding.txtName.text = it }
        user.birthDate.let { binding.txtBirthDate.text = it }
    }

    private fun getViews() {
        logout = binding.btnLogOut
    }

    private fun setListeners() {
        btnLogoutClick()
        btnEditProfile()
        btnVinculate()
        //once the linking functionality is implemented this listener must be removed
        binding.ivMiniLogo.setOnClickListener { startActivity(Intent(activity, ProfesionalHomeActivity::class.java)) }
    }

    private fun btnEditProfile() {
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(activity, EditProfileActivity::class.java))
        }
    }

    private fun btnVinculate() {
        binding.btnLinkAccount.setOnClickListener {
            val validation=true //Should be myQR!=null
            if (validation){
                startActivity(Intent(activity, LinkPatientActivity::class.java))
            }
        }
    }

    private fun btnLogoutClick() {
        logout.setOnClickListener {
            viewModel.closeSesion()
            requireActivity().finish()
            startActivity(Intent(activity, LoginActivity::class.java))
        }
    }
}
