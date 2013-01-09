org.obj2openjl
==============

A Java implementation of the infamous Obj2OpenGL python script. It lets you load Wavefront Object files (.obj) and convert them to arrays of indices, vertices, normals and texture coordinates as they are required by Kronos OpenGL libraries.

The project [AugmentedBizz](https://github.com/Vladee/com.server.AugmentedBizz) is a sample project in which the library has been used. The AugmentedBizz server loads OBJ model data into some cloud storage, from which the corresponding Android client loads the models in order to display them in augmented reality scenarios.


Use ``Obj2OpenJL`` like so:

```
RawOpenGLModel openGLModel = new Obj2OpenJL().convert("path/to/file");
```

The path does not necessarily have a file ending. Obj2OpenJL automatically looks for files ending with .wvo and .obj, which are both the same file format, just with different file extensions. The ``RawOpenGLModel`` returned is special in that it contains the raw model data and allows applying transformations to it prior to using it in your application. For instance,

```
OpenGLModelData openGLModelData = openGLModel.normalize().center().getDataForGLDrawElements();
```

would shrink or expand the model vertices such that the longest edge of the model bounding box is of length 1 (``normalize()``) and translate all vertices so that the bounding box is centered in the coordinate system origin (``center()``). The instance returned by these calls is the same object for fluent interface, which means you could split the above call across multiple lines. Finally, ``getDataForGLDrawElements()`` converts the raw model to the data format you would need for the Kronos OpenGL C libraries.

There are also more complex operations you may run on the data, which result in duplication of the original vertices. For instance,

```
List<ArrayTransformation> transformations = new ArrayList<ArrayTransformation>();
transformations.add(new TranslationTransformation(2, 1.f, 0.f, 0.f));
transformations.add(new TranslationTransformation(2, 0.f, 1.f, 0.f));
transformations.add(new RotationTransformation(2, 360.f, 0.f, 0.f, 1.f));
CombineTransformation transformation = new CombineTransformation(transformations);
openGLModel.setTransformation(transformation);
```

produces a combined transformation that replicates vertices in the process. The model is translated and copied twice by 1.0 along the x-axis (``TranslationTransformation(2, 1.f, 0.f, 0.f)``), then the whole result is translated and copied twice by 1.0 along the y axis (``TranslationTransformation(2, 0.f, 1.f, 0.f)``). Finally, all these vertices are then rotated by 360 degrees in two steps along the arbitrary axis (0, 0, 1) (``RotationTransformation(2, 360.f, 0.f, 0.f, 1.f)``).

All that transformation is optional, however. The converted data can be retrieved from the ``OpenGLModelData`` instance via

```
openGLModelData.getVertices();
openGLModelData.getNormals();
openGLModelData.getTextureCoordinates();
openGLModelData.getIndices();
```

Note that the data returned is intended to be used with the GL_TRIANGLES rendering mode. The project ``org.obj2openjl.ui`` contains a simple Java2D viewer I used for testing which you may want to check out to get an impression if this library would work for you. It depends on this project, though, so that you will want to clone both and adjust the class plath dependency as necessary.
