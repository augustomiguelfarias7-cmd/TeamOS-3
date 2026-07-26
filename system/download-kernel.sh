#!/bin/bash
set -e

# TeamOS 3.0 - Script real para baixar o kernel Linux oficial
# Baixa a versão estável mais recente do kernel.org

KERNEL_VERSION="6.12.40"   # Atualize este valor quando quiser uma versão mais nova
KERNEL_MAJOR="6.12"
DOWNLOAD_URL="https://cdn.kernel.org/pub/linux/kernel/v6.x/linux-${KERNEL_VERSION}.tar.xz"
TARGET_DIR="linux"

echo "=== TeamOS 3.0 - Download do Kernel Linux ==="
echo "Versão: ${KERNEL_VERSION}"
echo "URL: ${DOWNLOAD_URL}"
echo ""

if [ -d "${TARGET_DIR}" ]; then
    echo "A pasta ${TARGET_DIR} já existe."
    read -p "Deseja apagar e baixar novamente? (s/N): " resposta
    if [[ "$resposta" =~ ^[Ss]$ ]]; then
        rm -rf "${TARGET_DIR}"
        rm -f "linux-${KERNEL_VERSION}.tar.xz"
    else
        echo "Abortado."
        exit 0
    fi
fi

echo "Baixando kernel..."
wget -c "${DOWNLOAD_URL}" -O "linux-${KERNEL_VERSION}.tar.xz"

echo "Extraindo..."
tar -xf "linux-${KERNEL_VERSION}.tar.xz"
mv "linux-${KERNEL_VERSION}" "${TARGET_DIR}"

echo ""
echo "Kernel baixado e extraído com sucesso em: ./${TARGET_DIR}"
echo "Próximo passo: ./prepare-build.sh"
