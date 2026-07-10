package com.baralhobiblico.app.game

import kotlinx.serialization.Serializable

enum class Screen { HOME, SETUP, GAME, RESULT, STUDY, STUDY_CARD }

enum class Mode(val title: String, val subtitle: String, val needsPlayers: Boolean) {
    SOLO("Modo Sozinho", "Treine e desafie a si mesmo", false),
    GRUPO("Em grupo / equipes", "Um narrador, várias equipes", true),
    PVP("Individual (passa e joga)", "Cada um na sua vez", true),
    ESTUDO("Modo estudo", "Todas as dicas, sem pontuação", false)
}

enum class Phase { CLUE, REVEALED, GRUPO_WHO, GRUPO_NONE, ANSWERED }

enum class InputMode { SELF, TYPE }

/** Jogador ou equipe. Imutável — atualizações via copy() para disparar recomposição. */
data class Player(
    val name: String,
    val colorIndex: Int,
    val score: Int = 0,
    val hits: Int = 0
)

/** Resultado de um palpite digitado. */
data class AnswerResult(val playerIdx: Int?, val pts: Int, val correct: Boolean)

// ---------- Estruturas serializáveis para salvar a partida ----------

@Serializable
data class SavedPlayer(val name: String, val colorIndex: Int, val score: Int, val hits: Int)

@Serializable
data class SavedAnswer(val playerIdx: Int?, val pts: Int, val correct: Boolean)

@Serializable
data class GameSave(
    val mode: String,
    val players: List<SavedPlayer>,
    val roundsSetting: Int,
    val inputMode: String,
    val deck: List<Int>,
    val deckPos: Int,
    val limit: Int?,
    val played: Int,
    val turn: Int,
    val cardIdx: Int,
    val shown: Int,
    val phase: String,
    val feedback: String,
    val answer: SavedAnswer?
)
