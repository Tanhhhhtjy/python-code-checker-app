package com.example.codechecker.util

import java.security.MessageDigest

object HashUtil {
    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    
    fun validatePassword(input: String, storedHash: String): Boolean {
        return sha256(input) == storedHash
    }
}