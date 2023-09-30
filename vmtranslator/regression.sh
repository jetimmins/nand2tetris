#!/usr/bin/env bash

set -euo pipefail

REPO=$1
TESTS=()
readarray -d $'\0' TESTS< <(find "$REPO"/projects/08 "$REPO"/projects/07 -type f -regex ".*[^VME]\.tst" -print0)

function translate() {
    for dir in $(dirname "${TESTS[@]}"); do
      if [[ $dir == *"FunctionCalls"* && $dir != *"SimpleFunction"* ]]; then
        java -jar ./target/vmtranslator-1.0-shaded.jar "$dir"
        printf "Translating %-18s with bootstrap code\n" "$(basename "$dir")"
      else
        java -jar ./target/vmtranslator-1.0-shaded.jar "$dir" --no-bootstrap
        printf "Translating %s\n" "$(basename "$dir")"
      fi
    done
}

function regression() {
  declare -i failures=0

  for tst in "${TESTS[@]}"; do
    set +e
    result=$("$REPO"/tools/CPUEmulator.sh "$tst" 2> err.log)
    result+=$(cat err.log) && rm err.log
    printf "%-30s %s\n" "$(basename "$tst")" "$result"
    if [[ $result != "End of script"* ]]; then ((failures++)); fi
    set -e
  done

  echo "${#TESTS[@]} tests were run, $failures failures."
  if [ "$failures" -ne 0 ]; then exit 1; fi
}

translate
regression
