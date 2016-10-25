---
layout: post
title:  "Accessing more of the Object Map"
date:   2016-10-24 16:43:13 +0100
---
A couple of break-throughs have been made with this commit.

When I made the last commit I was stuck reading the object type 57 record.  The field documented as 'xrefindex+1' was not giving back sensible values, nor were the following fields.  This might have got me very stuck.  However I was lucky that I found this problem in a type 57 record first and that record has an "Alignment" field being always an "A" (integer value 65).  I scanned the buffer at the bit level and found this value, 10 bits earlier than I expected, and just one bit after the 'xrefindex+1' was expected.  By advancing just one bit when 'xrefindex+1' is expected we get to the 'A', and the remaining fields read sensible values and takes us exactly to the end of the data stream.  I had the same problem in the VPORT record and likewise, when I removed the 'xrefindex+1' read, it read with sensible values exactly to the end of the data stream.

I don't know the details of this missing field, but as long as removing it fixes the problems then I will remove it, and if it needs to be read then I will include it, and maybe I will figure out when it should be in and when it should not be in.

The second break-through was caused by confusion over the EED data.  The specification indicates there is a length of the data followed by 'X' (the data).  However the reading of the fields failed after this whenever the EED length was not zero.  Now chapter 27 does explain the format of the EED with multiple records of length/handle/data, and that clearly states that the length is just the data length.  However after much hacking around, and looking for that tell-tale 0101 code for the application handle, it appears that the length given on page 98 (size of extended object data, if any) is in fact the first data length.  They should not have included the length field on page 98.
