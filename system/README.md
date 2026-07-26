# System - Build do TeamOS 3.0

Esta pasta contém os scripts reais de build do TeamOS 3.0.

Os scripts baixam e preparam a infraestrutura do Linux (kernel + componentes básicos) a partir das fontes oficiais.

## Requisitos

- Sistema Linux (Ubuntu/Debian recomendado)
- `wget` ou `curl`
- `git`
- `make`, `gcc`, `bc`, `flex`, `bison`, `libssl-dev`, `libelf-dev` (para compilar o kernel)

## Como usar

```bash
cd system
chmod +x *.sh
./download-kernel.sh
./prepare-build.sh
```

Depois de baixar, você pode compilar o kernel com:

```bash
cd linux
make menuconfig   # ou defconfig
make -j$(nproc)
```

## Estrutura

- `download-kernel.sh` → Baixa o kernel Linux oficial
- `prepare-build.sh` → Prepara o ambiente e dependências básicas
- `build-rootfs.sh` → (futuro) Cria o sistema de arquivos raiz mínimo
