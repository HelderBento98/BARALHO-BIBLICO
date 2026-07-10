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
import com.baralhobiblico.app.data.CARDS
import com.baralhobiblico.app.game.GameViewModel
import com.baralhobiblico.app.game.Mode
import com.baralhobiblico.app.ui.components.*
import com.baralhobiblico.app.ui.theme.*

@Composable
fun HomeScreen(vm: GameViewModel) {
    var showRules by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        // marca
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
            Box(
                modifier = Modifier.size(46.dp).clip(RoundedCornerShape(13.dp)).background(Ink),
                contentAlignment = Alignment.Center
            ) { Text("BB", color = White, fontWeight = FontWeight.Bold, fontSize = 20.sp) }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Baralho Bíblico", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                Text("Adivinhe o personagem pelas dicas", color = Muted, fontSize = 13.sp)
            }
        }

        // hero
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 22.dp)) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(Surface1)
                    .border(1.dp, Line, RoundedCornerShape(999.dp)).padding(horizontal = 14.dp, vertical = 6.dp)
            ) { Text("${CARDS.size} PERSONAGENS", color = InkSoft, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp) }
            Spacer(Modifier.height(16.dp))
            Text("Das dicas difíceis", fontWeight = FontWeight.Bold, fontSize = 32.sp, color = Ink, textAlign = TextAlign.Center, lineHeight = 34.sp)
            Text("às mais fáceis", fontWeight = FontWeight.Bold, fontSize = 32.sp, color = Faint, textAlign = TextAlign.Center, lineHeight = 34.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                "Quanto antes você acertar, mais pontos ganha. Escolha um modo de jogo.",
                color = Muted, textAlign = TextAlign.Center, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        // continuar partida
        vm.savedGame?.let { s ->
            val label = (Mode.valueOf(s.mode)).title + " · carta " + (s.played + 1) + (s.limit?.let { " de $it" } ?: "")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clip(RoundedCornerShape(18.dp)).background(Ink)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f).clickable { vm.resumeGame() }.padding(16.dp)
                ) {
                    Box(Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(White.copy(alpha = 0.14f)), contentAlignment = Alignment.Center) {
                        Text("▶", color = White, fontSize = 16.sp)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Continuar partida", color = White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Text(label, color = White.copy(alpha = 0.65f), fontSize = 13.sp)
                    }
                }
                Box(
                    Modifier.fillMaxHeight().width(46.dp).clickable { vm.discardSavedGame() },
                    contentAlignment = Alignment.Center
                ) { Text("×", color = White.copy(alpha = 0.6f), fontSize = 22.sp) }
            }
        }

        // modos
        ModeCard("🎯", "Sozinho", "Treine e desafie a si mesmo, carta por carta.") { vm.openMode(Mode.SOLO) }
        Spacer(Modifier.height(14.dp))
        ModeCard("👥", "Em grupo / equipes", "Um narrador lê as dicas; as equipes disputam os pontos.") { vm.openMode(Mode.GRUPO) }
        Spacer(Modifier.height(14.dp))
        ModeCard("🔁", "Individual (passa e joga)", "Cada um na sua vez, no mesmo aparelho.") { vm.openMode(Mode.PVP) }
        Spacer(Modifier.height(14.dp))
        ModeCard("📖", "Modo estudo", "Veja todas as dicas e aprenda os personagens.") { vm.openMode(Mode.ESTUDO) }

        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("Como jogar", color = Muted, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { showRules = true })
            Spacer(Modifier.width(24.dp))
            Text("Sobre", color = Muted, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { showAbout = true })
        }
        Spacer(Modifier.height(20.dp))
        Text("Baseado no baralho físico “Baralho Bíblico”.", color = Faint, fontSize = 12.sp,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }

    if (showRules) RulesDialog { showRules = false }
    if (showAbout) AboutDialog { showAbout = false }
}

@Composable
private fun ModeCard(icon: String, title: String, desc: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Bg)
            .border(1.dp, Line, RoundedCornerShape(18.dp)).clickable { onClick() }.padding(18.dp)
    ) {
        Box(
            modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(Surface2).border(1.dp, Line, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) { Text(icon, fontSize = 24.sp) }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
            Text(desc, color = Muted, fontSize = 13.sp)
        }
        Text("→", color = Faint, fontSize = 20.sp)
    }
}

@Composable
private fun RulesDialog(onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = { TextButton(onClick = onClose) { Text("Entendi", color = Ink, fontWeight = FontWeight.SemiBold) } },
        title = { Text("Como jogar", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("• Cada carta é um personagem bíblico com dicas que vão da mais difícil para a mais fácil.", color = Muted, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                Text("• As dicas são reveladas uma a uma. Quem adivinha com menos dicas ganha mais pontos: 1ª dica = 7 … 7ª = 1.", color = Muted, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                Text("• Em grupo: o narrador lê as dicas (e vê a resposta) e marca a equipe que acertou.", color = Muted, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                Text("• Individual: cada jogador, na sua vez, revela as dicas e marca se acertou (ou digita o nome).", color = Muted, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                Text("• Vence quem tiver mais pontos ao fim da partida.", color = Muted, fontSize = 14.sp)
            }
        },
        containerColor = Bg
    )
}

@Composable
private fun AboutDialog(onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = { TextButton(onClick = onClose) { Text("Fechar", color = Ink, fontWeight = FontWeight.SemiBold) } },
        title = { Text("Sobre", fontWeight = FontWeight.Bold) },
        text = {
            Text(
                "Adaptação digital do Baralho Bíblico, com ${CARDS.size} personagens da Bíblia — de Abraão a Zorobabel. " +
                    "Cada personagem traz dicas com as referências bíblicas, das mais difíceis às mais fáceis. " +
                    "Ideal para reuniões, escola bíblica, família e grupos de estudo.",
                color = Muted, fontSize = 14.sp
            )
        },
        containerColor = Bg
    )
}
