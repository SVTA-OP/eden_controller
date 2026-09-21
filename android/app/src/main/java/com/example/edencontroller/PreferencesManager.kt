package com.example.edencontroller

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val TARGET_IP = stringPreferencesKey("target_ip")
        val TARGET_PORT = intPreferencesKey("target_port")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val OPACITY = floatPreferencesKey("opacity")
        val SCALE = floatPreferencesKey("scale")
    }

    val targetIp: Flow<String> = dataStore.data.map { it[TARGET_IP] ?: "127.0.0.1" }
    val targetPort: Flow<Int> = dataStore.data.map { it[TARGET_PORT] ?: 9876 }
    val hapticsEnabled: Flow<Boolean> = dataStore.data.map { it[HAPTICS_ENABLED] ?: true }
    val opacity: Flow<Float> = dataStore.data.map { it[OPACITY] ?: 0.8f }
    val scale: Flow<Float> = dataStore.data.map { it[SCALE] ?: 1.0f }

    suspend fun setTargetIp(ip: String) {
        dataStore.edit { it[TARGET_IP] = ip }
    }
    suspend fun setTargetPort(port: Int) {
        dataStore.edit { it[TARGET_PORT] = port }
    }
    suspend fun setHapticsEnabled(enabled: Boolean) {
        dataStore.edit { it[HAPTICS_ENABLED] = enabled }
    }
    suspend fun setOpacity(op: Float) {
        dataStore.edit { it[OPACITY] = op }
    }
    suspend fun setScale(sc: Float) {
        dataStore.edit { it[SCALE] = sc }
    }
}
