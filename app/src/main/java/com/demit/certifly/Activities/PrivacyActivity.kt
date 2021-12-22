package com.demit.certifly.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import com.demit.certifly.Extras.Shared
import com.demit.certifly.R
import com.demit.certifly.databinding.ActivityPrivacyBinding
import kotlinx.android.synthetic.main.view_data_content.view.*
import kotlinx.android.synthetic.main.view_data_heading.view.*

class PrivacyActivity : AppCompatActivity() {
    lateinit var binding: ActivityPrivacyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPrivacyData()
        attachClicks()
    }

    override fun onBackPressed() {
        return
    }

    private fun initPrivacyData() {
        binding.descriptionContent.movementMethod= LinkMovementMethod()
        with(binding) {
            dataSecurityExpandable.parentLayout.data_heading.text =
                getString(R.string.data_security_heading)
            dataSecurityExpandable.secondLayout.data_content.text =
                getText(R.string.data_security_content)

            dataCollectionExpandable.parentLayout.data_heading.text =
                getString(R.string.data_collection_heading)
            dataCollectionExpandable.secondLayout.data_content.text =
                getText(R.string.data_collection_content)

            useDataExpandable.parentLayout.data_heading.text =
                getString(R.string.use_of_data_heading)
            useDataExpandable.secondLayout.data_content.text =
                getText(R.string.use_of_data_content)
            useDataExpandable.secondLayout.data_content.movementMethod = LinkMovementMethod()
            dataDisclosureExpandable.parentLayout.data_heading.text =
                getString(R.string.disclosure_0f_data_heading)
            dataDisclosureExpandable.secondLayout.data_content.text =
                getText(R.string.disclosure_0f_data_content)

            cookieExpandable.parentLayout.data_heading.text =
                getString(R.string.cookie_policy_heading)
            cookieExpandable.secondLayout.data_content.text =
                getText(R.string.cookie_policy_content)

            childrenPrivacyExpandable.parentLayout.data_heading.text =
                getString(R.string.children_privacy_heading)
            childrenPrivacyExpandable.secondLayout.data_content.text =
                getText(R.string.children_privacy_content)

            euUserRightsExpandable.parentLayout.data_heading.text =
                getString(R.string.eu_rights_heading)
            euUserRightsExpandable.secondLayout.data_content.text =
                getText(R.string.eu_rights_content)

            personalInfoExpandable.parentLayout.data_heading.text =
                getString(R.string.personal_info_heading)
            personalInfoExpandable.secondLayout.data_content.text =
                getText(R.string.personal_info_content)
        }
    }

    private fun attachClicks() {
        with(binding) {

            //Parent layout click case
            dataSecurityExpandable.parentLayout.setOnClickListener {
                if (!dataSecurityExpandable.isExpanded)
                    dataSecurityExpandable.expand()
                else
                    dataSecurityExpandable.collapse()
            }
            dataCollectionExpandable.parentLayout.setOnClickListener {
                if (!dataCollectionExpandable.isExpanded)
                    dataCollectionExpandable.expand()
                else
                    dataCollectionExpandable.collapse()

            }

            useDataExpandable.parentLayout.setOnClickListener {
                if (!useDataExpandable.isExpanded)
                    useDataExpandable.expand()
                else
                    useDataExpandable.collapse()

            }

            dataDisclosureExpandable.parentLayout.setOnClickListener {
                if (!dataDisclosureExpandable.isExpanded)
                    dataDisclosureExpandable.expand()
                else
                    dataDisclosureExpandable.collapse()

            }

            cookieExpandable.parentLayout.setOnClickListener {
                if (!cookieExpandable.isExpanded)
                    cookieExpandable.expand()
                else
                    cookieExpandable.collapse()

            }
            childrenPrivacyExpandable.parentLayout.setOnClickListener {
                if (!childrenPrivacyExpandable.isExpanded)
                    childrenPrivacyExpandable.expand()
                else
                    childrenPrivacyExpandable.collapse()

            }

            euUserRightsExpandable.parentLayout.setOnClickListener {
                if (!euUserRightsExpandable.isExpanded)
                    euUserRightsExpandable.expand()
                else
                    euUserRightsExpandable.collapse()

            }
            personalInfoExpandable.parentLayout.setOnClickListener {
                if (!personalInfoExpandable.isExpanded)
                    personalInfoExpandable.expand()
                else
                    personalInfoExpandable.collapse()

            }

            //Second layout click case
            dataSecurityExpandable.secondLayout.setOnClickListener {
                dataSecurityExpandable.collapse()
            }
            dataCollectionExpandable.secondLayout.setOnClickListener {

                dataCollectionExpandable.collapse()

            }

            useDataExpandable.secondLayout.setOnClickListener {

                useDataExpandable.collapse()

            }

            dataDisclosureExpandable.secondLayout.setOnClickListener {

                dataDisclosureExpandable.collapse()

            }

            cookieExpandable.secondLayout.setOnClickListener {

                cookieExpandable.collapse()

            }
            childrenPrivacyExpandable.secondLayout.setOnClickListener {

                childrenPrivacyExpandable.collapse()

            }

            euUserRightsExpandable.secondLayout.setOnClickListener {

                euUserRightsExpandable.collapse()

            }
            personalInfoExpandable.secondLayout.setOnClickListener {
                personalInfoExpandable.collapse()
            }

            agreeBtn.setOnClickListener {
                Shared(this@PrivacyActivity).setString("is_first_time", "yes")
                startActivity(Intent(this@PrivacyActivity, LoginActivity::class.java))
                finish()
            }

        }
    }
}