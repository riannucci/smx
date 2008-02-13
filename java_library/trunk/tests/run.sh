#!/bin/bash

for dir in tests/[1234567]*
do
   if [ $dir == '[1234567]*' ] 
   then
      exit 1 
   fi
   echo ------ $dir ------
   for tf in $dir/*.in
   do 
      if [ $tf == $dir'/*.in' ] 
      then
         continue
      fi
      echo $tf | tee -a tests/log
      java -cp classes:libs/antlr-runtime-3.0.1.jar:libs/xerces.jar:libs/antlr-3.0.1.jar:libs/stringtemplate-3.1b1.jar:libs/antlr-2.7.7.jar test < $tf > tests/out 2>> tests/log
      if [ $? -ne 0 ]
      then
         exit 2
      fi
      diff $dir/$(basename $tf .in).out tests/out
      if [ $? -ne 0 ]
      then
         exit 3
      fi
      rm tests/out
   done
done
