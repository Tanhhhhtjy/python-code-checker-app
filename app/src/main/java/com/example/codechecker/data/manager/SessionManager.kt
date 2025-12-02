package com.example.codechecker.data.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import java.io.IOException
import com.example.codechecker.data.model.Session

// 在顶层声明扩展属性
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class SessionManager(context: Context) {
    
    private val dataStore = context.dataStore
    
    companion object {
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val DISPLAY_NAME_KEY = stringPreferencesKey("display_name")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }
    
    // 保存用户会话
    suspend fun saveSession(
        userId: Long,
        username: String,
        displayName: String,
        role: String
    ) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USERNAME_KEY] = username
            preferences[DISPLAY_NAME_KEY] = displayName
            preferences[USER_ROLE_KEY] = role
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }
    
    // 清除用户会话（登出）
    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
            preferences.remove(USERNAME_KEY)
            preferences.remove(DISPLAY_NAME_KEY)
            preferences.remove(USER_ROLE_KEY)
            preferences[IS_LOGGED_IN_KEY] = false
        }
    }
    
    // 获取当前会话
    val sessionFlow: Flow<Session?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val userId = preferences[USER_ID_KEY]
            val username = preferences[USERNAME_KEY]
            val displayName = preferences[DISPLAY_NAME_KEY]
            val role = preferences[USER_ROLE_KEY]
            val isLoggedIn = preferences[IS_LOGGED_IN_KEY] ?: false
            
            if (userId != null && username != null && displayName != null && role != null && isLoggedIn) {
                Session(userId, username, displayName, role, isLoggedIn)
            } else {
                null
            }
        }
    
    // 检查是否已登录（简化版）
    val isLoggedInFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }
    
    // 获取用户ID（立即返回，用于同步操作）
    suspend fun getUserId(): Long? {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }.catch { _ -> emit(null) }.firstOrNull()
    }
    
    // 获取用户角色（立即返回，用于同步操作）
    suspend fun getUserRole(): String? {
        return dataStore.data.map { preferences ->
            preferences[USER_ROLE_KEY]
        }.catch { _ -> emit(null) }.firstOrNull()
    }
}