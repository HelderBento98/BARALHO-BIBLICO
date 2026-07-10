package com.baralhobiblico.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baralhobiblico.app.game.*
import com.baralhobiblico.app.ui.components.*
import com.baralhobiblico.app.ui.theme.*

@Composable
fun GameScreen(vm: GameViewModel) {
    val card = vm.card ?: return
    var showQuit by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        TopBar(title = vm.mode.title, sub = if (vm.mode == Mode.SOLO) "Boa sorte!" else "${vm.players.size} participantes",
            onBack = { if (vm.played > 0 || vm.shown > 1) showQuit = true else vm.quitGame(false) })

        // vez do jogador (pvp)
        if (vm.mode == Mode.PVP) {
            val p = vm.players[vm.turn]
            Row(
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clip(RoundedCornerShape(12.dp))
                    .background(Surface1).border(1.dp, Line, RoundedCornerShape(12.dp)).padding(12.dp)
            ) {
                PlayerDot(p.colorIndex, 13)
                Spacer(Modifier.width(10.dp))
                Text("Vez de ${p.name}", fontWeight = FontWeight.SemiBold)
            }
        }

        // progresso
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
            Text("Carta ${vm.played + 1}", color = Muted, fontSize = 13.sp)
            Box(
                Modifier.weight(1f).padding(horizontal = 12.dp).height(5.dp).clip(RoundedCornerShape(999.dp)).background(Surface3)
            ) {
                val frac = vm.limit?.let { (vm.played.toFloat() / it).coerceIn(0f, 1f) } ?: 0f
                Box(Modifier.fillMaxWidth(frac).fillMaxHeight().clip(RoundedCornerShape(999.dp)).background(Ink))
            }
            Text(vm.limit?.let { "de $it" } ?: "livre", color = Muted, fontSize = 13.sp)
        }

        // carta
        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Bg)
                .border(1.dp, Line, RoundedCornerShape(24.dp)).padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("ADIVINHE O PERSONAGEM", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.6.sp, modifier = Modifier.weight(1f))
                Box(Modifier.clip(RoundedCornerShape(999.dp)).background(Ink).padding(horizontal = 11.dp, vertical = 5.dp)) {
                    Text("Vale ${vm.points}", color = White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // resposta só para o narrador (grupo)
            if (vm.mode == Mode.GRUPO && vm.phase == Phase.CLUE) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp).clip(RoundedCornerShape(12.dp)).background(Ink).padding(horizontal = 14.dp, vertical = 11.dp)
                ) {
                    Text("🔑 RESPOSTA — SÓ O NARRADOR VÊ", color = White.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, modifier = Modifier.weight(1f))
                    Text(card.name, color = White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            // dicas
            Column(Modifier.padding(top = 14.dp)) {
                for (i in 0 until vm.shown) {
                    val clue = card.clues[i]
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Box(
                            Modifier.size(26.dp).clip(RoundedCornerShape(8.dp)).background(Surface2).border(1.dp, Line, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) { Text("${i + 1}", color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(clue.text, fontSize = 16.sp, lineHeight = 22.sp)
                            if (clue.ref.isNotEmpty() && clue.ref != "—")
                                Text(clue.ref, color = Faint, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                    if (i < vm.shown - 1) HorizontalDivider(color = Line)
                }
            }

            // revelação do nome
            val revealed = vm.phase == Phase.REVEALED || vm.phase == Phase.GRUPO_WHO || vm.phase == Phase.GRUPO_NONE || vm.phase == Phase.ANSWERED
            if (revealed) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp).clip(RoundedCornerShape(18.dp)).background(Ink).padding(18.dp)
                ) {
                    Text("O PERSONAGEM É", color = White.copy(alpha = 0.55f), fontSize = 12.sp, letterSpacing = 2.sp)
                    Text(card.name, color = White, fontWeight = FontWeight.Bold, fontSize = 28.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
                }
            }

            Spacer(Modifier.height(18.dp))
            GameActions(vm, card)
        }
    }

    if (showQuit) {
        AlertDialog(
            onDismissRequest = { showQuit = false },
            confirmButton = { TextButton(onClick = { showQuit = false; vm.quitGame(true) }) { Text("Sair", color = Ink, fontWeight = FontWeight.SemiBold) } },
            dismissButton = { TextButton(onClick = { showQuit = false }) { Text("Cancelar", color = Muted) } },
            title = { Text("Sair da partida?", fontWeight = FontWeight.Bold) },
            text = { Text("Ela fica salva para você continuar depois pela tela inicial.", color = Muted) },
            containerColor = Bg
        )
    }
}

@Composable
private fun GameActions(vm: GameViewModel, card: com.baralhobiblico.app.data.Card) {
    val typeMode = vm.inputMode == InputMode.TYPE && vm.mode != Mode.GRUPO

    when {
        vm.phase == Phase.CLUE && typeMode -> {
            var guess by remember(vm.card, vm.shown) { mutableStateOf("") }
            if (vm.feedback.isNotEmpty())
                Text(vm.feedback, color = Muted, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            OutlinedTextField(
                value = guess, onValueChange = { guess = it },
                placeholder = { Text("Quem é o personagem?", color = Faint) },
                singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors()
            )
            Spacer(Modifier.height(11.dp))
            PrimaryButton("Responder · vale ${vm.points}") { vm.submitGuess(guess); guess = "" }
            QuietButton("Não sei — revelar") { vm.giveUp() }
        }

        vm.phase == Phase.CLUE -> {
            if (vm.shown < card.clues.size)
                GhostButton("Próxima dica · cai para ${vm.points - 1} pts") { vm.revealNextClue() }
            else HintText("Todas as dicas reveladas.")
            Spacer(Modifier.height(11.dp))
            if (vm.mode == Mode.GRUPO) {
                PrimaryButton("✋ Alguém acertou") { vm.grupoSomeoneAnswered() }
                QuietButton("Ninguém acertou") { vm.grupoNobody() }
            } else {
                PrimaryButton("Revelar resposta") { vm.revealAnswer() }
            }
        }

        vm.phase == Phase.REVEALED -> {
            val who = if (vm.mode == Mode.PVP) vm.players[vm.turn].name else "Você"
            HintText("$who acertou?")
            PrimaryButton("✓ Acertei · +${vm.points}") { vm.selfMark(true) }
            Spacer(Modifier.height(11.dp))
            GhostButton("✗ Não acertei") { vm.selfMark(false) }
        }

        vm.phase == Phase.GRUPO_WHO -> {
            Text("Quem acertou primeiro? Recebe +${vm.points}", color = Muted, textAlign = TextAlign.Center, fontSize = 14.sp, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))
            vm.players.forEachIndexed { i, p ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(12.dp)).background(Bg)
                        .border(1.dp, Line, RoundedCornerShape(12.dp)).clickable { vm.grupoAward(i) }.padding(horizontal = 15.dp, vertical = 13.dp)
                ) {
                    PlayerDot(p.colorIndex)
                    Spacer(Modifier.width(12.dp))
                    Text(p.name, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text("+${vm.points}", color = Ink, fontWeight = FontWeight.Bold)
                }
            }
            QuietButton("← Voltar") { vm.backToClue() }
        }

        vm.phase == Phase.GRUPO_NONE -> {
            HintText("Ninguém acertou — a carta volta ao baralho.")
            PrimaryButton("Próxima carta →") { vm.advanceNoScore() }
        }

        vm.phase == Phase.ANSWERED -> {
            val r = vm.answerResult
            val last = vm.limit != null && vm.played + 1 >= vm.limit!!
            if (r != null && r.correct) {
                val who = if (vm.mode == Mode.PVP) " para ${vm.players[vm.turn].name}" else ""
                Text("✓ Acertou! +${r.pts}$who", color = Ink, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            } else {
                Text("0 ponto nesta carta.", color = Muted, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
            }
            PrimaryButton(if (last) "Ver resultado →" else "Próxima carta →") { vm.confirmAnswer() }
        }
    }

    if (vm.limit == null) {
        Spacer(Modifier.height(6.dp))
        QuietButton("Encerrar partida") { vm.endFreeGame() }
    }
}
