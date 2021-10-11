package com.demit.certify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demit.certify.Interfaces.FaqScrollInterface
import com.demit.certify.Models.FaqAsk
import com.demit.certify.R
import kotlinx.android.synthetic.main.view_faq.view.*

import kotlinx.android.synthetic.main.view_faq_collapse.view.*
import kotlinx.android.synthetic.main.view_faq_expand.view.*



class FaqAdapter(private val faqAskList: List<FaqAsk>, private val scrollListener:FaqScrollInterface) :
    RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {


    inner class FaqViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(question: String, answer: String,position: Int) {
            with(view) {
                _question.text = question
                _answer.text = answer
                collapse.setOnClickListener {
                    if(expand.visibility==View.GONE) {
                        expand.visibility = View.VISIBLE
                        if(position==faqAskList.size-1){
                            scrollListener.scrollTo(position+1)
                        }
                    }
                    else
                        expand.visibility=View.GONE
                }
            }

        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_faq, parent, false)
        return FaqViewHolder(view)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.bindData(faqAskList[position].question, faqAskList[position].answer,position)
    }

    override fun getItemCount(): Int = faqAskList.size
}