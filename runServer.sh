set -e

# git pack-refs --all
# git gc --prune=now

./gradlew spotlessApply

rm -rf /workspace/build/libs

./gradlew build

# 处理 git tag 引用异常导致 jar 嵌套在子目录的情况
# (如 build/libs/webapi-refs/tags/*.jar，由 git describe 返回 refs/tags/... 引起)
# 将子目录中的 jar 上移到 build/libs/ 顶层，并清理空目录
if find /workspace/build/libs -mindepth 1 -type d | grep -q .; then
    find /workspace/build/libs -mindepth 2 -type f -name "*.jar" -exec mv -f {} /workspace/build/libs/ \;
    find /workspace/build/libs -mindepth 1 -type d -delete 2>/dev/null || true
    echo "[runServer] flatten: nested jar dirs detected, jars moved to build/libs/"
fi

target=$(ls /workspace/build/libs/ | grep -vE "(dev|sources|preshadow).jar")

ls /workspace/GTNH-server/mods/ | grep cropspower | while read line
do
    echo "$line"
    rm "/workspace/GTNH-server/mods/$line"
done

cp "/workspace/build/libs/$target" "/workspace/GTNH-server/mods/$target"

cd /workspace/GTNH-server
bash startserver-java9.sh

