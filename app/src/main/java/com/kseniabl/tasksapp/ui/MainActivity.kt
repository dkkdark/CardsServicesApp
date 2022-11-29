package com.kseniabl.tasksapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.databinding.ActivityMainBinding
import com.kseniabl.tasksapp.utils.HelperFunctions
import com.kseniabl.tasksapp.utils.Resource
import com.kseniabl.tasksapp.viewmodels.RegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    private val viewModel: RegistrationViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, true)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.tokenFlowData.collect {
                        if (it is Resource.Success<*>) {
                            Log.e("qqq", "${it.data}")
                            val isUserLogin = it.data?.isEmpty() == false
                            val navController = getNavController()
                            setupStartDestination(navController, isUserLogin)
                            onNavControllerActivated(navController)
                        }
                    }
                }
            }
        }

    }

    private fun getNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment
        return navHostFragment.navController
    }

    private fun setupStartDestination(navController: NavController, isUserLogin: Boolean) {
        val graph = navController.navInflater.inflate(R.navigation.nav_graph_main)
        graph.setStartDestination(
            if (isUserLogin)
                R.id.tabsFragment
            else
                R.id.loginFragment
        )
        navController.graph = graph
    }

    private fun onNavControllerActivated(navController: NavController) {
        if (this.navController == navController) return
        this.navController?.removeOnDestinationChangedListener(destinationListener)
        navController.addOnDestinationChangedListener(destinationListener)
        this.navController = navController
    }

    private val destinationListener = NavController.OnDestinationChangedListener { _, destination, arguments ->
        if (destination.id == R.id.loginFragment || destination.id == R.id.registrationFragment) {
            binding.mainConstraintLayout.setBackgroundResource(R.drawable.app_gradient)
            binding.additionalSpace.visibility = View.GONE
        }
        else {
            binding.mainConstraintLayout.setBackgroundColor(getColor(R.color.white))
            binding.additionalSpace.visibility = View.VISIBLE
        }
        binding.toolbar.title = HelperFunctions.setTitle(destination.label, arguments)
    }

    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
        navController = null
        super.onDestroy()
    }

    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            if (f is TabsFragment || f is NavHostFragment) return
            onNavControllerActivated(f.findNavController())
        }
    }
}