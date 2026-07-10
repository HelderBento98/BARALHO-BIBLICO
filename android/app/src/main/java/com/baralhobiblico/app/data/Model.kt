package com.baralhobiblico.app.data

/** Uma dica de uma carta: o texto da pergunta e a referência bíblica. */
data class Clue(val text: String, val ref: String)

/** Uma carta do baralho: o personagem e suas dicas (da mais difícil à mais fácil). */
data class Card(val name: String, val clues: List<Clue>)
