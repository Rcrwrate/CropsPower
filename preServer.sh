set -e

rm -rf "/tmp/GTNH"
mkdir -p "/tmp/GTNH"
cd "/tmp/GTNH"
git init -q -b main
git remote add origin "https://cnb:${CNB_TOKEN}@cnb.cool/shirokasoke/GTNH"

git sparse-checkout init --cone
git sparse-checkout set 2.9.0-beta3

# 浅克隆，LFS 只取需要的范围（先排除 World，最后一次拉全，避免 LFS 二次扫描）
git fetch --depth=1 origin main

git config lfs.fetchinclude "2.9.0-beta3/*"
GIT_LFS_SKIP_SMUDGE=0 git checkout -f FETCH_HEAD
# git lfs pull

rm -rf /workspace/GTNH-server
mv "/tmp/GTNH/2.9.0-beta3" /workspace/GTNH-server
rm -rf "/tmp/GTNH"

cd /tmp
wget https://cnb.cool/shirokasoke/McWebAPI/-/releases/download/2.9.0-beta2-0.11-pre/290beta2-915.dump.7z
7z x 290beta2-915.dump.7z -o/workspace/GTNH-server

echo '/usr/lib/jvm/zulu25-ca-amd64/bin/java -Xms6G -Xmx32G -Dmixin.debug.export=true -Dfml.readTimeout=180 @java9args.txt -jar lwjgl3ify-forgePatches.jar nogui' > /workspace/GTNH-server/startserver-java9.sh
# cat ./GTNH-server/logs/fml-junk-earlystartup.log | grep -oE '[0-9]+ms' | sort | uniq -c | sort -rn