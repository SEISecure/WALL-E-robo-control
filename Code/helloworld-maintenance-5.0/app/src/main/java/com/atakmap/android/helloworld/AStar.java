// AStar.java
package com.atakmap.android.helloworld;

import android.location.Location;

import androidx.annotation.NonNull;

import com.atakmap.coremap.filesystem.FileSystemUtils;
import com.atakmap.coremap.log.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
//import java.util.Objects;

public final class AStar {
   public static final class MapNode implements Serializable {
      private String id;
      private double latitude;
      private double longitude;
      private  NodeTerrain terrain;
      private  String list_of_neighbors;

      public String getId() {
         return id;
      }

      public double getLatitude() {
         return latitude;
      }

      public double getLongitude() {
         return longitude;
      }

      public NodeTerrain getTerrain() {
         return terrain;
      }

      public String getList_of_neighbors() {
         return list_of_neighbors;
      }

      private MapNode() {
      }

      public MapNode(String ID, double latitude, double longitude) {
         this.id = ID;
         this.latitude = latitude;
         this.longitude = longitude;
      }

      public void setId(String ID) {
         this.id = ID;
      }

      public void setTerrain(NodeTerrain terrain) {
         this.terrain = terrain;
      }

      //public void setList_of_neighbors(String neighbors) {
      //   this.list_of_neighbors = neighbors;
      //}

   }
   public static final class MapUnit implements Serializable  {
      private final double latitude;
      private final double longitude;
      private final String name;
      private String side;
      private String type;

      public double getLatitude() {
         return latitude;
      }

      public double getLongitude() {
         return longitude;
      }

      public String getName() {
         return name;
      }

      public String getSide() {
         return side;
      }

      public String getType() {
         return type;
      }

      private MapUnit() {
         this.name = "";
         this.latitude = 0.0;
         this.longitude = 0.0;
         this.side = "";
         this.type = "";
      }

      public MapUnit(String name, double latitude, double longitude) {
         this.name = name;
         this.latitude = latitude;
         this.longitude = longitude;
      }

   }
   private enum NodePenalty {
      Velocity,
      //Cover,
      Concealment
   }
   private enum NodeTerrain {
      empty0,  // 0
      Water,   // 1
      Trees,   // 2
      empty3,  // 3
      FloodedVegetation,   // 4
      Crops,   //  5
      empty6,  // 6
      BuiltArea,   // 7
      Bareground,   // 8
      SnowIce, // 9
      Clouds,  // 10
      RangeLand   // 11
   }

   private String mEnd = "";
   private boolean mDaytime  = false;
   private NodeTerrain[][] mTerrainArray;
   private int[][] mTerrainSecondArray;
   public int mHorizontalSize  = 0;
   public int mVerticalSize  = 0;
   private NodeTerrain [] SOAtypes;

   private static final class Key1<NodePenalty, NodeTerrain>  {
      public NodePenalty penalty;
      public NodeTerrain terrain;

      public Key1(NodePenalty penalty, NodeTerrain terrain)
      {
         this.penalty = penalty;
         this.terrain = terrain;
      }

      @NonNull
      @Override
      public String toString() {
         return "[" + "penalty" + ", " + "terrain" + "]";
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         Key1<?, ?> key1 = (Key1<?, ?>) o;
         return Objects.equals(penalty, key1.penalty) && Objects.equals(terrain, key1.terrain);
      }

      @Override
      public int hashCode() {
         return Objects.hash(penalty, terrain);
      }
   }

