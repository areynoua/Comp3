cd ../src/

for file in ../test/*.imp;
do
	f=${file##*/};
	echo test $f
	if ! make ${f%%.imp};
	then
		diff -y /tmp/out.out ${file%%.imp}.out
		echo
		echo compare with
		echo vimdiff /tmp/out.out ${file%%.imp}.out
		echo vim -o $file ${file%%.imp}.in ${file%%.imp}.out
		break;
	fi;
	echo
	echo --------------------
	echo
done
