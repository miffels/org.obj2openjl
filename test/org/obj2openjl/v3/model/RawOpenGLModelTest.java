package org.obj2openjl.v3.model;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.obj2openjl.v3.Obj2OpenJL;
import org.obj2openjl.v3.model.transform.Transformation;
import org.obj2openjl.v3.model.transform.matrix.RotationMatrix;
import org.obj2openjl.v3.model.transform.matrix.TransformationMatrix;

public class RawOpenGLModelTest {
	
	public RawOpenGLModel getRawModel() {
		Obj2OpenJL obj2openjl = new Obj2OpenJL();
		try {
			return obj2openjl.convert("test/box");
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		}
		return null;
	}
	
	@Test
	public void testCenter() {
		RawOpenGLModel model = this.getRawModel();
		OpenGLModelData originalData = model.getDataForGLDrawElements();
		BoundingBox3D originalBounds = model.getBoundingBox();
		model.center();
		OpenGLModelData centeredData = model.getDataForGLDrawElements();
		BoundingBox3D centeredBounds = model.getBoundingBox();
		
		assertTrue(originalBounds.getLengthX() == centeredBounds.getLengthX());
		assertTrue(originalBounds.getLengthY() == centeredBounds.getLengthY());
		assertTrue(originalBounds.getLengthZ() == centeredBounds.getLengthZ());
		
		assertTrue(centeredBounds.getCenterX() == 0.f);
		assertTrue(centeredBounds.getCenterY() == 0.f);
		assertTrue(centeredBounds.getCenterZ() == 0.f);
		
		float distanceMovedX = originalBounds.getCenterX();
		float distanceMovedY = originalBounds.getCenterY();
		float distanceMovedZ = originalBounds.getCenterZ();
		
		assertTrue(originalData.getVertices()[0] == centeredData.getVertices()[0] + distanceMovedX);
		assertTrue(originalData.getVertices()[1] == centeredData.getVertices()[1] + distanceMovedY);
		assertTrue(originalData.getVertices()[2] == centeredData.getVertices()[2] + distanceMovedZ);
		
		// It's a cuboid.
		assertTrue(centeredBounds.getMinX() == -centeredBounds.getMaxX());
		assertTrue(centeredBounds.getMinY() == -centeredBounds.getMaxY());
		assertTrue(centeredBounds.getMinZ() == -centeredBounds.getMaxZ());
	}
	
	@Test
	public void testNormalize() {
		BoundingBox3D normalizedBounds = this.getRawModel().normalize().getBoundingBox();
		
		assertTrue(normalizedBounds.getLengthX() <= 1.f);
		assertTrue(normalizedBounds.getLengthY() <= 1.f);
		assertTrue(normalizedBounds.getLengthZ() <= 1.f);
	}
	
	@Test
	public void testRotate() {
		RawOpenGLModel model = this.getRawModel();
		OpenGLModelData originalModelData = model.getDataForGLDrawElements();
		
		int rotations = 10;
		float step = 360/rotations;
		
		float x = originalModelData.getVertices()[0];
		float y = originalModelData.getVertices()[1];
		float z = originalModelData.getVertices()[2];
		Vertex rotatedVertex = new Vertex(x, y, z, null);
//		printval(rotatedVertex.toArrayCoordinatesOnly());
	
		Transformation rotation;
		TransformationMatrix rotationMatrix;
		for(int i = 1; i <= rotations; i++) {
			rotationMatrix = new RotationMatrix(i*step, 0.f, 0.f, 1.f);
			rotation = new Transformation(rotationMatrix);
			rotation.applyTo(rotatedVertex);
//			printval(rotatedVertex.toArrayCoordinatesOnly());
		}
		
		assertTrue(originalModelData.getVertices()[0] == rotatedVertex.toArray()[0]);
		assertTrue(originalModelData.getVertices()[1] == rotatedVertex.toArray()[1]);
		assertTrue(originalModelData.getVertices()[2] == rotatedVertex.toArray()[2]);
		
		rotationMatrix = new RotationMatrix(-90, 0.f, 1.f, 0.f);
		rotation = new Transformation(rotationMatrix);
		
		x = 1.f;
		y = 0.f;
		z = 1.f;
		rotatedVertex = new Vertex(x, y, z, null);
		
		rotation.applyTo(rotatedVertex);
		
		System.out.println();System.out.println();
		printval(rotatedVertex.getFloatValues());
		
		
	}
	
	private void printval(Float[] vals) {
		String res = "";
		for(float val: vals) {
			res += val + " ";
		}
		System.out.println(res);
	}
	
}
