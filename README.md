# MED X — Contagem Internato

Projeto Android nativo, Java, Android 7+; data padrão 01/03/2027 às 00:00. Sem internet.

## Gerar APK no GitHub (sem Android Studio)
1. Crie um repositório privado vazio no GitHub.
2. Extraia o ZIP e envie os arquivos mantendo a estrutura, incluindo `.github/workflows/build.yml` (use Git ou crie esse arquivo pela interface do GitHub se necessário).
3. Em Actions > Gerar APK MEDX > Run workflow, execute na branch main.
4. Após sucesso, abra a execução e baixe o artefato MEDX-APK; extraia app-debug.apk.

## Android Studio
Abra a pasta do projeto, use JDK 17, Gradle 8.9 e SDK 35. Compile com Build > Generate App Bundles or APKs > Generate APKs (ou `gradle :app:assembleDebug`).

## Tablet
Transfira o APK, abra pelo Meus Arquivos e autorize instalar apps desconhecidos para esse aplicativo, se solicitado. Na tela inicial, pressione uma área vazia > Widgets > MED X. Toque no widget para configurar a data.

## Limitações
O relógio nativo mostra horas totais (podem exceder 24), minutos e segundos; os dias e semanas são atualizados por eventos, alarmes e atualização periódica. O Android pode adiar alarmes e atualizações em economia de energia; ao terminar, a mensagem de conclusão depende da próxima atualização. Os anéis representam semanas em uma escala de 52 e dias em uma escala de 7, não progresso real do curso. Código não compilado nem testado em dispositivo neste ambiente.
