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
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.kseniabl.tasksapp.R
import com.kseniabl.tasksapp.databinding.ActivityMainBinding
import com.kseniabl.tasksapp.utils.HelperFunctions
import com.kseniabl.tasksapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, true)

        val navController = getNavController()

        setupStartDestination(navController)
        onNavControllerActivated(navController)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    /*viewModel.tokenFlowData.collect {
                        if (it == null) return@collect
                        else if (it.isEmpty()) {
                            navController.popBackStack(R.id.loadingScreen, true)
                            navController.navigate(R.id.loginFragment)
                        }
                        else {
                            navController.popBackStack(R.id.loadingScreen, true)
                            navController.navigate(R.id.tabsFragment)
                        }
                    }*/
                }
            }
        }

    }

    private fun getNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainFragmentContainer) as NavHostFragment
        return navHostFragment.navController
    }

    private fun setupStartDestination(navController: NavController) {
        val graph = navController.navInflater.inflate(R.navigation.nav_graph_main)
        graph.setStartDestination(
            R.id.loadingScreen
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
            binding.additionalSpace.visibility = View.GONE
        }
        else {
            binding.additionalSpace.visibility = View.VISIBLE
        }
        binding.toolbar.title = HelperFunctions.setTitle(destination.label, arguments)

        if (destination.id == R.id.loadingScreen || destination.id == R.id.tabsFragment ||
            destination.id == R.id.loginFragment || destination.id == R.id.registrationFragment
            || destination.id == R.id.welcomeFragment)
            binding.appBar.visibility = View.GONE
        else
            binding.appBar.visibility = View.VISIBLE
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