   private final Map<Key1<NodePenalty, NodeTerrain>, Double> mDayWeights = new HashMap<Key1<NodePenalty, NodeTerrain>, Double>(){
      {
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.empty0),10.0);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.empty3),10.0);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.empty6),10.0);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.Water),7.15);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.Trees),2.59);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.FloodedVegetation),4.17);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.Crops),4.69);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.BuiltArea),2.83); //good if home team, bad if away
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.Bareground),6.5);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.SnowIce),6.52);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.Clouds),5.02);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.RangeLand),5.74);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.empty0),10.0);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.empty3),10.0);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.empty6),10.0);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.Water),8.5);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.Trees),3.5);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.FloodedVegetation),6.75);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.Crops),3.375);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.BuiltArea),9.323); //orginally 6.125
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.Bareground),2.275);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.SnowIce),6.625);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.Clouds),4.41);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.RangeLand),1.365);
      }
   };
   private final Map<Key1<NodePenalty, NodeTerrain>, Double> mNightWeights = new HashMap<Key1<NodePenalty, NodeTerrain>, Double>(){
      {
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.empty0),0.0);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.empty3),0.0);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.empty6),0.0);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.Water),2.85);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.Trees),7.41);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.FloodedVegetation),5.83);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.Crops),5.31);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.BuiltArea),7.17);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.Bareground),3.5);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.SnowIce),3.48);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.Clouds),4.97625);
         this.put(new Key1<>(NodePenalty.Concealment, NodeTerrain.RangeLand),4.26);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.empty0),0.0);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.empty3),0.0);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.empty6),0.0);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.Water),1.5);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.Trees),6.5);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.FloodedVegetation),4.25);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.Crops),6.625);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.BuiltArea),9.3); //orginally 6.125
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.Bareground),7.725);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.SnowIce),3.375);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.Clouds),5.591875);
         this.put(new Key1<>(NodePenalty.Velocity, NodeTerrain.RangeLand),8.635);
      }
   };
   private final List<MapNode> mNodeList = new ArrayList<>();

   public AStar() {
      SOAtypes = new NodeTerrain [12];
      SOAtypes[0] = NodeTerrain.empty0;
      SOAtypes[1] = NodeTerrain.Water;
      SOAtypes[2] = NodeTerrain.Trees;
      SOAtypes[3] = NodeTerrain.empty3;
      SOAtypes[4] = NodeTerrain.FloodedVegetation;
      SOAtypes[5] = NodeTerrain.Crops;
      SOAtypes[6] = NodeTerrain.empty6;
      SOAtypes[7] = NodeTerrain.BuiltArea;
      SOAtypes[8] = NodeTerrain.Bareground;
      SOAtypes[9] = NodeTerrain.SnowIce;
      SOAtypes[10] = NodeTerrain.Clouds;
      SOAtypes[11] = NodeTerrain.RangeLand;

      ReadFiles();
      fillNodeList();
   }

   public List<MapNode> getNodesMaps(){
      return mNodeList;
   }

   private void fillNodeList(){

      // create the matrix array with points
      String ID ;
      int iID = 0;
      for (int j = 0; j < mVerticalSize; j++){
         for (int i = 0; i < mHorizontalSize; i++)  {
            iID = mHorizontalSize * j + i + 1;
            ID = Integer.toString(iID);
            MapNode Node = getPositionFromIJ(i, j);
            Node.setId(ID);
            Node.setTerrain(mTerrainArray[i][j]);

            // fill the list of neighbors for each point
            String neighb1 = "";
            for (int neighborI = -1; neighborI < 2; neighborI++) {
               for (int neighborJ = -1; neighborJ < 2; neighborJ++) {
                  if ((neighborI + i < 0) || (neighborI + i >= mHorizontalSize) ||
                          (neighborJ + j < 0) || (neighborJ + j >= mVerticalSize) ||
                          ((neighborI == 0) && (neighborJ == 0)))  continue;
                  int neighborintID = iID + mHorizontalSize * neighborJ + neighborI;
                  String neighborID = Integer.toString(neighborintID);
                  if (Node.list_of_neighbors == "")  neighb1 = neighborID;
                  else neighb1 = Node.list_of_neighbors + "," + neighborID;
                  Node.list_of_neighbors = neighb1;
               }
            }
            mNodeList.add(Node);
         }
      }

   }

   private void ReadFiles()  {

      // Read the Landcover file
      File theFile = new File(FileSystemUtils.getItem("export/"), "Landcover_KOBOL-1.txt");
      Log.d("astar", theFile.getAbsolutePath());
      String fileName = theFile.getAbsolutePath();
      File file = new File(fileName);
      String dd = file.exists() + "  " + file.canRead();
      Log.d("astar", dd);


      String thisLine;
      List<String[]> lines = new ArrayList<String[]>();

      try {
         BufferedReader reader = new BufferedReader(new FileReader(fileName));

         while ((thisLine = reader.readLine()) != null) {
            lines.add(thisLine.split(","));
         }
         reader.close();

         mVerticalSize = lines.size();
         mHorizontalSize = lines.get(0).length;
         // convert our list to a double array of NodeTerrains.
         mTerrainArray = new NodeTerrain[mHorizontalSize][mVerticalSize];
         mTerrainSecondArray = new int[mHorizontalSize][mVerticalSize];
         NodeTerrain [] terrainArray = NodeTerrain.values();
         for (int i = 0; i < mHorizontalSize; i++){   // x horizontal are the different elements in a line
            for (int j = 0; j < mVerticalSize; j++){   // y  vertical are the different lines
               int soaCode = (int)Double.parseDouble(lines.get(j)[i]);
               mTerrainArray[i][j] = SOAtypes[soaCode] ;
               mTerrainSecondArray[i][j] = soaCode ;
            }
         }
      }  catch (Exception e) {
         e.printStackTrace();
         Log.d("astar", "error");
      }
      return;
   }

   @NonNull
   public List<String> run(@NonNull String startNode, @NonNull String endNode, boolean daytime,
                         int velocity, int cover, int concealment, int minDistToEnemy,  @NonNull  List<MapUnit> enemies) throws Throwable {

      List<String> mOpen = new ArrayList<>();   // set of possible nodes to work withset of possible nodes to work with
      List<String> mClosed = new ArrayList<>(); // set of nodes already worked with

      Map<String, Float> mGScores = new HashMap<>();  // score added between present and next node
      Map<String, Float> mHScores = new HashMap<>();  // score added between next node and end
      Map<String, Float>  mFScores = new HashMap<>(); // total score added by using next node
      Map<String, String> mPrev = new HashMap<>(); // list with the previous node to every node

      this.mEnd = endNode;
      this.mDaytime = daytime;

      // initialize first node with the location and the scores to end node
      mOpen.add(startNode);
      mGScores.put(startNode, 0.0F);
      float startH = this.calculateHScore(startNode, endNode, velocity, cover, concealment, minDistToEnemy, enemies);
      mHScores.put(startNode, startH);
      mFScores.put(startNode, startH);

      int counter = 0;
      float shortestDistance = this.CalculateDistanceforNodes(startNode, endNode);;
      List<String> neighborsReturn = new ArrayList<>();
      while(!mOpen.isEmpty()) {

         // find the ID of the node in the mOpen set with minimum mFScore and call it bestNode
         // From now on bestNode will be the presently working node
         Map<String, Float> output = new HashMap<>();
         for (Map.Entry<String, Float> entry : mFScores.entrySet()) {
            if (mOpen.contains(entry.getKey())) {
               output.put(entry.getKey(), entry.getValue());
            }
         }
         Float minimum = Float.MAX_VALUE;
         String ID = "";
         for (Map.Entry<String, Float> entry: output.entrySet()){
            if (entry.getValue() < minimum) {
               minimum = entry.getValue();
               ID = entry.getKey();
            }
         }

         String bestNode = ID;
         if (Objects.equals(bestNode, "")) break;     // if no one found, return

         counter += 1;
         float distance = this.CalculateDistanceforNodes(bestNode, endNode);
         if (distance < shortestDistance) shortestDistance = distance;
         Log.d("astar", counter + "  best node is " + bestNode + "  dist: " + distance);

         if(Objects.equals(bestNode, endNode)) {      // if found is endNode, return route
            Log.d("astar", "vel $velocity");
            return getFinalRoute(mPrev);
         }

         // move bestNode from mOpen to mClosed.
         mClosed.add(bestNode);
         mOpen.remove(bestNode);

         // get the list of neighbors around the present node and loop over it
         List<String> neighbors = this.getNeighbors(bestNode);
         for (String neighbor : neighbors)  {

            // if the neighbor already worked, go to next neighbor
            if(mClosed.contains(neighbor)) continue;

            // get the gScore from the neighbor and update mGScores, if it is the better or didn't existed
            Float gScore = mGScores.get(neighbor);
            float currG = this.calculateGScore(bestNode, neighbor, mGScores, velocity, cover, concealment, minDistToEnemy, enemies);
            if (gScore == null || currG < gScore) {
               mGScores.put(neighbor, currG);

               // get the hScore from neighbor and update mFScores
               Float score = mHScores.get(neighbor);
               float hScore = score != null ? score : this.calculateHScore(neighbor, endNode, velocity, cover, concealment, minDistToEnemy, enemies);
               mFScores.put(neighbor, currG + hScore);

               // add the pair present node (bestNode) as previous to neighbor
               mPrev.put(neighbor, bestNode);

               // add neighbor to the list in mOpen
               mOpen.add(neighbor);
            }  // end of gScore calculation
         }  // end of loop over neighbors


         if ((counter % 200) == 0) {
            Log.d("astar", counter + "  best node is size before " + mOpen.size());
            for (int i = mOpen.size() - 1; i >= 0; i--) {
               String node = mOpen.get(i);
               if (node == null) mOpen.remove(i);
               else if (this.CalculateDistanceforNodes(node, endNode) > (1.01F * shortestDistance)) {
                  mOpen.remove(i);
                  mClosed.add(node);
               }
            }
            Log.d("astar", counter + "  best node is size after " + mOpen.size());
         }

      }  // continue loop over mOpen

      return neighborsReturn; //end node is unreachable, return empty list
   }

   private float calculateGScore(String prev, String next, Map<String, Float> mGScores,
                                 int velocity, int cover, int concealment, int minDistToEnemy, List<MapUnit> enemies) {

      //MapNode nodePrev = this.getNode(prev);    // present node
      MapNode nodeNext = this.getNode(next);    // test node

      //get distance in meters
      //float[] ret = new float[1];
      //Location.distanceBetween(nodePrev.getLatitude(), nodePrev.getLongitude(),nodeNext.getLatitude(),nodeNext.getLongitude(), ret);
      float ret1 = CalculateDistanceforNodes(prev, next);
      //Log.d("astar", "route dist ${ret[0]}");
      Log.d("astar", "route dist: " + ret1);

      NodeTerrain terrain = nodeNext.getTerrain();
      double terrainPenalty = 0.0D;
      NodePenalty[] Penalties = NodePenalty.values();
      for (NodePenalty penalty: Penalties)
         terrainPenalty += getTerrainPenalty(penalty,terrain,velocity,cover,concealment);

      int enemyPenalty = this.getEnemyPenalty(enemies, nodeNext, minDistToEnemy);
      Log.d("astar", "terrain " + terrain + " penalty " + terrainPenalty);
      Log.d("astar", "enemy penalty " + enemyPenalty);

      //return 0.0F;
      return mGScores.get(prev) + ret1 + (float)terrainPenalty + (float)enemyPenalty;

   }

   private int getEnemyPenalty(List<MapUnit> enemies, MapNode nodeNext, int minDistToEnemy) {

      int ret = 0;
      if ((enemies == null) || enemies.isEmpty()) return ret;

      for (MapUnit enemy : enemies) {
         float[] enemyDist = {1.0F};
         enemyDist[0] = 0.0F;
         Location.distanceBetween(nodeNext.getLatitude(),nodeNext.getLongitude(), enemy.getLatitude(), enemy.getLongitude(), enemyDist);
         if (enemyDist[0] < minDistToEnemy) {
            ret+=1000;
         }
      }
      return ret;
   }

   public double getTerrainPenalty(@NonNull NodePenalty penalty, @NonNull NodeTerrain terrain, int velocity, int cover, int concealment) {
      double weight;
      if (mDaytime) {
         weight = getDayWeightsValue(new Key1<>(penalty, terrain));
      } else {
         weight = getNightWeightsValue(new Key1<>(penalty, terrain));
      }

      double ret = 0.0;
      if (penalty == NodePenalty.Velocity) ret = Math.pow(3,weight) * (velocity  / 10.0);
         //else if  (penalty == NodePenalty.Cover) ret = weight * (cover  / 10.0);
      else if  (penalty == NodePenalty.Concealment) ret = Math.pow(3,weight) * (concealment  / 10.0);

      return ret; //30k weight unneeded
   }
   public Double getDayWeightsValue(Key1<NodePenalty, NodeTerrain> key) {
      for (Map.Entry<Key1<NodePenalty, NodeTerrain>, Double> entry: mDayWeights.entrySet()) {
         if (entry.getKey().equals(key)) return entry.getValue();
      }
      return 0.0;
   }

   public Double getNightWeightsValue(Key1<NodePenalty, NodeTerrain> key) {
      for (Map.Entry<Key1<NodePenalty, NodeTerrain>, Double> entry: mNightWeights.entrySet()) {
         if (entry.getKey().equals(key)) return entry.getValue();
      }
      return 0.0;
   }

   private float CalculateDistanceforNodes(String curr, String end){
      MapNode nodeCurr = this.getNode(curr);
      MapNode nodeNext = this.getNode(end);
      float[] ret = new float[1];
      Location.distanceBetween(nodeCurr.getLatitude(), nodeCurr.getLongitude(), nodeNext.getLatitude(), nodeNext.getLongitude(), ret);
      return ret[0];
   }

   private float calculateHScore(String curr, String end,
                                 int velocity, int cover, int concealment, int minDistToEnemy, List<MapUnit> enemies)  {
      float distance = CalculateDistanceforNodes(curr, end);////*10;
      int numSteps = (int)distance/100; //100 = resolution of the map
      //MapNode nodeCurr = this.getNode(curr);
      MapNode nodeNext = this.getNode(end);
      //float[] ret = new float[1];
      //Location.distanceBetween(nodeCurr.getLatitude(), nodeCurr.getLongitude(), nodeNext.getLatitude(), nodeNext.getLongitude(), ret);

      NodeTerrain terrain = nodeNext.getTerrain();
      double terrainPenalty = 0.0D;
      NodePenalty[] Penalties = NodePenalty.values();
      for (NodePenalty penalty: Penalties)
         terrainPenalty += getTerrainPenalty(penalty,terrain,velocity,cover,concealment);

      int enemyPenalty = this.getEnemyPenalty(enemies, nodeNext, minDistToEnemy);

      return distance + numSteps*((float)terrainPenalty + (float)enemyPenalty);
      //return ret[0];
   }

   public  MapNode getNode(String node) {
      MapNode mapNode = new MapNode();
      for (MapNode value : mNodeList) {
            if (Objects.equals(value.getId(), node)) {
               mapNode = value;
               break;
            }
      }
      return mapNode;
   }

   @NonNull
   public List<String> parseNeighbors(@NonNull String neighbors) {
      String[] result1 = neighbors.split(",");
      List<String> result = new ArrayList<>();
      for (String word : result1) {
         if (!Objects.equals(word, "")) result.add(word);
      }
      return result;
   }

   private List<String> getNeighbors(String node) {

      MapNode theNode = getNode(node);
      String thisNeighbors = theNode.getList_of_neighbors();
      return this.parseNeighbors(thisNeighbors);
   }

   private List<String> getFinalRoute(Map<String, String> mPrev) {
      List<String> ret = new ArrayList<>();
      ret.add(0,mEnd);
      String currNode = mPrev.get(mEnd);

      while( currNode != null ) {
         ret.add(0,currNode);
         currNode = mPrev.get(currNode);
      }
      return ret;
   }

   private MapNode getPositionFromIJ(int i, int j) {

      double lat_lowerLeft = 38.559796;
      double long_lowerLeft = -77.595278;
      double lat_upperRight = 38.648056;
      double long_upperRight = -77.482672;
      double lat_upperLeft = lat_upperRight;    // this is i = 0, j = 0
      double long_upperLeft = long_lowerLeft;   // this is i = 0, j = 0
      double deltaLat = Math.abs(lat_upperRight - lat_lowerLeft);
      double deltaLong = Math.abs(long_upperRight - long_lowerLeft);

      double latitude = lat_upperLeft - ((double)j / (double)mVerticalSize) * deltaLat;
      double longitude= long_upperLeft + ((double)i / (double)mHorizontalSize) * deltaLong;
      MapNode Position = new MapNode("", latitude, longitude);

      return Position;
   }
}
