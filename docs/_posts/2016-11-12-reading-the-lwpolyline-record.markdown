---
layout: post
title:  "Reading the LWPOLYLINE Record"
date:   2016-11-13 8:17:51 +0100
---
Mostly the entity object records have been well documented in the specification, with only small errors that could be solved with little more than looking at the bits and hacking around until it worked.

The LwPolyline, however, was not documented at all.  Furthermore this is an object that we really do want to be able to read.  A few techniques have proved helpful in cracking this one.  A simple thing to do is to loop through every bit offset, outputing the field assuming RD, BS and other bit field formats.  Comparing these to the known values (easiest by looking at the DXF export of the file).  If one knows a double value must be there but can't see it then look for a DD value.  The bit flags are the hardest to figure out, especially if the test files do not cover all uses, but unknown flags are not a show-stopper.  One gets a feel for this after a while, for example, if an integer value is in RC format, look at the previous two bits and consider that it may be in BS format.  Offsets jumping around give clues to this too.  And it is very helpful to separate out interesting records into a table that shows instant feed back as the code is changed.
