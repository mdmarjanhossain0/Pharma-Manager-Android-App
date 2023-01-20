package com.appbytes.pharma_manager.presentation

interface UICommunicationListener {

    fun displayProgressBar(isLoading: Boolean)

    fun expandAppBar()

    fun hideSoftKeyboard()

    fun isStoragePermissionGranted(): Boolean
}