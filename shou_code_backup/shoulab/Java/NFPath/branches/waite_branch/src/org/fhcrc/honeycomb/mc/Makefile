JFLAGS = -g
JC = javac
TESTING = test-files

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	MCImage.java \
	MCSperiment.java \
	$(TESTING)/testMCImage.java \
	$(TESTING)/testMCSperiment.java \


default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class $(TESTING)/*.class

docs:
	javadoc -sourcepath ../../../../../src org.fhcrc.honeycomb.mc -d ../../../../../doc
