sudo wget https://raw.githubusercontent.com/Prism-Launcher-for-Debian/repo/refs/heads/repo/prismlauncher.gpg -O /usr/share/keyrings/prismlauncher-archive-keyring.gpg \
  && echo "deb [signed-by=/usr/share/keyrings/prismlauncher-archive-keyring.gpg] https://raw.githubusercontent.com/Prism-Launcher-for-Debian/repo/refs/heads/repo $(. /etc/os-release; echo "${UBUNTU_CODENAME:-${DEBIAN_CODENAME:-${VERSION_CODENAME}}}") main" | sudo tee /etc/apt/sources.list.d/prismlauncher.list \
  && sudo apt update \
  && sudo apt install prismlauncher -y

cd ~
# wget https://cnb.cool/Cool_Sapphire/file/-/releases/download/2.8.4/GT_New_Horizons_2.8.4_Java_17-25.zip
wget https://cnb.cool/Cool_Sapphire/file/-/releases/download/2.9.0/GT_New_Horizons_2.9.0-beta-2_Java_17-25.zip

# 压缩dump
# 7z a -t7z -m0=lzma2 -mx=9 -mfb=273 -md=512m -ms=on -mmt=on -mtc=off -mta=off /workspace/dumps.7z /root/.local/share/PrismLauncher/instances/GT_New_Horizons_2.8.4_Java_17-25/.minecraft/dumps/