package com.demit.certify.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.demit.certify.Activities.CertificatePreviewActivity
import com.demit.certify.Activities.DashboardActivity
import com.demit.certify.Extras.CertificateGenerator
import com.demit.certify.Interfaces.DialogDismissInterface
import com.demit.certify.Models.AllCertificatesModel
import com.demit.certify.R
import com.mutualmobile.cardstack.CardStackAdapter
import kotlinx.android.synthetic.main.certificate_row.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CertificateAdapter(
    private val context: Context,
    private val certificateList: List<AllCertificatesModel>,
    private val onDismiss: DialogDismissInterface
) : CardStackAdapter(context) {
    @SuppressLint("SetTextI18n")
    override fun createView(position: Int, container: ViewGroup?): View {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.certificate_row, container, false)
        (context as DashboardActivity).lifecycleScope.launch(Dispatchers.IO) {
            val pdfByteArray = async {
                CertificateGenerator.generateCompatCertificate(
                    certificateList[position]
                )
            }
            val pdfBytes = pdfByteArray.await()
            context.runOnUiThread {
                pdfBytes?.let {
                    with(itemView) {
                        pdfView.maxZoom = pdfView.zoom
                        pdfView.fromBytes(it.toByteArray()).load()
                        certificate_header.text = "Certificate: ${position + 1}"
                    }
                }

                if (position == certificateList.size - 1)
                    onDismiss.dismissDialog()

            }
        }
        itemView.view_details.setOnClickListener {
            with(context) {
                val previewIntent = Intent(this, CertificatePreviewActivity::class.java)
                previewIntent.putExtra("certificate",certificateList[position])
                startActivity(previewIntent)
            }

        }
        return itemView
    }

    override fun getCount(): Int = certificateList.size


}