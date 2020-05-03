/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/TSPConfiguration.java,v $
 * $Id: TSPConfiguration.java,v 1.3 2005/08/23 23:18:05 dsaiko Exp $
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
 * $Log: TSPConfiguration.java,v $
 * Revision 1.3  2005/08/23 23:18:05  dsaiko
 * Finished.
 *
 * Revision 1.2  2005/08/22 22:08:51  dsaiko
 * Created engines with heuristics
 *
 * Revision 1.1  2005/08/12 23:52:17  dsaiko
 * Initial revision created
 *
 */
package uk.me.candle.eve.routing.engines;

/**
 * @author Dusan Saiko (dusan@saiko.cz) Last change $Date: 2005/08/23 23:18:05 $
 *
 * TSPConfiguration specifies the configuration parameters of the application
 */
public class TSPConfiguration {

    /**
     * Initial population count which is set to the computation engine.
     */
    protected int initialPopulationSize = 100;
    /**
     * Computation thread priority
     *
     * @see Thread#setPriority(int)
     */
    protected int threadPriority = 5;
    /**
     * Computation thread maximum count
     * 0 (zero) for maximum
     * Will never exceed Runtime.getRuntime().availableProcessors() * 2
     */
    protected int threadMaxCount = 1;
    /**
     * Population growth between two generations. This flag does not apply for
     * all the engines.
     */
    protected double populationGrow = 0.00075;
    /**
     * Ratio (0..1), how much the population should undergo random mutation
     */
    protected double mutationRatio = 0.5;
    /**
     * The count of generation which give the same best result after which the
     * program should stop computations;
     */
    protected int maxBestCostAge = 100;

    /**
     * @return initial population count which is set to the computation engine.
     */
    public int getInitialPopulationSize() {
        return initialPopulationSize;
    }

    /**
     * @param initialPopulationSize Initial population count which is set to the
     * computation engine.
     */
    public void setInitialPopulationSize(int initialPopulationSize) {
        this.initialPopulationSize = initialPopulationSize;
    }

    /**
     * @return Returns the maxBestCostAge.
     */
    public int getMaxBestCostAge() {
        return maxBestCostAge;
    }

    /**
     * @param maxBestCostAge The maxBestCostAge to set.
     */
    public void setMaxBestCostAge(int maxBestCostAge) {
        this.maxBestCostAge = maxBestCostAge;
    }

    /**
     * @return ratio (0..1), how much the population should undergo random
     * mutation
     */
    public double getMutationRatio() {
        return mutationRatio;
    }

    /**
     * @param mutationRatio Ratio (0..1), how much the population should undergo
     * random mutation
     */
    public void setMutationRatio(double mutationRatio) {
        this.mutationRatio = mutationRatio;
    }

    /**
     * @return Population growth between two generations. This flag does not
     * apply for all the engines.
     */
    public double getPopulationGrow() {
        return populationGrow;
    }

    /**
     * @param populationGrow Population growth between two generations. This
     * flag does not apply for all the engines.
     */
    public void setPopulationGrow(double populationGrow) {
        this.populationGrow = populationGrow;
    }

    /**
     * @return computation thread priority
     * @see Thread#setPriority(int)
     *
     */
    public int getThreadPriority() {
        return threadPriority;
    }

    /**
     * @param threadPriority Computation thread priority
     * @see Thread#setPriority(int)
     */
    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }

    /**
     * @return Computation thread maximum count
     * 0 (zero) for maximum
     * Will never exceed Runtime.getRuntime().availableProcessors() * 2
     */
    public int getThreadMaxCount() {
        return threadMaxCount;
    }

    /**
     * @param threadMaxCount Computation thread maximum count
     * 0 (zero) for maximum
     * Will never exceed Runtime.getRuntime().availableProcessors() * 2
     */
    public void setThreadMaxCount(int threadMaxCount) {
        this.threadMaxCount = threadMaxCount;
    }
}