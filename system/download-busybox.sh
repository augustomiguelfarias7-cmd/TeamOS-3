#!/bin/bash
set -e

# TeamOS 3.0 - Baixa o BusyBox (userspace mínimo real)
# BusyBox fornece os comandos básicos do sistema (ls, cp, sh, etc.)

BUSYBOX_VERSION="1.36.1"
DOWNLOAD_URL="https://busybox.net/downloads/busybox-${BUSYBOX_VERSION}.tar.bz2"
TARGET_DIR="busybox"

echo "=== TeamOS 3.0 - Download do BusyBox ==="
echo "Versão: ${BUSYBOX_VERSION}"

if [ -d "${TARGET_DIR}" ]; then
    echo "A pasta ${TARGET_DIR} já existe."
    read -p "Deseja apagar e baixar novamente? (s/N): " resposta
    if [[ "$resposta" =~ ^[Ss]$ ]]; then
        rm -rf "${TARGET_DIR}"
        rm -f "busybox-${BUSYBOX_VERSION}.tar.bz2"
    else
        echo "Abortado."
        exit 0
    fi
fi

echo "Baixando BusyBox..."
wget -c "${DOWNLOAD_URL}" -O "busybox-${BUSYBOX_VERSION}.tar.bz2"

echo "Extraindo..."
tar -xjf "busybox-${BUSYBOX_VERSION}.tar.bz2"
mv "busybox-${BUSYBOX_VERSION}" "${TARGET_DIR}"

echo ""
echo "BusyBox baixado com sucesso em: ./${TARGET_DIR}"
echo "Para configurar e compilar:"
echo "  cd busybox"
echo "  make defconfig"
echo "  make -j\$(nproc)"
echo "  make install"
