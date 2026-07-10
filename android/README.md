# Baralho Bíblico — App Android (Kotlin + Jetpack Compose)

Versão nativa do jogo, escrita em **Kotlin** com **Jetpack Compose**, pronta para
abrir no Android Studio e publicar na **Google Play**. Funciona **100% offline**
(sem internet, sem anúncios, sem permissões).

## O que tem
- **97 personagens** da Bíblia (os mesmos do baralho físico), com dicas e referências.
- **4 modos:** Sozinho, Em grupo/equipes, Individual (passa e joga) e Estudo.
- **Pontuação por dicas usadas** (1ª dica = 7 … 7ª = 1).
- **Digitar a resposta** (tolerante a acentos/maiúsculas) ou marcar você mesmo.
- **Salvar/continuar partida** e lembrar os últimos participantes (SharedPreferences).
- No modo grupo, o **narrador vê a resposta**.
- Visual **claro e moderno** (branco/preto/cinza).

## Como abrir e rodar
1. Instale o **Android Studio** (versão recente — Koala/Ladybug ou mais nova).
2. **File ▸ Open** e selecione a pasta `android/`.
3. Aguarde o **Gradle Sync** (baixa as dependências na 1ª vez).
4. Conecte um celular (ou use um emulador) e clique em **Run ▶**.

> O projeto já vem com o Gradle Wrapper (`gradlew`). Requer **JDK 17** (o Android
> Studio recente já traz). `minSdk 26` (Android 8.0+), `targetSdk 34`.

## Publicar na Google Play (resumo)
1. **Build ▸ Generate Signed Bundle / APK ▸ Android App Bundle (.aab)**.
2. Crie uma **keystore** (guarde bem — é ela que assina todas as atualizações).
3. Crie a conta de desenvolvedor em <https://play.google.com/console> (taxa única de US$ 25).
4. Crie o app, preencha a ficha (nome, descrição, ícone, prints), classificação
   etária e política de privacidade, e envie o **.aab**.
5. Como o app é offline e sem coleta de dados, o formulário de segurança de dados
   é simples ("não coleta dados").

## Personalizar
- **Nome do pacote / applicationId:** `com.baralhobiblico.app` — mude em
  `app/build.gradle.kts` e no `namespace` se quiser um id próprio antes de publicar.
- **Nome do app:** `app/src/main/res/values/strings.xml`.
- **Ícone:** `app/src/main/res/drawable/ic_launcher_foreground.xml` (+ cor de fundo
  em `res/values/colors.xml`). Dá para trocar pelo Asset Studio do Android Studio.
- **Cores/tema:** `app/src/main/java/com/baralhobiblico/app/ui/theme/`.

## Estrutura
```
app/src/main/java/com/baralhobiblico/app/
  MainActivity.kt            # ponto de entrada + roteador de telas
  data/   Model.kt, Cards.kt # 97 cartas (geradas do baralho web)
  game/   GameViewModel.kt, GameLogic.kt, State.kt, Storage.kt
  ui/     theme/, components/, screens/
```
