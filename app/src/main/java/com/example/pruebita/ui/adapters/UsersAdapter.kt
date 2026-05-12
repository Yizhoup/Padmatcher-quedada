package com.example.pruebita.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebita.R
import com.example.pruebita.models.User

class UsersAdapter(
    private val users: List<User>
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    class UserViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val user = users[position]

        val tvName = holder.view.findViewById<TextView>(R.id.tvName)
        val tvStatus = holder.view.findViewById<TextView>(R.id.tvStatus)

        tvName.text = user.nombre

        tvStatus.text = if (user.online) "🟢 Online" else "⚪ Offline"
    }
}