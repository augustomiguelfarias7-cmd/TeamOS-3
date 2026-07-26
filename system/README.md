# System - Build do TeamOS 3.0

Esta pasta contém os scripts reais de build do TeamOS 3.0.

A ideia é baixar a **parte mais difícil** da infraestrutura Linux (kernel, firmware/drivers, componentes de baixo nível e alguns pacotes pesados).  
A parte mais fácil (interface final, loja WebView, ChatGusto, etc.) será desenvolvida de forma independente pelo TeamOS.

## O que os scripts baixam

- Kernel Linux (oficial)
- Linux Firmware (drivers de hardware)
- BusyBox (userspace mínimo)
- PCManFM (gerenciador de arquivos leve)
- Componentes base de sistema

## Requisitos

- Linux (Ubuntu/Debian recomendado)
- `wget`, `git`, `make`, `gcc` e dependências de compilação

## Como usar

```bash
cd system
chmod +x *.sh
./build.sh
```

## Estrutura dos scripts

- `build.sh` → Script principal (roda tudo)
- `download-kernel.sh` → Kernel Linux
- `download-firmware.sh` → Firmware/drivers
- `download-busybox.sh` → BusyBox
- `download-filemanager.sh` → Gerenciador de arquivos (PCManFM)
- `prepare-build.sh` → Instala dependências de compilação
