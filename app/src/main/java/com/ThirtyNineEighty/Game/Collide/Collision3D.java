package com.ThirtyNineEighty.Game.Collide;

import android.opengl.Matrix;
import android.util.Log;

import com.ThirtyNineEighty.Game.Objects.IPhysicalObject;
import com.ThirtyNineEighty.Helpers.Vector2;
import com.ThirtyNineEighty.Helpers.Vector3;

import java.util.Vector;

public class Collision3D
  extends Collision<Vector3>
{
  private Vector3 mtv;
  private float mtvLength;
  private boolean collide;

  public Collision3D(IPhysicalObject objectOne, IPhysicalObject objectTwo)
  {
    CheckResult result = check(objectOne, objectTwo);

    if (result == null)
    {
      collide = false;
      return;
    }

    collide = true;
    mtv = getMTV(result);
    mtvLength = result.collision.getMTVLength();
  }

  private CheckResult check(IPhysicalObject firstPh, IPhysicalObject secondPh)
  {
    Vector<Vector3> firstNormals = firstPh.getGlobalNormals();
    Vector<Vector3> secondNormals = secondPh.getGlobalNormals();

    CheckResult resultOne = check(firstPh, secondPh, firstNormals);
    if (resultOne == null)
      return null;

    CheckResult resultTwo = check(firstPh, secondPh, secondNormals);
    if (resultTwo == null)
      return null;

    float mtvLengthOne = resultOne.collision.getMTVLength();
    float mtvLengthTwo = resultTwo.collision.getMTVLength();

    if (Math.abs(mtvLengthOne) <= Math.abs(mtvLengthTwo))
      return resultOne;

    return resultTwo;
  }

  private CheckResult check(IPhysicalObject firstPh, IPhysicalObject secondPh, Vector<Vector3> normals)
  {
    Collision2D min = null;
    Vector3 minNormal = null;

    for (Vector3 normal : normals)
    {
      Vector<Vector2> resultOne = firstPh.getConvexHull(normal);
      Vector<Vector2> resultTwo = secondPh.getConvexHull(normal);

      Collision2D collision = new Collision2D(resultOne, resultTwo);
      if (!collision.isCollide()) return null;

      if (min == null || Collision2D.compare(min, collision) < 0)
      {
        min = collision;
        minNormal = normal;
      }
    }

    return new CheckResult(min, minNormal);
  }

  private Vector3 getMTV(CheckResult result)
  {
    Vector2 mtv2 = result.collision.getMTV();
    Vector3 mtv3 = new Vector3(mtv2.getX(), mtv2.getY(), 0);

    Vector3 planeZ = result.normal;
    Vector3 planeX = planeZ.getOrthogonal();
    Vector3 planeY = planeX.getCross(planeZ);

    float angleX = Vector3.xAxis.getAngle(planeX);
    float angleY = Vector3.yAxis.getAngle(planeY);
    float angleZ = Vector3.zAxis.getAngle(planeZ);

    float[] matrix = new float[16];
    Matrix.setIdentityM(matrix, 0);
    Matrix.rotateM(matrix, 0, angleX, 1.0f, 0.0f, 0.0f);
    Matrix.rotateM(matrix, 0, angleY, 0.0f, 1.0f, 0.0f);
    Matrix.rotateM(matrix, 0, angleZ, 0.0f, 0.0f, 1.0f);

    Matrix.multiplyMV(mtv3.getRaw(), 0, matrix, 0, mtv3.getRaw(), 0);

    mtv3.normalize();
    return mtv3;
  }

  @Override
  public Vector3 getMTV()
  {
    return mtv;
  }

  @Override
  public float getMTVLength()
  {
    return mtvLength;
  }

  @Override
  public boolean isCollide()
  {
    return collide;
  }

  private class CheckResult
  {
    public Collision2D collision;
    public Vector3 normal;

    public CheckResult(Collision2D collision, Vector3 normal)
    {
      this.collision = collision;
      this.normal = normal;
    }
  }
}
