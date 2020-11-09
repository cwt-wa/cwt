#!/bin/bash

# Download replay files from CWT Binary Store and reprocess with local WAaaS instance.
# Right now this returns SQL to update the texture column of game_stats

# These could be arguments
ids=('1602' '1606' '1607' '1608' '1611' '1616' '1621' '1626' '1627' '1631' '1632' '1633' '1634' '1636' '1637' '1638' '1639' '1641' '1642' '1643' '1644' '1645' '1646' '1647' '1651' '1656' '1657' '1658' '1659' '1660' '1661' '1662' '1666' '1671' '1672' '1673' '1674' '1675' '1676' '1678' '1679' '1680' '1681' '1682')

for id in "${ids[@]}"; do
  echo $id
  wget -O ${id}.zip --wait=5 --limit-rate=50K "http://cwt-binary.normalnonoobs.com/api/game/${id}/replay"
  unzip ${id}.zip -d $id # I did not test this 
done

count=1
for game in */; do
  for i in $game*game; do
    echo $i
    mv "$i" "$game$count.WAgame"
    ((count++))
  done
done

for game in */; do
  echo "game: $game"
  for i in $game*.WAgame; do
      gameId="$(echo "$game" | gsed 's/\///g')"
      json="$(curl -X POST 'http://localhost:8000' -F "replay=@${i}" | json_pp)"
      terrain="$(echo "$json" | gsed -rn 's/[^\S]*"texture" : "(.*)",?/\1/p')"
      startedAt="$(echo "$json" | gsed -rn 's/[^\S]*"startedAt" : "(.*)",?/\1/p' | gsed -r 's/(.*) GMT/\1/')"
      echo "$terrain"
      echo "$startedAt"
      echo "SQL: update game_stats set texture='${terrain}' where game_id=${gameId} and startedAt='${startedAt}';"
      sleep 1s
  done
done

