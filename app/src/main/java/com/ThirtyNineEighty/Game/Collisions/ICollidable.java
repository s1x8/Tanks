package com.ThirtyNineEighty.Game.Collisions;

import com.ThirtyNineEighty.Helpers.Plane;
import com.ThirtyNineEighty.Helpers.Vector2;
import com.ThirtyNineEighty.Helpers.Vector3;

import java.util.ArrayList;

public interface ICollidable
{
  void setGlobal(Vector3 position, float angleX, float angleY, float angleZ);

  ArrayList<Vector2> getConvexHull(Plane plane);
  ArrayList<Vector3> getGlobalVertices();
  ArrayList<Vector3> getGlobalNormals();
}