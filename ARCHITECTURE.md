# Arquitetura do Launcher3 / Trebuchet e Planejamento do Aero Launcher (TeamOS 3.0)

Este documento apresenta uma análise detalhada da arquitetura do **Launcher3** (Android Open Source Project - AOSP) e do **Trebuchet** (LineageOS), explicando seus principais componentes e descrevendo como o novo **Aero Launcher** (o launcher oficial do TeamOS 3.0) foi planejado e estruturado.

---

## 1. Análise da Arquitetura do Launcher3 / Trebuchet

O Launcher3 é o launcher padrão do ecossistema Android Open Source Project (AOSP), e o Trebuchet é a sua versão estendida e customizada mantida pelo LineageOS. A arquitetura de ambos é altamente modular, orientada a eventos e baseada em uma separação clara entre a camada de persistência/dados (Loader/Model) e a camada de exibição (UI).

Os principais componentes e suas responsabilidades são detalhados abaixo:

### A. Launcher (A atividade principal)
*   **Classe principal:** `com.android.launcher3.Launcher`
*   **Função:** Atua como o controlador central (Controller) de toda a aplicação. Ela gerencia o ciclo de vida do Launcher, escuta eventos globais do sistema, inicializa as views principais e coordena as interações entre elas.
*   **Fluxo:** No `onCreate`, ela cria a janela, inicializa as instâncias de `DragLayer`, `Workspace`, `AllAppsContainerView`, e inicia o processo de carregamento de dados chamando o `LauncherModel`.

### B. Workspace (A tela de trabalho/inicial)
*   **Classe principal:** `com.android.launcher3.Workspace`
*   **Função:** É uma subclasse de `PagedView` que gerencia as páginas da tela inicial. O usuário desliza horizontalmente entre diferentes páginas. Cada página é representada por um `CellLayout`.
*   **Responsabilidade:** Controla o scroll horizontal, gerencia o estado da tela (Normal, Dragging, SpringLoaded, Overview) e suporta a adição e remoção dinâmica de páginas.

### C. CellLayout (A grade de posicionamento)
*   **Classe principal:** `com.android.launcher3.CellLayout`
*   **Função:** Um ViewGroup personalizado que implementa um layout de grade bidimensional (geralmente 4x4, 4x5 ou 5x5).
*   **Responsabilidade:** Permite posicionar itens (ícones, pastas e widgets) usando coordenadas de células (`cellX`, `cellY`) e spans de tamanho (`spanX`, `spanY`). Controla a detecção de colisões durante o arrastar-e-soltar (drag-and-drop).

### D. BubbleTextView (Ícone de aplicativo)
*   **Classe principal:** `com.android.launcher3.BubbleTextView`
*   **Função:** É uma subclasse customizada de `TextView` usada para desenhar os ícones de atalho na tela inicial e na gaveta de apps.
*   **Responsabilidade:** Renderiza o ícone do aplicativo centralizado no topo e o rótulo de texto abaixo. Controla estados visuais de toque (efeitos de feedback), badges de notificação e o desenho de "sombras" ou "efeito bolha" no texto para garantir legibilidade sobre qualquer papel de parede.

### E. DragController e DragLayer (Arraste e Soltura)
*   **Classes principais:** `com.android.launcher3.dragndrop.DragController` e `com.android.launcher3.dragndrop.DragLayer`
*   **Função:** A `DragLayer` é a view raiz do Launcher que abrange toda a tela. O `DragController` gerencia o estado lógico de arrastar.
*   **Responsabilidade:** Captura todos os eventos de toque durante o arraste, renderiza o "drag view" (uma cópia visual do ícone sendo arrastado) e despacha eventos de soltura para os alvos (`DropTarget`, como o `Workspace`, lixeiras, etc.).

### F. AllAppsContainerView (Gaveta de aplicativos)
*   **Classe principal:** `com.android.launcher3.allapps.AllAppsContainerView`
*   **Função:** O container que abriga a gaveta de aplicativos (App Drawer).
*   **Responsabilidade:** Contém um `AllAppsRecyclerView` que exibe a lista/grade de todos os aplicativos instalados no sistema em ordem alfabética, além de uma barra de pesquisa dinâmica no topo.

### G. LauncherModel (O motor de dados)
*   **Classe principal:** `com.android.launcher3.LauncherModel`
*   **Função:** Gerencia o estado de dados em memória e coordena o carregamento em background dos aplicativos e atalhos da tela inicial.
*   **Responsabilidade:**
    1.  Monitora instalações, desinstalações e atualizações de pacotes usando `LauncherApps` ou `BroadcastReceiver` (`com.android.launcher3.InstallShortcutReceiver`).
    2.  Carrega a lista de aplicativos instalados.
    3.  Lê o banco de dados do SQLite em uma thread secundária (`LoaderTask`) para obter a configuração da tela inicial do usuário e, em seguida, faz o "binding" (associação) desses dados na thread principal (`LauncherModel.Callbacks`).

