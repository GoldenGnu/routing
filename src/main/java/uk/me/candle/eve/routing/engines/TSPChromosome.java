/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/TSPChromosome.java,v $
 * $Id: TSPChromosome.java,v 1.3 2005/08/23 23:18:05 dsaiko Exp $
 * $Date: 2005/08/23 23:18:05 $
 * $Revision: 1.3 $
 * $Author: dsaiko $
 *
 * Traveling Salesman Problem genetic algorithm.
 * This source is released under GNU public licence agreement.
 * dusan@saiko.cz
 * http://www.saiko.cz/ai/tsp/
 * 
 * Change log:
 * $Log: TSPChromosome.java,v $
 * Revision 1.3  2005/08/23 23:18:05  dsaiko
 * Finished.
 *
 * Revision 1.2  2005/08/13 12:53:02  dsaiko
 * XML2PDF report finished
 *
 * Revision 1.1  2005/08/12 23:52:17  dsaiko
 * Initial revision created
 *
 */
package uk.me.candle.eve.routing.engines;

import uk.me.candle.eve.graph.Node;

/**
 * @author Dusan Saiko (dusan@saiko.cz) Last change $Date: 2005/08/23 23:18:05 $
 *
 * TSPChromosome of the traveling salesman problem. The chromosome represents
 * ordered array of cities and have some functions over this array.
 * @param <T>
 */
public class TSPChromosome<T extends Node> {

    /**
     * ordered array of cities
     */
    protected T[] cities;
    /**
     * distance of this chromosome - the length of all the way through all the
     * cities and back to the first one. if the coordinates of cities are in
     * S-JTSK, then this length is in meters Can be used as genetic evaluation
     * criteria.
     */
    protected double totalDistance;
    /**
     * total cost of this chroosome. can contain more criteria than the distance
     * itself (e.g. maxDistance of cities ...)
     */
    protected double totalCost;
    /**
     * Return to start city at the end
     */
    protected final boolean loop;

    /**
     * Creates the chromosome from the list of cities
     *
     * @param cities
     * @param type
     */
    public TSPChromosome(T[] cities, boolean loop) {
        this.cities = cities.clone();
        this.loop = loop;
        // compute the current costs
        computeCost();
    }

    /**
     * Compute the total distance and cost of this chromosome - Distance is the
     * length of all the way through all the cities and back to the first one.
     * if the coordinates of cities are in S-JTSK, then this length is in
     * meters. The costs could be different from distance in that way, that it
     * can contain more criteria than the distance itself
     */
    public final void computeCost() {
        //compute the distance to travel through all the cities
        totalDistance = 0;
        totalCost = 0;
       
        //go through cities and compute costs
        for (int i = 0; i < cities.length - 1; i++) {
            totalDistance += Manager.distance(cities[i], cities[i + 1]);
            totalCost += Manager.cost(cities[i], cities[i + 1]);
        }
        
        //add the cost from last city back to home
        if (loop) {
            totalDistance += Manager.distance(cities[cities.length - 1], cities[0]);
            totalCost += Manager.cost(cities[cities.length - 1], cities[0]);
        }
    }

    /**
     * @return the distance of this chromosome - the length of all the way
     * through all the cities and back to the first one. if the coordinates of
     * cities are in S-JTSK, then this length is in meters
     */
    public double getTotalDistance() {
        return totalDistance;
    }

    /**
     * get total cost of this chroosome. can contain more criteria than the
     * distance itself (e.g. maxDistance of cities ...)
     *
     * @return totalCost
     */
    public double getTotalCost() {
        return totalCost;
    }

    /**
     * @return the ordered array of cities of this chromosome
     */
    public T[] getCities() {
        return cities;
    }
}