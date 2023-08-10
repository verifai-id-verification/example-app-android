#!/bin/bash

LICENSE_STR=$1

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    exit 1
fi

TMP_STR="verifaiLicense="
while IFS= read -r line; do
    TMP_STR="$TMP_STR\"$line\\\\\\\\n\" +\\\\\n\\"
done <<< "$LICENSE_STR"
TMP_STR=${TMP_STR::-13}
echo ""
printf "$TMP_STR\""
echo ""
