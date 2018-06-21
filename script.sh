#!/bin/bash 
va(){
	if   [ $(expr "$RANDOM" % "2") -eq "0" ];
	then 
		ca="0"
	else
		ca="2"
    fi
    return "$ca"
}
va
echo $ca