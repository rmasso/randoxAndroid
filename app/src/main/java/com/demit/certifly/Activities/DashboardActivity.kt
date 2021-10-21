package com.demit.certifly.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.demit.certifly.Fragments.*
import com.demit.certifly.Interfaces.DashboardInterface
import com.demit.certifly.R
import com.demit.certifly.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() ,DashboardInterface{
    lateinit var binding : ActivityDashboardBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changefragment(HomeFragment(this))
        bnavclick()
        click()
    }

    fun bnavclick(){
        binding.bottomnav.setOnItemSelectedListener {
            if(it.itemId == R.id.home){
                changefragment(HomeFragment(this))
            }else if(it.itemId == R.id.profile){
                changefragment(ProfileFragment())
            }else if(it.itemId == R.id.certi){
                changefragment(CertificatesFragment())
            }else if(it.itemId == R.id.register){
                changefragment(YtestFragment(this))
            }else if(it.itemId == R.id.faq){
                changefragment(FaqFragment())
            }
            true
        }
        binding.bottomnav.setOnItemReselectedListener {

        }
    }

    fun click(){
        binding.scanb.setOnClickListener {
            setp(2)
        }
    }
    fun changefragment(fragment : Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.fragcontainer,fragment).commit()
    }

    override fun setpage(page: Int) {
        setp(page)
    }
    fun setp(page: Int){
        val ids : MutableList<Int> = ArrayList();
        ids.add(R.id.home)
        ids.add(R.id.certi)
        ids.add(R.id.register)
        ids.add(R.id.faq)
        ids.add(R.id.profile)

        binding.bottomnav.selectedItemId = ids[page]
    }
}