# jMDFLib ![Build Status](http://wollekuel.spdns.de:8080/buildStatus/icon?job=jmdflib)

I'm thinking about doing a re-implementation of [jMDFLib](https://sourceforge.net/projects/jmdflib/).

I'm using Vector's specification document for version 3.1.1, accessible from their [homepage](https://vector.com/downloads/mdf_specification.pdf). Unfortunately, no newer specification files are available...

## Status

### Implemented block types

* IDBlock
* HDBlock
* TXBlock
* DGBlock

#### Not tested

* More than one DGBlock

### Missing block types

* All other

## Missing implementations

* Files lenghts greater or equal than `Integer.MAX_VALUE`
* Unfinalized MDF files
* Big Endian byte order
* Floating-point format compliant with G_Float or D_Float
* PRBlock
* Number of Record IDs > 0