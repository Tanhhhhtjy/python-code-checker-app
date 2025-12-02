package com.example.codechecker.util

import java.security.MessageDigest
import javax.inject.Inject

class HashUtil @Inject constructor() {
    
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
}