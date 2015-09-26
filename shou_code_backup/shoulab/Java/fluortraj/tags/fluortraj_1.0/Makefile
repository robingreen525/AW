JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	../mc/src/org/fhcrc/honeycomb/mc/MCImage.java \
	../mc/src/org/fhcrc/honeycomb/mc/MCSperiment.java \
	Background.java \
	FluorTraj.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
