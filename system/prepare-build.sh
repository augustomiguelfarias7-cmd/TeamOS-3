#!/bin/bash
set -e

# TeamOS 3.0 - Prepara o ambiente de build real
# Instala dependências necessárias para compilar o kernel Linux

echo "=== TeamOS 3.0 - Preparação do ambiente de build ==="

if [ ! -d "linux" ]; then
    echo "Erro: pasta 'linux' não encontrada."
    echo "Execute primeiro: ./download-kernel.sh"
    exit 1
fi

echo "Detectando distribuição..."
if [ -f /etc/os-release ]; then
    . /etc/os-release
    DISTRO=$ID
else
    echo "Não foi possível detectar a distribuição."
    exit 1
fi

echo "Distribuição detectada: $DISTRO"

case $DISTRO in
    ubuntu|debian|linuxmint|pop)
        echo "Instalando dependências (Debian/Ubuntu)..."
        sudo apt update
        sudo apt install -y build-essential libncurses-dev bison flex libssl-dev libelf-dev bc dwarves openssl
        ;;
    fedora|rhel|centos)
        echo "Instalando dependências (Fedora/RHEL)..."
        sudo dnf install -y gcc make ncurses-devel bison flex elfutils-libelf-devel openssl-devel bc
        ;;
    arch|manjaro)
        echo "Instalando dependências (Arch)..."
        sudo pacman -Syu --noconfirm base-devel ncurses bison flex openssl bc
        ;;
    *)
        echo "Distribuição não suportada automaticamente: $DISTRO"
        echo "Instale manualmente: gcc, make, bison, flex, libssl-dev, libelf-dev, bc, ncurses"
        exit 1
        ;;
esac

echo ""
echo "Ambiente preparado com sucesso."
echo "Agora você pode entrar na pasta do kernel e compilar:"
echo "  cd linux"
echo "  make defconfig          # configuração padrão"
echo "  # ou: make menuconfig   # configuração interativa"
echo "  make -j\$(nproc)"
