package com.baralhobiblico.app.game

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.baralhobiblico.app.data.CARDS
import com.baralhobiblico.app.data.Card

class GameViewModel(app: Application) : AndroidViewModel(app) {

    private val storage = Storage(app)

    // ---------- Navegação / setup ----------
    var screen by mutableStateOf(Screen.HOME)
        private set
    var mode by mutableStateOf(Mode.SOLO)
        private set
    val players = mutableStateListOf<Player>()
    var roundsSetting by mutableStateOf(10)     // 0 = livre
        private set
    var inputMode by mutableStateOf(InputMode.SELF)
        private set

    // ---------- Runtime da partida ----------
    private var deck: List<Int> = emptyList()
    private var deckPos = 0
    var limit by mutableStateOf<Int?>(null)     // null = livre
        private set
    var played by mutableStateOf(0)
        private set
    var turn by mutableStateOf(0)
        private set
    var card by mutableStateOf<Card?>(null)
        private set
    var shown by mutableStateOf(1)
        private set
    var phase by mutableStateOf(Phase.CLUE)
        private set
    var feedback by mutableStateOf("")
        private set
    var answerResult by mutableStateOf<AnswerResult?>(null)
        private set

    // ---------- Modo estudo ----------
    private val studyOrder: List<Int> =
        CARDS.indices.sortedBy { CARDS[it].name }
    var studyQuery by mutableStateOf("")
    var studyPos by mutableStateOf(0)
        private set
    var studyHideName by mutableStateOf(false)
        private set

    // ---------- Continuar partida ----------
    var savedGame by mutableStateOf<GameSave?>(null)
        private set

    init { refreshContinue() }

    val points: Int get() = pointsFor(shown)
    fun scoreTarget(): Int = if (mode == Mode.PVP) turn else 0

    fun refreshContinue() { savedGame = storage.loadGame() }

    fun goHome() {
        screen = Screen.HOME
        studyHideName = false
        refreshContinue()
    }

    // ================= SETUP =================
    fun openMode(m: Mode) {
        if (m == Mode.ESTUDO) { openStudy(); return }
        mode = m
        roundsSetting = 10
        inputMode = InputMode.SELF
        players.clear()
        if (m.needsPlayers) {
            storage.loadLastPlayers().take(10).forEachIndexed { i, name ->
                players.add(Player(name, i % PLAYER_COLORS))
            }
        }
        screen = Screen.SETUP
    }

    fun setRounds(r: Int) { roundsSetting = r }
    fun setInputMode(m: InputMode) { inputMode = m }

    fun addPlayer(name: String) {
        val n = name.trim()
        if (n.isEmpty() || players.size >= 10) return
        players.add(Player(n, players.size % PLAYER_COLORS))
    }
    fun removePlayer(index: Int) { if (index in players.indices) players.removeAt(index) }
    fun canStart(): Boolean = if (mode.needsPlayers) players.size >= 2 else true

    // ================= JOGO =================
    fun startGame() {
        if (mode.needsPlayers) storage.saveLastPlayers(players.map { it.name })
        if (mode == Mode.SOLO) {
            players.clear()
            players.add(Player("Você", 0))
        }
        for (i in players.indices) players[i] = players[i].copy(score = 0, hits = 0)

        deck = CARDS.indices.shuffled()
        deckPos = 0
        played = 0
        turn = 0
        limit = when {
            roundsSetting == 0 -> null
            mode == Mode.PVP -> minOf(roundsSetting * players.size, CARDS.size)
            else -> minOf(roundsSetting, CARDS.size)
        }
        screen = Screen.GAME
        nextCard()
    }

    private fun nextCard() {
        val lim = limit
        if (deckPos >= deck.size || (lim != null && played >= lim)) { endGame(); return }
        card = CARDS[deck[deckPos++]]
        shown = 1
        phase = Phase.CLUE
        feedback = ""
        answerResult = null
        persist()
    }

    fun revealNextClue() {
        val c = card ?: return
        if (shown < c.clues.size) { shown++; persist() }
    }

    fun revealAnswer() { phase = Phase.REVEALED; persist() }          // self mode
    fun grupoSomeoneAnswered() { phase = Phase.GRUPO_WHO; persist() }
    fun grupoNobody() { phase = Phase.GRUPO_NONE; persist() }
    fun backToClue() { phase = Phase.CLUE; persist() }

    /** Modo "marcar eu mesmo": acertou / errou. */
    fun selfMark(correct: Boolean) {
        award(if (correct) scoreTarget() else null, if (correct) points else 0)
    }

    /** Modo grupo: a equipe [index] acertou. */
    fun grupoAward(index: Int) { award(index, points) }

    /** Avança sem dar pontos (ninguém acertou). */
    fun advanceNoScore() { award(null, 0) }

