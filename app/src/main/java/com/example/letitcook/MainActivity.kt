package com.example.letitcook
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.letitcook.databinding.ActivityMainBinding
import com.example.letitcook.R
import com.example.letitcook.data.AuthRepository
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val authRepository = AuthRepository(this)

//        Inflate the graph manually so we can edit the start destination (if the user is already logged in)
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        if (authRepository.isUserLoggedIn()) {
            navGraph.setStartDestination(R.id.homeFragment)
        } else {
            navGraph.setStartDestination(R.id.loginFragment)
        }

        navController.graph = navGraph

        binding.bottomNavigationView.setupWithNavController(navController)

        binding.fabAdd.setOnClickListener {
            navController.navigate(R.id.addReviewFragment)
        }

        //disable navbar in these pages
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment,
                R.id.registerFragment,
                R.id.addReviewFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.fabAdd.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.fabAdd.visibility = View.VISIBLE
                }
            }
        }
    }
}