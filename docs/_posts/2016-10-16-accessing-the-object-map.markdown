---
layout: post
title:  "Accessing the Object Map"
date:   2016-10-16 10:46:59 +0100
---
As far as reading a DWG file is concerned, the data we are really trying to get to is in the objects in the objects section.  Once I had managed to read the sections, I could get to the objects section.  The problem was that objects in the objects section should not be read sequentially but are like trees with the roots of the trees in the header section.  The objects should be read starting at the roots.  That is why I worked on reading the header section in its entirety first.

I got rather stuck on trying to figure out the handles because some contain the offset of the object from the start of the objects section, whereas others give a relative offset from the location of another object.  It is not clear to me yet how one gets the object from which a relative handle is based.  However all the handles in the header that I have investigated contain offsets from the start of the objects section.  For example the CLAYER handle gets us to an object of type 51 (LAYER), and the CMATERIAL handle gets us to a class in the class list with a classdxfname of 'MATERIAL'.  I'm fairly confident that the way relative offsets work will become clear once I get to an object that uses such a handle.

