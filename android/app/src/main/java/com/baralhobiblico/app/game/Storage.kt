package com.baralhobiblico.app.game

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Salva a partida em andamento e a última lista de participantes (SharedPreferences + JSON). */
class Storage(context: Context) {
    private val prefs = context.getSharedPreferences("baralho_biblico", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun saveGame(save: GameSave) {
        prefs.edit().putString(KEY_GAME, json.encodeToString(save)).apply()
    }

    fun loadGame(): GameSave? {
        val raw = prefs.getString(KEY_GAME, null) ?: return null
        return runCatching { json.decodeFromString<GameSave>(raw) }.getOrNull()
    }

    fun clearGame() {
        prefs.edit().remove(KEY_GAME).apply()
    }

    fun saveLastPlayers(names: List<String>) {
        prefs.edit().putString(KEY_PLAYERS, json.encodeToString(names)).apply()
    }

    fun loadLastPlayers(): List<String> {
        val raw = prefs.getString(KEY_PLAYERS, null) ?: return emptyList()
        return runCatching { json.decodeFromString<List<String>>(raw) }.getOrDefault(emptyList())
    }

    companion object {
        private const val KEY_GAME = "game_v1"
        private const val KEY_PLAYERS = "players_v1"
    }
}
