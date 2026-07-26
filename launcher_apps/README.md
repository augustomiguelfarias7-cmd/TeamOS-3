# Launcher & Apps - TeamOS 3.0

Interface completa inspirada no **Android 16 (Material 3 Expressive)** + estilo **One UI / Samsung**.

## Fluxo de primeira inicialização (Onboarding)

1. `onboarding/welcome.html` → Tela de boas-vindas + botão **Seguir**
2. `onboarding/language.html` → Seleção de idioma (11 idiomas)
3. `onboarding/wifi.html` → Configuração de rede Wi-Fi
4. Depois entra no launcher principal

## Launcher principal

- `launcher/home.html` → Tela inicial completa
  - Status bar
  - Relógio grande
  - Grade de apps
  - Dock inferior
  - **Quick Settings Panel** (deslize do topo para baixo ou clique na status bar)

### Quick Settings inclui:
- Brilho (slider)
- Wi-Fi
- Dados móveis
- Bluetooth
- Modo Avião
- Lanterna
- Não perturbar
- Localização
- Rotação de tela

## Apps

- `apps/gallery/` → App de imagens

## Como testar

Abra os arquivos HTML no navegador (celular ou PC).  
Comece por: `onboarding/welcome.html`
