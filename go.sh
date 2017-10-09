if type -p java; then
    echo found java executable in PATH
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo found java executable in JAVA_HOME     
    _java="$JAVA_HOME/bin/java"
else
    echo "No java found. This application requires java 1.8"
fi

if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ "$version" > "1.5" ]]; then
	echo compiling files...
	javac -cp dependencies/json-simple-1.1.1.jar src/com/atishaygoyal/sortablechallenge/*.java
	echo creating executable jar...
	cd src/
	jar -cfm ../Sortable.jar ../MANIFEST.MF com
	echo executing application..
	cd ..
	java -jar Sortable.jar
    else         
        echo java version is less than 1.6
    fi
fi
