This library offers a set of image transformation tools to enhance neural network training.

Two kind of objects can be created: **RGB Holders**, **Iconoclastic Layers**

**== RGBHolder ==**

An image can be assigned to the RGBHolder directly from a file or a BufferedImage, then you can take advantage of a set of functions: 

resize

invert colours

get&set R,G,B matrices independently

standardise (Z values)

printOnFile



**== Iconoclastic Layer ==**

Allows to break the image in N^2 sub-sections and perform different operations in each section.

-  local standardisation
-  Get top N sections with the greatest colour contrast (features)
-  get average RGB value in each section

**GET FEATURES**

getNfeatures( N, height, width, filepath)

The getNfeatures() function selects the top N areas with highest colour contrast (leaving out all sections with uniform colour), then tries to optimise each section by enlarging or shrinking it. Since the optimisation process returns sections of different size, they will all be resized to a standard dimension specified in the function input. Finally it will return all the areas as an array of RGBHolders of length N.Each element can be used again as input for another layer.

Filepath is optional, you can print all features in a subfolder for debugging purposes. If printing on files is not required, set the path to null.


![1](https://user-images.githubusercontent.com/21087227/40578829-283e0f72-6113-11e8-8e22-3bbf2a64caaf.JPG)
