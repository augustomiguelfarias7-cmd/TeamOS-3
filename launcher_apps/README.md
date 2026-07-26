# Launcher & Apps - TeamOS 3.0

Interface **nativa Android** (Kotlin + XML)  
Visual: mistura de **Android 16 (Material 3 Expressive / Pixel)** + **One UI 8 (Samsung)**

## Estrutura

```
launcher_apps/
├── onboarding/          # Primeira inicialização (Kotlin)
├── launcher/            # Tela inicial + Quick Settings
├── apps/                # Apps nativos do sistema
│   ├── gallery/
│   ├── camera/
│   ├── store/           # Loja de apps (WebView)
│   └── settings/
├── navigator/           # Navegadores
│   ├── download-firefox.sh
│   └── teamos-browser/  # Navegador próprio (WebView + abas)
└── res/                 # Temas, cores, estilos (One UI 8 + Android 16)
```

## Apps nativos (open source)

- Galeria
- Câmera
- Loja de apps
- Configurações
- Navegador próprio (WebView com abas)
- Firefox (baixado via script em navigator/)

## Não incluído na base open source

- ChatGusto e partes de IA (permanecem fechadas)

## Estilo visual

- Material 3 Expressive (Android 16 Pixel)
- One UI 8 (bordas arredondadas, hierarquia limpa, painéis modernos)
- Cores dinâmicas, blur, animações modernas
