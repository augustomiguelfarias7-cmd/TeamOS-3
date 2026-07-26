#!/bin/bash
set -e

# TeamOS 3.0 - Script principal de build
# Orquestra o download e preparação da infraestrutura Linux

echo "=========================================="
echo "   TeamOS 3.0 - Build System"
echo "=========================================="
echo ""

# Torna os scripts executáveis
chmod +x download-kernel.sh prepare-build.sh download-busybox.sh 2>/dev/null || true

echo "[1/3] Baixando Kernel Linux..."
./download-kernel.sh

echo ""
echo "[2/3] Baixando BusyBox (userspace mínimo)..."
./download-busybox.sh

echo ""
echo "[3/3] Preparando ambiente de compilação..."
./prepare-build.sh

echo ""
echo "=========================================="
echo " Preparação concluída!"
echo "=========================================="
echo ""
echo "Próximos passos manuais:"
echo "  1. cd linux && make defconfig && make -j\$(nproc)"
echo "  2. cd ../busybox && make defconfig && make -j\$(nproc) && make install"
echo ""
echo "Depois disso você terá o kernel e o userspace mínimo prontos"
echo "para continuar o desenvolvimento do TeamOS 3.0."
