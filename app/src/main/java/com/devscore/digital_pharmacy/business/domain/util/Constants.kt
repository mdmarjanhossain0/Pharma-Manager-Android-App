package com.devscore.digital_pharmacy.business.domain.util

class Constants {

    companion object{

        const val BASE_URL = "http://10.0.2.2:8000/api/"    // For emulator
//        const val BASE_URL = "http://127.0.0.1/:8000/api/"
//        const val BASE_URL = "http://192.168.43.254:8000/api/"
//        const val BASE_URL = "http://79.143.183.43/api/"
        const val PASSWORD_RESET_URL: String = "http://157.245.147.57/password_reset/"


        const val NETWORK_TIMEOUT = 6000L
        const val CACHE_TIMEOUT = 2000L
        const val TESTING_NETWORK_DELAY = 0L // fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing

        const val PAGINATION_PAGE_SIZE = 10000

        const val GALLERY_REQUEST_CODE = 201
        const val PERMISSIONS_REQUEST_READ_STORAGE: Int = 301
        const val CROP_IMAGE_INTENT_CODE: Int = 401
    }

}