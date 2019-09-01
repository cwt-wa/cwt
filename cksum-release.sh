#!/bin/bash

echo "Validating that dist static and JAR have equal checksums."

regex='s/^([0-9]+).*/\1/'


dist=$(cksum cwt-angular/target/dist/app*.js | sed -E "$regex" | head -n 1)
static=$(cksum cwt-spring/src/main/resources/static/app*.js | sed -E "$regex" | head -n 1)

unzip cwt-spring/target/cwt-spring*.jar -d cwt-spring/target/cksum-release-unzip > /dev/null

jar=$(cksum cwt-spring/target/cksum-release-unzip/BOOT-INF/classes/static/app*.js | sed -E "$regex" | head -n 1)

echo "Dist   $dist"
echo "Static $static"
echo "JAR    $jar"

