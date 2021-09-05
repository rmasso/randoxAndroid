package com.demit.certify

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.demit.certify.R
import java.util.ArrayList

class Test(context: Context, resource: Int) : ArrayAdapter<Any?>(context, resource) {
    var l: MutableList<Int> = ArrayList()
    override fun getCount(): Int {
        return l.size
    }

    override fun getItem(position: Int): Any? {
        return l[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_certificates, parent, false)
        }
        val n = l[position].toString()
        convertView!!.setOnClickListener { v: View? -> Log.d("aaa", n) }
        return convertView
    }

    init {
        l.add(1)
        l.add(1)
        l.add(1)
        l.add(1)
        l.add(1)
    }
}