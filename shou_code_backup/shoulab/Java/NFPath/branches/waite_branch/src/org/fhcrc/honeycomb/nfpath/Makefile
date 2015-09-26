JFLAGS = -g
JC = javac
TESTING = test-files

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	NFObject.java \
	NFPath.java \
	$(TESTING)/TestNFObject.java \
	$(TESTING)/testNFPath.java \


default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class $(TESTING)/*.class

docs:
	javadoc -sourcepath ../../../../../src org.fhcrc.honeycomb.nfpath -d ../../../../../doc
