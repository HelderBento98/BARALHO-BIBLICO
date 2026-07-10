package com.baralhobiblico.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baralhobiblico.app.game.GameViewModel
import com.baralhobiblico.app.game.Mode
import com.baralhobiblico.app.game.Player
import com.baralhobiblico.app.ui.components.*
import com.baralhobiblico.app.ui.theme.*

@Composable
fun ResultScreen(vm: GameViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        TopBar(title = "Resultado", sub = vm.mode.title, onBack = { vm.goHome() })

        if (vm.mode == Mode.SOLO) {
            val p = vm.players.firstOrNull() ?: Player("Você", 0)
            val maxPossible = vm.played * 7
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                Text("🏅", fontSize = 54.sp)
                Text("${p.score} pontos", fontWeight = FontWeight.Bold, fontSize = 28.sp)
                Text("${p.hits} de ${vm.played} acertos", color = Muted, fontSize = 14.sp)
            }
            ScoreRow(rank = "✓", name = "Acertos", value = "${p.hits}/${vm.played}", lead = true)
            Spacer(Modifier.height(10.dp))
            ScoreRow(rank = "★", name = "Pontuação", value = "${p.score}", sub = "/ $maxPossible")
        } else {
            val ranked = vm.ranking()
            val top = ranked.firstOrNull()
            val tie = ranked.size > 1 && ranked[1].score == (top?.score ?: 0)
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                Text("🏆", fontSize = 54.sp)
                Text(if (tie) "Empate!" else (top?.name ?: "—"), fontWeight = FontWeight.Bold, fontSize = 26.sp)
                Text(
                    if (tie) "no topo com ${top?.score ?: 0} pts" else "venceu com ${top?.score ?: 0} pontos",
                    color = Muted, fontSize = 14.sp
                )
            }
            ranked.forEachIndexed { i, p ->
                val lead = top != null && p.score == top.score && top.score > 0
                ScoreRow(rank = "${i + 1}º", name = p.name, value = "${p.score}", sub = "pts · ${p.hits} acertos", lead = lead, colorIndex = p.colorIndex)
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GhostButton("Início", modifier = Modifier.weight(1f)) { vm.goHome() }
            PrimaryButton("Jogar de novo", modifier = Modifier.weight(1f)) { vm.openMode(vm.mode) }
        }
    }
}

@Composable
private fun ScoreRow(rank: String, name: String, value: String, sub: String? = null, lead: Boolean = false, colorIndex: Int? = null) {
    val bg = if (lead) Ink else Surface1
    val fg = if (lead) White else TextMain
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(bg)
            .border(1.dp, if (lead) Ink else Line, RoundedCornerShape(12.dp)).padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(rank, color = if (lead) White.copy(alpha = 0.7f) else Faint, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.width(30.dp))
        if (colorIndex != null) { PlayerDot(colorIndex, 14); Spacer(Modifier.width(10.dp)) }
        Text(name, color = fg, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Text(value, color = fg, fontWeight = FontWeight.Bold, fontSize = 19.sp)
        if (sub != null) { Spacer(Modifier.width(6.dp)); Text(sub, color = if (lead) White.copy(alpha = 0.6f) else Muted, fontSize = 11.sp) }
    }
}
