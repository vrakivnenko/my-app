#!/bin/bash 
echo $(expr "$RANDOM" % 2)
exit $(expr "$RANDOM" % 2)


