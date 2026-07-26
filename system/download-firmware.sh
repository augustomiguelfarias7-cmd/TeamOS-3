#!/bin/bash
set -e

# TeamOS 3.0 - Baixa o Linux Firmware (drivers de hardware)
# Contém firmwares para Wi-Fi, GPU, som, rede, etc.

FIRMWARE_REPO="https://git.kernel.org/pub/scm/linux/kernel/git/firmware/linux-firmware.git"
TARGET_DIR="linux-firmware"

echo "=== TeamOS 3.0 - Download do Linux Firmware (drivers) ==="

if [ -d "${TARGET_DIR}" ]; then
    echo "A pasta ${TARGET_DIR} já existe."
    read -p "Deseja atualizar com git pull? (s/N): " resposta
    if [[ "$resposta" =~ ^[Ss]$ ]]; then
        cd "${TARGET_DIR}"
        git pull
        cd ..
        echo "Firmware atualizado."
        exit 0
    else
        echo "Abortado."
        exit 0
    fi
fi

echo "Clonando repositório oficial de firmware (pode demorar)..."
git clone --depth 1 "${FIRMWARE_REPO}" "${TARGET_DIR}"

echo ""
echo "Linux Firmware baixado com sucesso em: ./${TARGET_DIR}"
echo "Esta é a parte difícil dos drivers de hardware."
