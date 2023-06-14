package com.example.lexiapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lexiapp.databinding.ItemPatientBinding
import com.example.lexiapp.domain.model.User

class UserAdapter(
    private val patientList: List<User>,
    private val onClickPatient: (User) -> Unit,
    private val onClickDelete: (String) -> Unit
): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val patientBinding =
            ItemPatientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(patientBinding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val patient = patientList[position]
        holder.bind(patient)
        holder.setListener (patient)
    }

    override fun getItemCount(): Int {
        return patientList.size
    }

    inner class UserViewHolder(val binding: ItemPatientBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(patient: User) {
            binding.txtName.text=patient.userName
            binding.txtEmail.text=patient.email
        }
        fun setListener(patient: User) {
            binding.btnTrash.setOnClickListener{
                onClickDelete(patient.email)
            }
            binding.clDetailPatient.setOnClickListener{
                onClickPatient(patient)
            }
        }
    }
}

