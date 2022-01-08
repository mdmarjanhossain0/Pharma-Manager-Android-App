package com.devscore.digital_pharmacy.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.devscore.digital_pharmacy.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerBtn :Button = findViewById(R.id.registerBtnId)

        registerBtn.setOnClickListener(){
            val intent = Intent(this, VerifyActivity::class.java)
            startActivity(intent)
        }
    }
}