package com.example.geolocalizacion

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.geolocalizacion.pantallasMenu.FotoFragment
import com.example.geolocalizacion.pantallasMenu.MandarFotosFragment
import com.example.geolocalizacion.pantallasMenu.MandarUbicacionesFragment
import com.example.geolocalizacion.pantallasMenu.UbicacionesFragment
import com.google.android.material.navigation.NavigationView


class Base_Menu : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var tokenStr:String
    private var IdUsuario:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        tokenStr = intent.getStringExtra("token").toString()
        val strIdUsuario = intent.getStringExtra("idUsuario")
        if(!strIdUsuario.isNullOrBlank()) IdUsuario = strIdUsuario.toString().toInt()
        setContentView(R.layout.activity_base_menu)
        supportActionBar!!.title = "Carga de Fotografías"
        val drawerLayout:DrawerLayout = findViewById(R.id.drawerLayout)
        toggle =ActionBarDrawerToggle(this,drawerLayout,R.string.open_drawer,R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navView: NavigationView = findViewById(R.id.nav_view)

        // Si entró con la opción de boton sin datos se va a ocultar la opción de subir datos a servidor
        val habilitarOpciones = intent.getStringExtra("hayConexion").toString()
        if(habilitarOpciones == "N"){
            navView.menu.findItem(R.id.nav_2).setVisible(false)
            navView.menu.findItem(R.id.nav_4).setVisible(false)
        }

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_1 -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContaiderView, FotoFragment())
                        commit()
                        supportActionBar!!.title = "Carga de Fotografías"
                    }
                }
                R.id.nav_2 -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContaiderView, MandarFotosFragment(tokenStr,IdUsuario))
                        commit()
                        supportActionBar!!.title = "Envio de Fotografías"
                    }
                }
                R.id.nav_3 -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContaiderView, UbicacionesFragment())
                        commit()
                        supportActionBar!!.title = "Carga de Ubicaciones"

                    }
                }
                R.id.nav_4 -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContaiderView, MandarUbicacionesFragment(tokenStr,IdUsuario))
                        commit()
                        supportActionBar!!.title = "Carga de Ubicaciones"

                    }
                }
                R.id.nav_salir -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

    }
}