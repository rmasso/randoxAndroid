package com.demit.certify.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demit.certify.Models.ProfileModel
import com.demit.certify.databinding.FragmentProfileBinding
import com.demit.certify.databinding.ViewProfileBinding

//import com.microblink.entities.recognizers.Recognizer
//import com.microblink.entities.recognizers.RecognizerBundle
//import com.microblink.entities.recognizers.blinkid.generic.BlinkIdCombinedRecognizer
//import com.microblink.uisettings.ActivityRunner
//import com.microblink.uisettings.BlinkIdUISettings


class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    lateinit var adapter: ProfileAdapter
    var index = 0
    val list: MutableList<ProfileModel> = ArrayList();

    //Blink Id Variables
//    private lateinit var mRecognizer: BlinkIdCombinedRecognizer
//    private lateinit var mRecognizerBundle: RecognizerBundle
    private val MY_REQUEST_CODE = 801
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
        binding.rv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rv.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mRecognizer = BlinkIdCombinedRecognizer()
//        mRecognizer.setReturnFaceImage(true)
        // bundle recognizers into RecognizerBundle
//        mRecognizerBundle = RecognizerBundle(mRecognizer)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode === MY_REQUEST_CODE) {
//            if (resultCode === RESULT_OK && data != null) {
//                // load the data into all recognizers bundled within your RecognizerBundle
////                mRecognizerBundle.loadFromIntent(data)
//
//                // now every recognizer object that was bundled within RecognizerBundle
//                // has been updated with results obtained during scanning session
//                // you can get the result by invoking getResult on recognizer
////                val result = mRecognizer.result
//                // result is valid, you can use it however you wish
//                if (result.resultState == Recognizer.Result.State.Valid) {
//                    binding.fname.text= Editable.Factory.getInstance().newEditable( result.firstName)
//                    binding.sname.text= Editable.Factory.getInstance().newEditable( result.lastName)
//
//                    result.dateOfBirth.date?.let {date->
//                        val day= date.day
//                        val year= date.year
//                        val month= date.month
//                        binding.dob.text= Editable.Factory.getInstance().newEditable("$day/$month/$year")
//                    }
//
//
//                    binding.pnumber.text= Editable.Factory.getInstance().newEditable( result.documentNumber)
//
//
//
//
//                    /* val imageArr = result.encodedFaceImage
//                     faceImage.setImageBitmap(
//                         result.faceImage?.convertToBitmap()
//                     )*/
//
//               /*     val p = ProfileModel();
//                list.add(p)
//                binding.rv.getAdapter()?.itemCount?.minus(1)?.let { it1 ->
//                    binding.rv.smoothScrollToPosition(
//                        it1
//                    )
//                };
//                setData(list.size - 1)*/
//
//                }
//            }
//        }
//
//    }

    fun clicks() {
        binding.add.setOnClickListener() {
//            startScanning()
            val p = ProfileModel();
            list.add(p)
            binding.rv.getAdapter()?.itemCount?.minus(1)?.let { it1 ->
                binding.rv.smoothScrollToPosition(
                    it1
                )
            };
            setData(list.size - 1)
        }
    }

    fun fielddata() {
        binding.fname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].fname = s.toString();
            }

        })

        binding.sname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].sname = s.toString();
            }

        })

        binding.dob.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].dob = s.toString();
            }

        })

        binding.pnumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].pnumber = s.toString();
            }

        })

        binding.email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].email = s.toString();
            }

        })


        binding.phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].phone = s.toString();
            }

        })

        binding.address.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                list[index].address = s.toString();
            }

        })

        binding.zip.addTextChangedListener(object : TextWatcher {
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

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                list[index].gender = position
            }

        }

        list[index].country = binding.country.selectedCountryNameCode

        binding.country.setOnCountryChangeListener {
            list[index].country = binding.country.selectedCountryNameCode
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(position: Int) {
//         for(i in 0 until list.size){
        list[index].selected = false
//         }
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
        binding.country.setCountryForNameCode(list[position].country);
        binding.gender.setSelection(list[position].gender)
    }

    inner class ProfileAdapter : RecyclerView.Adapter<ProfileAdapter.ProfileVH>() {
        lateinit var context: Context
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileVH {
            val inflater = LayoutInflater.from(parent.context)
            val binding: ViewProfileBinding = ViewProfileBinding.inflate(inflater, parent, false)
            return ProfileVH(binding);
        }

        override fun onBindViewHolder(holder: ProfileVH, position: Int) {
            val model = this@ProfileFragment.list[position];
            holder.itemView.setOnClickListener {
                this@ProfileFragment.setData(position)
            }
            if (model.selected) {
                holder.binding.verified.visibility = View.VISIBLE
            } else {
                holder.binding.verified.visibility = View.GONE
            }
        }

        override fun getItemCount(): Int {
            return this@ProfileFragment.list.size
        }

        inner class ProfileVH(val binding: ViewProfileBinding) :
            RecyclerView.ViewHolder(binding.root) {

        }
    }

    private fun startScanning() {
        // Settings for BlinkIdActivity
//        val settings = BlinkIdUISettings(mRecognizerBundle)
//
//        // tweak settings as you wish
//
//        // Start activity
//        ActivityRunner.startActivityForResult(this, MY_REQUEST_CODE, settings)
    }
}