package com.example.smartcontact.Kotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartcontact.R


class ReadRecyclerAdapter(val context: Context, private val ProfilList:ArrayList<Profil>)
    :RecyclerView.Adapter<ReadRecyclerAdapter.ReadViewHolder>() {

    class ReadViewHolder(view: View):RecyclerView.ViewHolder(view){
        val profilid:TextView = view.findViewById(R.id.txtProfilId)
        val fullName:TextView=view.findViewById(R.id.txtfullName)
        val competence:TextView=view.findViewById(R.id.txtCompetence)
        val company:TextView=view.findViewById(R.id.txtCompany)
        val positionWork:TextView=view.findViewById(R.id.txtPositionWork)
        val experience: TextView = view.findViewById(R.id.txtExperience)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.single_profil_item,parent,false)
        return ReadViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReadViewHolder, position: Int) {
        holder.profilid.text = ProfilList[position].profilid
        holder.fullName.text=ProfilList[position].fullName
        holder.competence.text=ProfilList[position].competence
        holder.company.text=ProfilList[position].company
        holder.positionWork.text=ProfilList[position].positionWork
        holder.experience.text=ProfilList[position].experience

    }

    override fun getItemCount(): Int {
        return ProfilList.size
    }
}