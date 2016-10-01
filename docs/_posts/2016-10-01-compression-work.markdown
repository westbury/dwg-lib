---
layout: post
title:  "Uncompressing the LZ77 compressed parts of the file"
date:   2016-10-01 11:21:03 +0100
---
If one tries to implement a reader for the DWG file format from ODA's specification, it won't take long before you find yourself reading a load of data that has been compressed.  Writing code to uncompress may seem daunting but actually the ODA have documented it very clearly and there is plenty of documentation on LZ77 compression.  The implementation is all in the Expander class.

Figuring out the uncompression has allowed us to read the section information.  The data look correct so uncompression is probably working correctly, though the test file actually only uses opcodes >= 0x40 so we don't have good code coverage.