    /** Encerra uma partida no modo livre. */
    fun endFreeGame() { if (played == 0) quitGame(false) else endGame() }

    /** Modo digitar: envia um palpite. */
    fun submitGuess(text: String) {
        val c = card ?: return
        if (text.isBlank()) return
        when {
            isCorrectGuess(text, c) -> {
                answerResult = AnswerResult(scoreTarget(), points, true)
                phase = Phase.ANSWERED
            }
            shown < c.clues.size -> {
                shown++
                feedback = "❌ Não é “${text.trim()}”. Mais uma dica…"
            }
            else -> {
                feedback = ""
                answerResult = AnswerResult(null, 0, false)
                phase = Phase.ANSWERED
            }
        }
        persist()
    }

    fun giveUp() {
        feedback = ""
        answerResult = AnswerResult(null, 0, false)
        phase = Phase.ANSWERED
        persist()
    }

    /** Confirma o resultado (botão "Próxima carta") e avança. */
    fun confirmAnswer() {
        val r = answerResult ?: AnswerResult(null, 0, false)
        award(r.playerIdx, r.pts)
    }

    private fun award(playerIdx: Int?, pts: Int) {
        if (playerIdx != null && pts > 0 && playerIdx in players.indices) {
            val p = players[playerIdx]
            players[playerIdx] = p.copy(score = p.score + pts, hits = p.hits + 1)
        }
        played++
        if (mode == Mode.PVP && players.isNotEmpty()) turn = (turn + 1) % players.size
        nextCard()
    }

    private fun endGame() {
        storage.clearGame()
        savedGame = null
        screen = Screen.RESULT
    }

    /** Sair do jogo. Se [keepSave], a partida fica salva para continuar depois. */
    fun quitGame(keepSave: Boolean) {
        if (!keepSave) { storage.clearGame() }
        goHome()
    }

    /** Ranking para a tela de resultado (maior pontuação primeiro). */
    fun ranking(): List<Player> = players.sortedByDescending { it.score }

    // ================= CONTINUAR =================
    fun resumeGame() {
        val s = storage.loadGame() ?: return
        mode = Mode.valueOf(s.mode)
        players.clear()
        s.players.forEach { players.add(Player(it.name, it.colorIndex, it.score, it.hits)) }
        roundsSetting = s.roundsSetting
        inputMode = InputMode.valueOf(s.inputMode)
        deck = s.deck
        deckPos = s.deckPos
        limit = s.limit
        played = s.played
        turn = s.turn
        card = CARDS.getOrNull(s.cardIdx)
        shown = s.shown
        phase = Phase.valueOf(s.phase)
        feedback = s.feedback
        answerResult = s.answer?.let { AnswerResult(it.playerIdx, it.pts, it.correct) }
        screen = Screen.GAME
    }

    fun discardSavedGame() { storage.clearGame(); savedGame = null }

    private fun persist() {
        val c = card ?: return
        if (mode == Mode.ESTUDO) return
        storage.saveGame(
            GameSave(
                mode = mode.name,
                players = players.map { SavedPlayer(it.name, it.colorIndex, it.score, it.hits) },
                roundsSetting = roundsSetting,
                inputMode = inputMode.name,
                deck = deck,
                deckPos = deckPos,
                limit = limit,
                played = played,
                turn = turn,
                cardIdx = CARDS.indexOf(c),
                shown = shown,
                phase = phase.name,
                feedback = feedback,
                answer = answerResult?.let { SavedAnswer(it.playerIdx, it.pts, it.correct) }
            )
        )
        savedGame = storage.loadGame()
    }

    // ================= ESTUDO =================
    fun openStudy() { studyQuery = ""; screen = Screen.STUDY }

    fun studyFiltered(): List<Int> {
        val q = studyQuery.trim().lowercase()
        return if (q.isEmpty()) studyOrder
        else studyOrder.filter { CARDS[it].name.lowercase().contains(q) }
    }

    fun openStudyCard(globalIdx: Int) {
        studyPos = studyOrder.indexOf(globalIdx).coerceAtLeast(0)
        screen = Screen.STUDY_CARD
    }
    fun studyCard(): Card = CARDS[studyOrder[studyPos]]
    fun studyTotal(): Int = studyOrder.size
    fun studyNext() { studyPos = (studyPos + 1) % studyOrder.size }
    fun studyPrev() { studyPos = (studyPos - 1 + studyOrder.size) % studyOrder.size }
    fun studyShuffle() { studyPos = studyOrder.indices.random() }
    fun toggleHideName() { studyHideName = !studyHideName }
    fun closeStudyCard() { studyHideName = false; screen = Screen.STUDY }

    companion object { const val PLAYER_COLORS = 6 }
}
