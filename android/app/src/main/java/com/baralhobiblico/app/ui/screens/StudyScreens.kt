package com.baralhobiblico.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baralhobiblico.app.data.CARDS
import com.baralhobiblico.app.game.GameViewModel
import com.baralhobiblico.app.ui.components.*
import com.baralhobiblico.app.ui.theme.*

@Composable
fun StudyScreen(vm: GameViewModel) {
    val filtered = vm.studyFiltered()
    Column(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 18.dp, vertical = 22.dp)) {
        TopBar(title = "Modo estudo", sub = "Toque num personagem para ver as dicas", onBack = { vm.goHome() })
        OutlinedTextField(
            value = vm.studyQuery, onValueChange = { vm.studyQuery = it },
            placeholder = { Text("Buscar personagem…", color = Faint) },
            singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = fieldColors()
        )
        Spacer(Modifier.height(12.dp))
        Text("${filtered.size} " + if (filtered.size == 1) "personagem" else "personagens", color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filtered) { idx ->
                val c = CARDS[idx]
                Column(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(Bg).border(1.dp, Line, RoundedCornerShape(12.dp))
                        .clickable { vm.openStudyCard(idx) }.padding(15.dp)
                ) {
                    Text(c.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text("${c.clues.size} dicas", color = Faint, fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
                }
            }
        }
    }
}

@Composable
fun StudyCardScreen(vm: GameViewModel) {
    val card = vm.studyCard()
    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        TopBar(
            title = "${vm.studyPos + 1} / ${vm.studyTotal()}", sub = "Modo estudo",
            onBack = { vm.closeStudyCard() },
            trailing = {
                Box(
                    Modifier.clip(RoundedCornerShape(999.dp)).background(Bg).border(1.dp, Line, RoundedCornerShape(999.dp))
                        .clickable { vm.toggleHideName() }.padding(horizontal = 12.dp, vertical = 8.dp)
                ) { Text(if (vm.studyHideName) "👁 Mostrar nome" else "🙈 Ocultar nome", color = Muted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
            }
        )

        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Bg)
                .border(1.dp, Line, RoundedCornerShape(24.dp)).padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("PERSONAGEM", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.6.sp, modifier = Modifier.weight(1f))
                Box(Modifier.clip(RoundedCornerShape(999.dp)).background(Ink).padding(horizontal = 11.dp, vertical = 5.dp)) {
                    Text("${card.clues.size} dicas", color = White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp).clip(RoundedCornerShape(18.dp)).background(Ink).padding(18.dp)
            ) {
                Text("PERSONAGEM", color = White.copy(alpha = 0.55f), fontSize = 12.sp, letterSpacing = 2.sp)
                Text(if (vm.studyHideName) "•••••" else card.name, color = White, fontWeight = FontWeight.Bold, fontSize = 26.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
            }
            Column(Modifier.padding(top = 8.dp)) {
                card.clues.forEachIndexed { i, clue ->
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
                    if (i < card.clues.size - 1) HorizontalDivider(color = Line)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GhostButton("← Anterior", modifier = Modifier.weight(1f)) { vm.studyPrev() }
            Box(
                Modifier.clip(RoundedCornerShape(14.dp)).background(Bg).border(1.dp, Line, RoundedCornerShape(14.dp))
                    .clickable { vm.studyShuffle() }.height(52.dp).padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) { Text("🔀", fontSize = 18.sp) }
            PrimaryButton("Próximo →", modifier = Modifier.weight(1f)) { vm.studyNext() }
        }
    }
}
