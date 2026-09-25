all: SporeUplift.jar

SporeUplift.jar: $(wildcard src/name/bizna/sporeuplift/* src/*.properties)
	javac --release 8 -s classic/src -d obj -encoding UTF-8 src/name/bizna/sporeuplift/*.java
	cp -v src/name/bizna/sporeuplift/*.txt obj/name/bizna/sporeuplift/
	cp -v src/*.properties obj/
	jar --create --file $@ --main-class name.bizna.sporeuplift.SporeUplift -C obj . 

clean:
	rm -rf obj/ SporeUplift.jar

.PHONY: all clean
.SECONDARY:
MAKEFLAGS += --no-builtin-rules