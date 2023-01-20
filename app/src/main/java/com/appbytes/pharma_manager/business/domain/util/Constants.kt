package com.appbytes.pharma_manager.business.domain.util

class Constants {

    companion object{

//        const val BASE_URL = "http://10.0.2.2:8000/api/"    // For emulator
//        const val BASE_URL = "http://127.0.0.1/:8000/api/"
//        const val BASE_URL = "http://192.168.0.104:8000/api/"
//        const val BASE_URL = "http://79.143.183.43/api/"
        const val BASE_URL = "https://pharmacy.educodiv.com/api/"
        const val PASSWORD_RESET_URL: String = "https://pharmacy.educodiv.com/password_reset/"
            const val PRIVACY_POLIC = "https://pharmacy.educodiv.com/privacy-policy"


        const val NETWORK_TIMEOUT = 6000L
        const val CACHE_TIMEOUT = 2000L
        const val TESTING_NETWORK_DELAY = 0L // fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing

        const val PAGINATION_PAGE_SIZE = 50

        const val GALLERY_REQUEST_CODE = 201
        const val PERMISSIONS_REQUEST_READ_STORAGE: Int = 301
        const val CROP_IMAGE_INTENT_CODE: Int = 401
    }

}