---
layout: post
title:  "Welcome to dwg-lib!"
date:   2016-09-26 22:59:35 +0100
---
I've decided to write a reader for the DWG CAD drawing file format.
It is called dwg-lib, as that name also makes a sensible Maven artifact id, and the source is on Github.

I anticipate that there will be many challenges along the way, not least
because Autodesk have gone to some effort to obfuscate the format.

Fortunately the Open Design Alliance (ODA) have published the format specification.  I would not even have attempted this project had the
specification not been available.

I have made my first commit.  It reads the file version (well, one's got to start somewhere).  Looking at the specification it seems the format changed for the worse between 2004 and 2007, but then 2010 was more similar to 2004.  On that basis I will probably start with the 2010 format.
