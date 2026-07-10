package com.baralhobiblico.app.game

import com.baralhobiblico.app.data.Card
import java.text.Normalizer

/** Pontos por acerto: 1ª dica = 7 ... 7ª = 1 (piso de 1). */
fun pointsFor(shown: Int): Int = maxOf(1, 8 - shown)

/** Normaliza um texto: minúsculas, sem acentos, sem pontuação e sem artigo inicial. */
fun normalize(s: String): String {
    val noAccents = Normalizer.normalize(s, Normalizer.Form.NFD)
        .replace("\\p{M}+".toRegex(), "")
    return noAccents.lowercase()
        .replace("[^a-z0-9\\s]".toRegex(), " ")
        .replace("\\s+".toRegex(), " ")
        .trim()
        .replace("^(a|o|as|os)\\s+".toRegex(), "")
}

/** Variações aceitas para o nome de uma carta ao digitar a resposta. */
fun acceptedAnswers(card: Card): List<String> {
    val full = normalize(card.name)
    val beforeComma = normalize(card.name.substringBefore(","))
    val set = linkedSetOf(full, beforeComma)
    val firstWord = beforeComma.split(" ").firstOrNull() ?: ""
    if (firstWord.length >= 3) set.add(firstWord)
    if (card.name == "A MULHER SAMARITANA") {
        set.add("samaritana")
        set.add("mulher samaritana")
    }
    return set.toList()
}

/** Verifica se um palpite digitado corresponde ao personagem (tolerante a acentos/maiúsculas/pontuação). */
fun isCorrectGuess(guess: String, card: Card): Boolean {
    val g = normalize(guess)
    return g.isNotEmpty() && acceptedAnswers(card).contains(g)
}
