package com.demit.certify.Fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demit.certify.ProfileModel
import com.demit.certify.R
import com.demit.certify.databinding.FragmentProfileBinding
import com.demit.certify.databinding.ViewProfileBinding


class ProfileFragment : Fragment() {
    lateinit var binding : FragmentProfileBinding
    lateinit var adapter : ProfileAdapter
    var index = 0
    val list : MutableList<ProfileModel> = ArrayList();
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        val p = ProfileModel();
        p.selected = true;
        list.add(p)
        clicks()
        fielddata()
        adapter = ProfileAdapter()
        binding.rv.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        binding.rv.adapter = adapter;
        return binding.root
    }

    fun clicks(){
        binding.add.setOnClickListener(){
            val p = ProfileModel();
            list.add(p)
            adapter.notifyDataSetChanged()
        }
    }
    fun fielddata(){
        binding.fname.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                list[index].fname = s.toString();
            }

        })

        binding.sname.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                list[index].sname = s.toString();
            }

        })

        binding.dob.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                list[index].dob = s.toString();
            }

        })

        binding.pnumber.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                list[index].pnumber = s.toString();
            }

        })

        binding.email.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                list[index].email = s.toString();
            }

        })


        binding.phone.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                list[index].phone = s.toString();
            }

        })

        binding.address.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                list[index].address = s.toString();
            }

        })

        binding.zip.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable?) {
                list[index].zipcode = s.toString();
            }
        })

        list[index].gender = binding.gender.selectedItemPosition

        binding.gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                list[index].gender = position
            }

        }

        list[index].country = binding.country.selectedItemPosition

        binding.country.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                list[index].country = position
            }

        }
    }
    fun setData(position : Int){
         for(i in 0 until list.size){
             list[i].selected = false
         }
        index = position
        list[position].selected = true
        adapter.notifyDataSetChanged()

        binding.fname.setText(list[position].fname)
        binding.sname.setText(list[position].sname)
        binding.dob.setText(list[position].dob)
        binding.email.setText(list[position].email)
        binding.pnumber.setText(list[position].pnumber)
        binding.phone.setText(list[position].phone)
        binding.address.setText(list[position].address)
        binding.zip.setText(list[position].zipcode)

        binding.country.setSelection(list[position].country)
        binding.gender.setSelection(list[position].gender)
    }
    inner class ProfileAdapter : RecyclerView.Adapter<ProfileAdapter.ProfileVH>() {
        lateinit var context : Context
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileVH {
            val inflater = LayoutInflater.from(parent.context)
            val binding : ViewProfileBinding = ViewProfileBinding.inflate(inflater,parent,false)
            return  ProfileVH(binding);
        }

        override fun onBindViewHolder(holder: ProfileVH, position: Int) {
            val model = this@ProfileFragment.list[position];
            holder.itemView.setOnClickListener{
                this@ProfileFragment.setData(position)
            }
            if(model.selected){
                holder.binding.verified.visibility = View.VISIBLE
            }else{
                holder.binding.verified.visibility = View.GONE
            }
        }

        override fun getItemCount(): Int {
            return this@ProfileFragment.list.size
        }

        inner class ProfileVH(val binding: ViewProfileBinding) : RecyclerView.ViewHolder(binding.root){

        }
    }
}