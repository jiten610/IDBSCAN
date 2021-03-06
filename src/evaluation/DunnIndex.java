package evaluation;

import java.util.ArrayList;
import clustering.algorithms.Cluster;
import clustering.algorithms.DenseRegion;
import datasets.DatasetPoint;

public class DunnIndex {
	
	private ArrayList<DatasetPoint> dataset;
	private double maxClusterSize;
	private ArrayList<Cluster> clusters;

	public DunnIndex(ArrayList<Cluster> clusters, ArrayList<DatasetPoint> dataset) {
		this.dataset = dataset;
		this.maxClusterSize = 0;
		this.clusters = clusters;
		this.maxClusterSize = calculateMaxClusterSize();
	}
	
	public DunnIndex(ArrayList<Cluster> clusters, ArrayList<DenseRegion> denseRegions ,ArrayList<DatasetPoint> dataset){
		this.dataset = dataset;
		this.maxClusterSize = 0;
		this.clusters = clusters;
		for (int i = 0; i < clusters.size(); i++) {
			Cluster c = this.clusters.get(i);
			if(!c.getIsActive()) continue;
   		ArrayList<DenseRegion> clusterDenseRegions = c.getRegions();
   		for (int j = 0; j < clusterDenseRegions.size(); j++) {
				DenseRegion d = clusterDenseRegions.get(j);
				c.addPointsList(d.getPoints());
   		}
		}
		this.maxClusterSize = calculateMaxClusterSize();
		
	}
	
	/**
	 * calculate the dunn index between two clusters
	 * @param clusters clusters 
	 * @param dataset dataset
	 * @return dunn index
	 */
	public double calculateDunnIndex(){
		double minDI = Double.MAX_VALUE;
		for (int i = 0; i < clusters.size(); i++) {
			Cluster ci = clusters.get(i);
			if(!ci.getIsActive()) continue;
			if(ci.getPointsIDs().size() < 30) continue;

			for (int j = 0; j < clusters.size(); j++) {
				Cluster cj = clusters.get(j);
				if( ci.getID() == cj.getID() || !cj.getIsActive()) continue;
				if(cj.getPointsIDs().size() < 30) continue;

				double minDistBetCiCj = calculateMinDistanceBetweenTwoClusters(ci, cj);
				double DIij = minDistBetCiCj/this.maxClusterSize;
				if(DIij < minDI){
					minDI = DIij;
				}
			}
		}
		return minDI;
	}
	
	/**
	 * calculate the minimum distance between two clusters
	 * @param ci cluster ci
	 * @param cj cluster cj
	 * @return min distance between ci and cj
	 */
	private double calculateMinDistanceBetweenTwoClusters(Cluster ci, Cluster cj){
		double minDist = 0;
		ArrayList<Integer> ciPoints =	ci.getPointsIDs();
		ArrayList<Integer> cjPoints = cj.getPointsIDs();
		for (int i = 0; i < ciPoints.size(); i++) {
			DatasetPoint ciPoint = this.dataset.get(ciPoints.get(i));
			for (int j = 0; j < cjPoints.size(); j++) {
				DatasetPoint cjPoint = this.dataset.get(cjPoints.get(j));
				double distance = calculateDistanceBtwTwoPoints(ciPoint, cjPoint);
				minDist+= distance;
			}
		}
		return minDist/((ciPoints.size()-1)*(cjPoints.size()*1));
	}
	
	
	/**
	 * calculate the max cluster size
	 * @return max cluster size
	 */
	private double calculateMaxClusterSize(){
		double maxClusterSize = Double.MIN_VALUE;
		for (int i = 0; i < this.clusters.size(); i++) {
			Cluster c = this.clusters.get(i);
			if(!c.getIsActive() || c.getPointsIDs().size() < 30) continue;
			double clusterSize = calculateClusterSize(c);
			if(clusterSize > maxClusterSize){
				maxClusterSize = clusterSize;
			}
		}
		return maxClusterSize;
	}
	
	/**
	 * Calculate the cluster size or diameter
	 * @param c cluster
	 * @return cluster size or diameter
	 */
	private double calculateClusterSize(Cluster c){
		double size = 0;
		ArrayList<Integer> clusterPoints = c.getPointsIDs();
		for (int i = 0; i < clusterPoints.size(); i++) {
			DatasetPoint point1 = this.dataset.get(clusterPoints.get(i));
			for (int j = 0; j < clusterPoints.size(); j++) {
				if(i==j) continue;
				DatasetPoint point2 = this.dataset.get(clusterPoints.get(j));
				size += calculateDistanceBtwTwoPoints(point1, point2);
			}
		}
		return size/(clusterPoints.size()*(clusterPoints.size()-1));
	}
	
	/**
	 * calculate the Euclidean Distance between two points
	 * @param p1 point 1
	 * @param p2 point 2
	 * @return Distance
	 */
	private double calculateDistanceBtwTwoPoints(DatasetPoint p1, DatasetPoint p2){
		double xDiff = p1.getX() - p2.getX();
		double yDiff = p1.getY() - p2.getY();
		return Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
	}



}
