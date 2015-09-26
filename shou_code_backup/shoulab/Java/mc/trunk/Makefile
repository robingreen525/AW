JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	MCImage.java \
	MCSperiment.java \
	MCSperimentTest.java \
	MCImageTest.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
