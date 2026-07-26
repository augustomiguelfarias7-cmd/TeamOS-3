#!/bin/bash
set -e

# TeamOS 3.0 - Baixa o Firefox (100% open source)
# O Firefox será o navegador principal do sistema

echo "=== TeamOS Navigator - Download do Firefox ==="

# Detecta arquitetura
ARCH=$(uname -m)
case $ARCH in
    x86_64)  FF_ARCH="linux-x86_64" ;;
    aarch64) FF_ARCH="linux-aarch64" ;;
    armv7l)  FF_ARCH="linux-armv7l" ;;
    *)       echo "Arquitetura não suportada: $ARCH"; exit 1 ;;
esac

# Versão estável (atualize conforme necessário)
FIREFOX_VERSION="128.0"
DOWNLOAD_URL="https://download-installer.cdn.mozilla.net/pub/firefox/releases/${FIREFOX_VERSION}/${FF_ARCH}/pt-BR/firefox-${FIREFOX_VERSION}.tar.bz2"

TARGET_DIR="firefox"

if [ -d "$TARGET_DIR" ]; then
    echo "Firefox já existe em ./$TARGET_DIR"
    read -p "Baixar novamente? (s/N): " r
    if [[ ! "$r" =~ ^[Ss]$ ]]; then
        exit 0
    fi
    rm -rf "$TARGET_DIR"
fi

echo "Baixando Firefox ${FIREFOX_VERSION} (${FF_ARCH})..."
wget -c "$DOWNLOAD_URL" -O firefox.tar.bz2

echo "Extraindo..."
tar -xjf firefox.tar.bz2
# O tarball já cria a pasta firefox/

echo ""
echo "Firefox baixado com sucesso em: ./firefox"
echo "Execute com: ./firefox/firefox"
