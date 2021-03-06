package clustering.partitioning;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.crypto.Data;

import org.jfree.ui.RefineryUtilities;

import plot.PlotPartitioning;

import datasets.ChameleonData;
import datasets.DatasetPoint;

public class PartitioningAlgorithm {
 	private ArrayList<DatasetPoint> dataset;
	private Centroid[] centroids;

	/**
	 * Constructor
	 * @param dataset dataset
	 * @param k number of partitions
	 */
	public PartitioningAlgorithm(ArrayList<DatasetPoint> dataset, int k) {
		this.dataset= dataset;
		this.centroids = new Centroid[k];
		for (int i = 0; i < k; i++) {
			int randomIndex = 0 + (int)(Math.random() * (((200-1) - 0) + 1));
			DatasetPoint p = this.dataset.get(randomIndex);
			this.centroids[i] = new Centroid(i, p.getX(), p.getY());
		}
	}
	
	/**
	 * return the centroids
	 * @return centroids
	 */
	public Centroid[] getCentroids() {
		return centroids;
	}
	
	/**
	 * run the partitioning algorithm to al data
	 */
	public void run(){
		for (int i = 0; i < this.dataset.size(); i++) {
			DatasetPoint point = this.dataset.get(i);
			partitionPoint(point);
		}
	}
	
	/**
	 * Insert point into partitions
	 * @param point data point
	 * @return the partition id
	 */
	public int partitionPoint(DatasetPoint point){
		double distance = Double.MAX_VALUE;
		Centroid cen = null;
		for (int j = 0; j < this.centroids.length; j++) {
			double d = calculateDistanceBtwTwoPoints(this.centroids[j].getX(), this.centroids[j].getY(), point);
			if(d<distance){
				cen = this.centroids[j];
				distance = d;
			}
		}
		point.setAssignedCentroidID(cen.getID());
		cen.updateCentroid(point);
		return cen.getID();
	}
	
	/**
	 * calculate the distance between two points
	 * @param x  x
	 * @param y y 
	 * @param p2 data point
	 * @return distance between two points
	 */
	private double calculateDistanceBtwTwoPoints(double x, double y, DatasetPoint p2){
		double xDiff = x - p2.getX();
		double yDiff = y - p2.getY();
		return Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
	}

	
	public static void main(String[] args) throws IOException {
		int k =25;
		ChameleonData datasetLoader = new ChameleonData();
		ArrayList<DatasetPoint> dataset = datasetLoader.loadArrayList("/media/disk/master/Courses/Machine_Learning/datasets/chameleon-data/t5.8k.dat");
		PartitioningAlgorithm p = new PartitioningAlgorithm(dataset, k);
		p.run();
		PlotPartitioning plotter = new PlotPartitioning("partitions");
		plotter.plot(dataset, p.getCentroids());
		plotter.pack();
		RefineryUtilities.centerFrameOnScreen(plotter);
		plotter.setVisible(true); 

	}
	
}
