#!/bin/bash
errors=0
for i in $( find /Users/vrakivnenko/Documents -name "*.sh" );
do
    if shellcheck "$i";
    then
    :
    else
        (( errors++ ))
    fi
done
echo $errors