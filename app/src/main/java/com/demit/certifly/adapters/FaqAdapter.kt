package com.demit.certifly.adapters

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demit.certifly.Interfaces.FaqScrollInterface
import com.demit.certifly.Models.FaqAsk
import com.demit.certifly.R
import kotlinx.android.synthetic.main.view_faq.view.*

import kotlinx.android.synthetic.main.view_faq_collapse.view.*
import kotlinx.android.synthetic.main.view_faq_expand.view.*


class FaqAdapter(
    private val faqAskList: List<FaqAsk>,
    private val scrollListener: FaqScrollInterface
) :
    RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

    val sparseBooleanArray = SparseBooleanArray()

    inner class FaqViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(question: String, answer: String, position: Int) {
            with(view) {
                _question.text = question
                _answer.text = answer
                collapse.setOnClickListener {
                    if (expand.visibility == View.GONE) {
                        expand.visibility = View.VISIBLE
                        sparseBooleanArray.put(position,true)
                        if (position == faqAskList.size - 1) {
                            scrollListener.scrollTo(position + 1)
                        }
                    } else {
                        expand.visibility = View.GONE
                        sparseBooleanArray.put(position,false)
                    }
                }
                if(!sparseBooleanArray[position] && expand.visibility==View.VISIBLE){
                    expand.visibility = View.GONE
                }else if(sparseBooleanArray[position] && expand.visibility==View.GONE){
                    expand.visibility = View.VISIBLE
                }


            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_faq, parent, false)
        return FaqViewHolder(view)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.bindData(faqAskList[position].question, faqAskList[position].answer, position)

    }

    override fun getItemCount(): Int = faqAskList.size
}