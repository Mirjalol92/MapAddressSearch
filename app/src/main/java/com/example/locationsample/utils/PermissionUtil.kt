package com.example.locationsample.utils

import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.innfinity.permissionflow.lib.*
import kotlinx.coroutines.flow.collect


fun Fragment.requestPermissions(vararg permissionsToRequest: String, granted: ()->Unit, denied: ()->Unit){
    var allGranted = true

    permissionsToRequest.forEach { permission->
        allGranted = allGranted && ContextCompat.checkSelfPermission(requireContext(), permission) == PermissionChecker.PERMISSION_GRANTED
    }
    if (allGranted){
        granted()
        return
    }
    lifecycleScope.launch {
        requestPermissions(permissionsToRequest.toString()).collect {
            allGranted = it.find { pr-> !pr.isGranted } == null
            if (allGranted){
                granted()
            }else{
                denied()
            }
        }
    }
}