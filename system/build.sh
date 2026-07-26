#!/bin/bash
set -e

# TeamOS 3.0 - Script principal de build
# Baixa a parte mais difícil da infraestrutura Linux
# (kernel + firmware/drivers + busybox + gerenciador de arquivos)

echo "=========================================="
echo "   TeamOS 3.0 - Build System"
echo "=========================================="
echo ""
echo "Baixando a metade difícil da infraestrutura..."
echo "A parte mais fácil será feita de forma independente."
echo ""

# Torna os scripts executáveis
chmod +x download-kernel.sh download-firmware.sh download-busybox.sh download-filemanager.sh prepare-build.sh 2>/dev/null || true

echo "[1/5] Baixando Kernel Linux..."
./download-kernel.sh

echo ""
echo "[2/5] Baixando Linux Firmware (drivers de hardware)..."
./download-firmware.sh

echo ""
echo "[3/5] Baixando BusyBox (userspace mínimo)..."
./download-busybox.sh

echo ""
echo "[4/5] Baixando Gerenciador de Arquivos (PCManFM)..."
./download-filemanager.sh

echo ""
echo "[5/5] Preparando ambiente de compilação..."
./prepare-build.sh

echo ""
echo "=========================================="
echo " Preparação da infraestrutura concluída!"
echo "=========================================="
echo ""
echo "Componentes baixados (parte difícil):"
echo "  - Kernel Linux"
echo "  - Firmware / Drivers"
echo "  - BusyBox"
echo "  - Gerenciador de arquivos (PCManFM)"
echo ""
echo "Próximos passos:"
echo "  1. Compilar o kernel (cd linux && make defconfig && make -j\$(nproc))"
echo "  2. Compilar o BusyBox"
echo "  3. Continuar o desenvolvimento da parte independente do TeamOS"
