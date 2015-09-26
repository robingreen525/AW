JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	../org/fhcrc/honeycomb/mc/MCImage.java \
	../org/fhcrc/honeycomb/mc/MCSperiment.java \
	../org/fhcrc/honeycomb/utils/Write.java \
	../org/fhcrc/honeycomb/utils/ij/MaskInfo.java \
	../org/fhcrc/honeycomb/utils/ij/AWImagePlus.java \
	CellFinder_.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
