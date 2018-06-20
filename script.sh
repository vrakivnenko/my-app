#!/bin/bash 
va(){
	if   [ $(expr "$RANDOM" % "2") -eq "0" ];
	then 
		ca=0
	else
		ca=1
    fi
    return "$ca"
}
va