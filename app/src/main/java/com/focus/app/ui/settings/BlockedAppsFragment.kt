package com.focus.app.ui.settings

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.focus.app.databinding.FragmentBlockedAppsBinding
import com.focus.app.util.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlockedAppsFragment : Fragment() {

    private var _binding: FragmentBlockedAppsBinding? = null
    private val binding get() = _binding!!
    private lateinit var appSettings: AppSettings
    private lateinit var adapter: BlockedAppsAdapter
    private val appList = mutableListOf<BlockedAppInfo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlockedAppsBinding.inflate(inflater, container, false)
        appSettings = AppSettings(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadInstalledApps()
    }

    private fun setupRecyclerView() {
        adapter = BlockedAppsAdapter(appList) { appInfo, isBlocked ->
            // Update the setting when the switch is toggled
            appSettings.setAppBlocked(appInfo.packageName, isBlocked)
            Log.d("BlockedAppsFragment", "App ${appInfo.packageName} blocked state set to: $isBlocked")
        }
        binding.recyclerViewBlockedApps.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewBlockedApps.adapter = adapter
    }

    private fun loadInstalledApps() {
        CoroutineScope(Dispatchers.IO).launch {
            var totalPackagesFound = 0
            var filteredAppsCount = 0
            val loadedApps = mutableListOf<BlockedAppInfo>()
            try { // Wrap the whole background process
                val pm = requireContext().packageManager
                val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                totalPackagesFound = packages.size
                Log.d("BlockedAppsFragment", "Found $totalPackagesFound total packages installed.")

                val currentBlockedApps = appSettings.getBlockedApps()

                for (appInfo in packages) {
                    // Filter to include only launchable, non-system apps
                    if (pm.getLaunchIntentForPackage(appInfo.packageName) != null && !isSystemApp(appInfo)) {
                        try {
                            val appName = pm.getApplicationLabel(appInfo).toString()
                            val appIcon = pm.getApplicationIcon(appInfo)
                            val packageName = appInfo.packageName
                            val isBlocked = currentBlockedApps.contains(packageName)

                            loadedApps.add(BlockedAppInfo(appName, packageName, appIcon, isBlocked))
                        } catch (e: PackageManager.NameNotFoundException) {
                            // This specific error might be okay for some packages, just log it
                            Log.w("BlockedAppsFragment", "Couldn't get info for package ${appInfo.packageName}", e)
                        } catch (e: Exception) {
                            // Catch unexpected errors getting info for a specific app
                            Log.e("BlockedAppsFragment", "Unexpected error loading info for ${appInfo.packageName}", e)
                        }
                    }
                }

                filteredAppsCount = loadedApps.size
                Log.d("BlockedAppsFragment", "Filtered down to $filteredAppsCount launchable, non-system apps.")

                // Sort apps alphabetically by name
                loadedApps.sortBy { it.appName.lowercase() }

                withContext(Dispatchers.Main) {
                    appList.clear()
                    appList.addAll(loadedApps)
                    adapter.notifyDataSetChanged()
                    if (filteredAppsCount == 0) {
                        Log.w("BlockedAppsFragment", "App list is empty after filtering. No apps to display.")
                        // TODO: Consider showing a 'No apps found' message in the UI
                    }
                }
            } catch (e: Exception) {
                // Catch errors during the overall loading process (e.g., getting package list)
                Log.e("BlockedAppsFragment", "Fatal error loading installed applications list", e)
                withContext(Dispatchers.Main) {
                    // Ensure UI shows an empty list on error
                    appList.clear()
                    adapter.notifyDataSetChanged()
                    // TODO: Consider showing an error message in the UI
                }
            }
        }
    }

    // Helper to check if an app is a system app (optional filtering)
    private fun isSystemApp(appInfo: android.content.pm.ApplicationInfo): Boolean {
        return (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
