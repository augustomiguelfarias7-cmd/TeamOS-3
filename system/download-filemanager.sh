#!/bin/bash
set -e

# TeamOS 3.0 - Baixa o gerenciador de arquivos PCManFM
# PCManFM é leve e serve como base real de gerenciador de arquivos

PCMANFM_VERSION="1.3.2"
DOWNLOAD_URL="https://downloads.sourceforge.net/pcmanfm/pcmanfm-${PCMANFM_VERSION}.tar.xz"
TARGET_DIR="pcmanfm"

echo "=== TeamOS 3.0 - Download do Gerenciador de Arquivos (PCManFM) ==="

if [ -d "${TARGET_DIR}" ]; then
    echo "A pasta ${TARGET_DIR} já existe."
    read -p "Deseja apagar e baixar novamente? (s/N): " resposta
    if [[ "$resposta" =~ ^[Ss]$ ]]; then
        rm -rf "${TARGET_DIR}"
        rm -f "pcmanfm-${PCMANFM_VERSION}.tar.xz"
    else
        echo "Abortado."
        exit 0
    fi
fi

echo "Baixando PCManFM ${PCMANFM_VERSION}..."
wget -c "${DOWNLOAD_URL}" -O "pcmanfm-${PCMANFM_VERSION}.tar.xz" || {
    echo "Falha no download direto. Tentando via git..."
    git clone https://github.com/lxde/pcmanfm.git "${TARGET_DIR}"
    echo "PCManFM clonado via git."
    exit 0
}

echo "Extraindo..."
tar -xf "pcmanfm-${PCMANFM_VERSION}.tar.xz"
mv "pcmanfm-${PCMANFM_VERSION}" "${TARGET_DIR}"

echo ""
echo "Gerenciador de arquivos baixado em: ./${TARGET_DIR}"
echo "Esta é uma das partes de infraestrutura que o TeamOS vai usar como base."
