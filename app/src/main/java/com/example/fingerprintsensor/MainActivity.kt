package com.example.fingerprintsensor

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

//private val Any?.isKeyguardSecure: Any
   // get() {}

class MainActivity : AppCompatActivity() {
    private  var cancellationSignal: CancellationSignal?=null
    private  val authenticationCallback:BiometricPrompt.AuthenticationCallback
    get() =
        @RequiresApi(Build.VERSION_CODES.P)
        object : BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                notifyuser("Authentication error: $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                notifyuser("Authentication success!" )
                startActivity(Intent(this@MainActivity,secactivity::class.java))
            }
        }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkBiometricSupport()
        authenticate.setOnClickListener{
            val biometricPrompt: BiometricPrompt =BiometricPrompt.Builder(this)
                .setTitle("Place finger")
                .setSubtitle("Authentication is required")
                .setDescription("This unlocks the protection")
                .setNegativeButton("Cancel",this.mainExecutor, DialogInterface.OnClickListener {dialog, which->
                    notifyuser("Authentication cancelled")
                }).build()
            biometricPrompt.authenticate(getcancellationSignal(),mainExecutor,authenticationCallback)
        }


    }
    private fun notifyuser(message:String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }
    private fun getcancellationSignal():CancellationSignal{
        cancellationSignal=CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyuser("Authenitication was cancelled by the user")
        }

        return  cancellationSignal as CancellationSignal
    }

    private  fun checkBiometricSupport():Boolean {
        val keyguardManager:KeyguardManager =getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if(!keyguardManager.isKeyguardSecure){
            notifyuser("Fingerprint authentication has not been enabled")
            return false
        }
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.USE_BIOMETRIC)!= PackageManager.PERMISSION_GRANTED){
            notifyuser("Fingerprint authentication is not enabled")
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            true
        }else true

    }
}