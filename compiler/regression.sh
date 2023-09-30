#!/usr/bin/env bash

set -euo pipefail

REPO=$1
SOURCES=()
TESTS=()
readarray -d $'\0' SOURCES< <(find "$REPO/projects/10" "$REPO/projects/11" -type f -name "*.jack" -print0)
readarray -d $'\0' TESTS< <(find "$REPO/projects/10" "$REPO/projects/11" -maxdepth 2 -type f -regex ".*xml" -print0)

function compile() {
  local src
  local chapter
  local srcfile
  local dir

    for src in "${SOURCES[@]}"; do
      chapter=$(echo "$src" | awk -v FS='/' '{print $(NF-2)}')
      srcfile=$(basename "$src")
      dir=$(dirname "$src")
      if [[ $chapter == "10" ]]; then
        compile-chapter "$srcfile" "$dir" "--tokenise" "Tokenised to XML"
        compile-chapter "$srcfile" "$dir" "--xml" "Compiled to XML"
      else
        cp "$REPO"/tools/OS/*.vm "$dir"
        compile-chapter "$srcfile" "$dir" "--target $dir" "Compiled to VM and copied OS files"
      fi
    done
}

function compile-chapter() {
  srcfile=$1
  dir=$2
  flags=$3
  message=$4
  local flagarr
  read -ra flagarr <<<"${flags}"
  java -jar ./target/compiler-1.0-shaded.jar "$dir" "${flagarr[@]}"
  printf "%-40s%s\n" "$(basename "$dir")/$srcfile" "$message"
}

function regression() {
  declare -i failures=0
  local tst
  local target
  local result

  for tst in "${TESTS[@]}"; do
    target="$(dirname "$tst")/target/$(basename "$tst")"
    set +e
    result=$("$REPO"/tools/TextComparer.sh "$tst" "$target" 2> err.log)
    result+=$(cat err.log) && rm err.log
    printf "%-40s%s\n" "$(echo "$tst" | awk -v FS='/' '{print $(NF-1)"/"$(NF)}')" "$result"
    if [[ $result != "Comparison ended successfully" ]]; then ((failures++)); fi
    set -e
  done

  echo "${#TESTS[@]} tests were run, $failures failures."
  echo "Note that chapter 11 must be tested interactively with the VMEmulator according to the text."
  if [ "$failures" -ne 0 ]; then exit 1; fi
}

compile
regression