### H. LauncherProvider (Persistência)
*   **Classe principal:** `com.android.launcher3.LauncherProvider`
*   **Função:** Um `ContentProvider` que gerencia o banco de dados SQLite (`launcher.db`).
*   **Responsabilidade:** Armazena os atalhos, pastas e widgets criados pelo usuário com suas respectivas coordenadas de tela (`screen`, `cellX`, `cellY`), IDs de contêineres (`container`) e informações do pacote.

### I. AppWidgetHost / AppWidgetHostView (Widgets)
*   **Classes principais:** `com.android.launcher3.LauncherAppWidgetHost` e `com.android.launcher3.LauncherAppWidgetHostView`
*   **Função:** Controladores que gerenciam a exibição e ciclo de vida de widgets do sistema Android.
*   **Responsabilidade:** Ouve atualizações dos widgets vindas do sistema e lida com o dimensionamento dinâmico dos componentes de widget sobre as grades do `CellLayout`.

---

## 2. Planejamento do Aero Launcher (TeamOS 3.0)

Para transformar essa arquitetura robusta em uma experiência única e exclusiva do **TeamOS 3.0**, o **Aero Launcher** foi projetado seguindo uma reinterpretação moderna e limpa de todos esses módulos usando **Java para Android moderno com Gradle**. Ele adota um design visual futurista característico do TeamOS.

### A. Elementos de Design Futurista e Identidade Visual (TeamOS 3.0)
1.  **Glassmorphism (Efeito de Vidro Fosco):** As gavetas de aplicativos, cartões de configurações e widgets personalizados utilizam fundos semi-transparentes com bordas delicadas e cantos extremamente arredondados (`cornerRadius` de `16dp` a `28dp`), simulando vidro sob luz neon.
2.  **Paletas Neon / Glow:**
    *   **Tema Escuro (Cyber Dark):** Fundo em azul-espacial profundo (`#0B0E14`), com acentos de brilho em azul-ciano neon (`#00F0FF`) e violeta/magenta (`#D000FF`).
    *   **Tema Claro (Aero Light):** Fundo branco translúcido (`#EBF3FA`), com detalhes em tons pastel e azul-gelo vibrante.
3.  **Molduras de Ícones Personalizáveis (Icon Framing):** O componente `BubbleTextView` suporta enquadrar ícones de aplicativos automaticamente em formatos geométricos elegantes:
    *   *Hexágono Futurista* (marca registrada do TeamOS 3.0)
    *   *Squircle* (suave e moderno)
    *   *Círculo* (minimalista clássico)
    *   *Glow Ring* (com bordas brilhantes ajustáveis)
4.  **Integração com Assistente ChatGusto:** Uma barra de busca proeminente integrada na gaveta de aplicativos com um botão de atalho dedicado para o **ChatGusto**, o assistente inteligente oficial do TeamOS.

### B. Estrutura de Pastas e Componentes Desenvolvidos
O projeto está estruturado em `/app` usando o padrão moderno do Gradle:

*   `app/src/main/AndroidManifest.xml`: Declara as intenções de HOME, permissões de leitura de aplicativos, uso de widgets e papéis de parede.
*   `app/src/main/java/com/teamos/launcher/`:
    *   `LauncherActivity.java`: Atividade principal unificada. Gerencia o Workspace, o sliding panel da Gaveta de Apps, as animações e os gestos de toque.
    *   `Workspace.java` e `CellLayout.java`: Controlam as páginas da tela inicial em grade 4x5 ou 5x5, permitindo gerenciar ícones e widgets dinamicamente.
    *   `BubbleTextView.java`: Desenha o ícone e rótulo do app, gerando dinamicamente molduras hexagonais, squircles ou círculos com efeito glow ao redor dos ícones tradicionais do Android.
    *   `LauncherModel.java`: Carrega de forma assíncrona os aplicativos instalados usando o `PackageManager` e monitora adições/remoções em tempo real.
    *   `LauncherProvider.java` / `LauncherDatabaseHelper.java`: Banco de dados SQLite local para salvar o layout de atalhos e widgets na tela.
    *   `AppWidgetHostManager.java`: Facilita a vinculação e exibição de widgets Android reais de outros aplicativos na tela inicial.
    *   `SettingsActivity.java`: Tela de configurações dedicada do Aero Launcher, permitindo ao usuário alterar o tamanho da grade, formato dos ícones, habilitar/desabilitar brilhos visuais e alternar temas.
    *   `SwipeGestureDetector.java`: Detector de gestos personalizado para deslizar para cima (App Drawer), deslizar para baixo (painel de notificações) e toque duplo.
*   `app/src/main/res/`:
    *   `layout/activity_launcher.xml` e `layout/activity_settings.xml`: Layouts principais com componentes visuais modernos.
    *   `drawable/`: Vetores de máscaras de ícones (hexagon, squircle, circle) e gradientes/efeitos glassmorphic de vidro.
    *   `values/colors.xml` e `values/styles.xml`: Paleta futurista neon, estilos unificados e suporte a Dark Mode completo.

Esta arquitetura garante que o **Aero Launcher** seja incrivelmente rápido, consuma pouca memória, seja totalmente compatível com o ecossistema Android e proporcione a experiência definitiva e exclusiva do **TeamOS 3.0**.
