package com.baralhobiblico.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baralhobiblico.app.game.GameViewModel
import com.baralhobiblico.app.game.InputMode
import com.baralhobiblico.app.game.Mode
import com.baralhobiblico.app.ui.components.*
import com.baralhobiblico.app.ui.theme.*

@Composable
fun SetupScreen(vm: GameViewModel) {
    var newName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        TopBar(title = vm.mode.title, sub = vm.mode.subtitle, onBack = { vm.goHome() })

        if (vm.mode.needsPlayers) {
            Panel(title = if (vm.mode == Mode.GRUPO) "Equipes ou participantes" else "Jogadores") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        placeholder = { Text(if (vm.mode == Mode.GRUPO) "Nome da equipe ou pessoa" else "Nome do jogador", color = Faint) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { vm.addPlayer(newName); newName = "" })
                    )
                    Spacer(Modifier.width(10.dp))
                    Box(
                        modifier = Modifier.height(56.dp).clip(RoundedCornerShape(12.dp)).background(Surface2)
                            .border(1.dp, Line, RoundedCornerShape(12.dp))
                            .clickable { vm.addPlayer(newName); newName = "" }.padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("+ Add", fontWeight = FontWeight.SemiBold) }
                }
                Spacer(Modifier.height(12.dp))
                if (vm.players.isEmpty()) {
                    HintText("Adicione pelo menos 2 participantes para começar.")
                } else {
                    vm.players.forEachIndexed { i, p ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                                .clip(RoundedCornerShape(12.dp)).background(Bg).border(1.dp, Line, RoundedCornerShape(12.dp))
                                .padding(horizontal = 13.dp, vertical = 11.dp)
                        ) {
                            PlayerDot(p.colorIndex)
                            Spacer(Modifier.width(12.dp))
                            Text(p.name, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            Text("×", color = Muted, fontSize = 22.sp, modifier = Modifier.clickable { vm.removePlayer(i) }.padding(horizontal = 6.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        Panel(title = "Quantas cartas nesta partida?") {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(5, 10, 15, 20, 0).forEach { r ->
                    SelectChip(if (r == 0) "Livre" else "$r", vm.roundsSetting == r) { vm.setRounds(r) }
                }
            }
            Spacer(Modifier.height(10.dp))
            val roundsHint = when {
                vm.roundsSetting == 0 -> "Livre: joga até acabar o baralho ou até você encerrar."
                vm.mode == Mode.PVP -> "Cada jogador joga ${vm.roundsSetting} cartas."
                else -> "Partida com ${vm.roundsSetting} cartas."
            }
            Text(roundsHint, color = Faint, fontSize = 13.sp)
        }
        Spacer(Modifier.height(16.dp))

        if (vm.mode != Mode.GRUPO) {
            Panel(title = "Como responder?") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SelectChip("✋ Marcar eu mesmo", vm.inputMode == InputMode.SELF) { vm.setInputMode(InputMode.SELF) }
                    SelectChip("⌨️ Digitar o nome", vm.inputMode == InputMode.TYPE) { vm.setInputMode(InputMode.TYPE) }
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    if (vm.inputMode == InputMode.TYPE)
                        "Você digita o nome; se errar, aparece a próxima dica (valendo menos)."
                    else "Você mesmo confere e marca se acertou.",
                    color = Faint, fontSize = 13.sp
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        PrimaryButton("Começar", enabled = vm.canStart()) { vm.startGame() }
    }
}

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Ink,
    unfocusedBorderColor = Line,
    focusedTextColor = TextMain,
    unfocusedTextColor = TextMain,
    cursorColor = Ink,
    focusedContainerColor = Bg,
    unfocusedContainerColor = Bg
)
