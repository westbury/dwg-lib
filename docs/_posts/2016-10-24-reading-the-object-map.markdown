---
layout: post
title:  "Accessing the Object Map"
date:   2016-10-24 22:03:33 +0100
---
With this commit we now read some of the objects as referenced from the header, and get to other objects further down the trees.

I always wondered why handles and strings were put into separate streams.  Whatever the reason, it has actually made life a little easier because we can read all the handles reliably from the handles stream even when we have have had problems reading to the end of the data stream.  The documentation for the layout of some of the object types does appear to be not quite right.  For example the 'Alignment' field in object type 57 (documented in 19.4.56) is an RC, always 'A', yet it was not 'A' in the file used in the unit tests.  Nor can we read the preceeding patternLen field.  Interestingly, a bit scan of the buffer does find an 'A' (0x41) but it appears earlier in the buffer than I would expect, so some hacking around is needed.  It does seem that the specification is becoming rather less precise as we move deeper into the file, which is why progress is slowing.
