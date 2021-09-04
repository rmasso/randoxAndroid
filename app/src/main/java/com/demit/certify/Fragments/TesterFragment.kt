package com.demit.certify.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demit.certify.Models.TesterModel
import com.demit.certify.R
import com.demit.certify.databinding.FragmentTesterBinding
import com.demit.certify.databinding.ViewTesterBinding

class TesterFragment : Fragment() {
    var list : MutableList<TesterModel> = ArrayList();
    lateinit var binding : FragmentTesterBinding
    lateinit var adapter : TesterAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTesterBinding.inflate(layoutInflater)
        addData()
        binding.tlist.layoutManager = LinearLayoutManager(context)
        adapter = TesterAdapter()
        binding.tlist.adapter = adapter
        setclicks()
        return binding.root
    }

    fun setclicks(){
        binding.gotop.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragcontainer,ScancompleteFragment())?.addToBackStack("")?.commit()
        }

    }

    fun addData(){
        list.add(TesterModel("Tony"))
        list.add(TesterModel("Steve"))
        list.add(TesterModel("Natasha"))
        list.add(TesterModel("Bruce"))
        list.add(TesterModel("Clint"))
        list.add(TesterModel("Sam"))
        list.add(TesterModel("Frank"))
        list.add(TesterModel("Peter"))
    }

    fun setData(positon : Int){
        for(i in 0 until list.size){
            list[i].checked = false
        }
        list[positon].checked = true
        adapter.notifyDataSetChanged()
    }

    inner class TesterAdapter : RecyclerView.Adapter<TesterAdapter.TesterVH>() {
        lateinit var context : Context
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TesterVH {
            val inflater = LayoutInflater.from(parent.context)
            val binding : ViewTesterBinding = ViewTesterBinding.inflate(inflater,parent,false)
            return  TesterVH(binding);
        }

        override fun onBindViewHolder(holder: TesterVH, position: Int) {
            val model = this@TesterFragment.list[position];
            holder.itemView.setOnClickListener{
                if(!model.checked) {
                    this@TesterFragment.setData(position)
                }
            }
            holder.binding.rd.setOnClickListener {
                if(!model.checked) {
                    this@TesterFragment.setData(position)
                }
            }
            holder.binding.tname.text = model.name

            holder.binding.rd.isChecked = model.checked
        }

        override fun getItemCount(): Int {
            return this@TesterFragment.list.size
        }

        inner class TesterVH(val binding: ViewTesterBinding) : RecyclerView.ViewHolder(binding.root)
    }
}