package com.example.proyectodsm

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.proyectodsm.login.LoginMaestro
import com.example.proyectodsm.login.LoginPrincipal
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var toogle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth

    lateinit var userTextView: TextView

    lateinit var spCantidadPreguntas: Spinner
    lateinit var spCantidadTiempo: Spinner

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawerLayout)
        auth = FirebaseAuth.getInstance()
        val navView: NavigationView = findViewById(R.id.nav_view)

        val headerView = navView.inflateHeaderView(R.layout.nav_header)
        userTextView = headerView.findViewById(R.id.tvuser_name)
        userTextView.text = auth.currentUser!!.email.toString()
        toogle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setCheckedItem(R.id.nav_home)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, HomeFragment())
            .commit()
        navView.setCheckedItem(R.id.nav_home)
        navView.setNavigationItemSelectedListener {

            it.isChecked = true

            when (it.itemId) {
                R.id.nav_home -> replaceFregment(HomeFragment(), it.title.toString())
                R.id.nav_create_exam -> {
                    val dialog = Dialog(this)
                    dialog.setContentView(R.layout.dialog_input)

                    spCantidadPreguntas = dialog.findViewById(R.id.spCantidadPreguntas)
                    spCantidadTiempo = dialog.findViewById(R.id.spTiempo)

                    dialog.findViewById<Button>(R.id.buttonAceptar).setOnClickListener {
                        dialog.dismiss()

                        val fragment = CreateExamFragment(
                            spCantidadPreguntas.selectedItem.toString().toInt(),
                            spCantidadTiempo.selectedItem.toString().toInt()
                        )
                        replaceFregment(fragment, "Titulo")
                    }

                    dialog.show()
                }
                R.id.nav_logOut -> {
                    auth.signOut().also {
                        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginPrincipal::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            true
        }
    }

    private fun replaceFregment(fragment: Fragment, title: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toogle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Estás seguro que quieres salir?")
            .setCancelable(false)
            .setPositiveButton("Salir") { _, _ -> finish() }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
        val alert = builder.create()
        alert.show()

    }
}