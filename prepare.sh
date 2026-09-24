vsfix
echo "" >> /etc/profile
echo "alias s='./gradlew spotlessApply --offline'" >> /etc/profile
echo "alias b='./gradlew build'" >> /etc/profile
echo "alias run='./gradlew runServer25'" >> /etc/profile

mv /tmp/repo/.gradle ./.gradle
mkdir -p ./build
mv /tmp/repo/build/* ./build
mkdir -p ./run/natives
mv /tmp/repo/run/natives/* ./run/natives

mkdir -p ~/.gradle/init.d/
cp tools/cnb-mirror.gradle ~/.gradle/init.d/cnb-mirror.gradle

wget https://cnb.cool/Cool_Sapphire/file/-/lfs/5384d9e1e6f8b155bb96ebb36e762e2a84cb0bf48a52d86a429f253d8370bbdd?name=jbrsdk_jcef-25.0.2-linux-x64-b300.57.tar.gz -O jbrsdk_jcef-25.0.2-linux-x64-b300.57.tar.gz

tar -zxvf jbrsdk_jcef-25.0.2-linux-x64-b300.57.tar.gz
mkdir -p /usr/lib/jvm/jbr25/
mv jbrsdk_jcef-25.0.2-linux-x64-b300.57/* /usr/lib/jvm/jbr25/
rm -rf /workspace/jbrsdk_jcef-25.0.2-linux-x64-b300.57/
rm -rf /workspace/jbrsdk_jcef-25.0.2-linux-x64-b300.57.tar.gz