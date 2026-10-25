# 📺 GAMA TV — Aplicativo Android (APK)

Projeto refatorado para compilar automaticamente um arquivo **.apk** instalável para Android TV via GitHub Actions, utilizando **Capacitor**.

## 🚀 Como gerar o APK pelo GitHub

1. Arraste todos esses arquivos para a raiz do seu projeto no **github.dev**.
2. Faça o commit (`Ctrl + Shift + G`).
3. Vá para a aba **Actions** no repositório do GitHub.
4. Você verá um workflow chamado **Build Android APK** rodando. Ele irá baixar o Java, o Android SDK, integrar seu código web com o Capacitor e compilar o app.
5. Quando terminar, role até a parte inferior do resumo da execução na aba Actions e baixe o artefato **`gama-tv-apk`**. O arquivo `app-debug.apk` estará lá dentro.

## 📺 Como instalar na TV (via PowerShell)

Com o arquivo baixado no seu PC, abra o PowerShell e conecte-se à TV:
```bash
adb connect 192.168.18.5
adb install caminho/para/o/app-debug.apk
```
