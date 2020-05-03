/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/engines/simpleUnisexMutator/SimpleUnisexMutatorEngine.java,v $
 * $Id: SimpleUnisexMutatorEngine.java,v 1.2 2005/08/23 23:18:05 dsaiko Exp $
 * $Date: 2005/08/23 23:18:05 $
 * $Revision: 1.2 $
 * $Author: dsaiko $
 *
 * Traveling Salesman Problem genetic algorithm.
 * This source is released under GNU public licence agreement.
 * dusan@saiko.cz
 * http://www.saiko.cz/ai/tsp/
 * 
 * Change log:
 * $Log: SimpleUnisexMutatorEngine.java,v $
 * Revision 1.2  2005/08/23 23:18:05  dsaiko
 * Finished.
 *
 * Revision 1.1  2005/08/22 22:13:52  dsaiko
 * Packages rearanged
 *
 * Revision 1.1  2005/08/22 22:08:51  dsaiko
 * Created engines with heuristics
 *
 * Revision 1.3  2005/08/13 15:02:09  dsaiko
 * build task
 *
 * Revision 1.2  2005/08/13 14:41:35  dsaiko
 * *** empty log message ***
 *
 * Revision 1.1  2005/08/12 23:52:17  dsaiko
 * Initial revision created
 *
 */
package uk.me.candle.eve.routing.engines.simpleUnisexMutator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.engines.Manager;
import uk.me.candle.eve.routing.engines.TSPChromosome;
import uk.me.candle.eve.routing.engines.TSPConfiguration;
import uk.me.candle.eve.routing.engines.TSPEngine;

/**
 * @author Dusan Saiko (dusan@saiko.cz) Last change $Date: 2005/08/23 23:18:05 $
 *
 * Simple random unisex genetic algorithm for solving the Traveling Salesman
 * Probleme
 *
 * This class performs basic genetic operations and calls growPopulation(..)
 * method. In this way, this class can be used as supper class for overriding.
 *
 * Initialization and nextGeneration method implement multi thread algorithm
 * which uses at maximum Runtime.getRuntime().availableProcessors()*2 threads.
 *
 * This algorithm creates child in such way, that it just randomly swaps two
 * items in from parent
 * @param <T>
 *
 * @see #getChild(TSPChromosome)
 * @see org.saiko.ai.genetics.tsp.engines.crossover.GreedyCrossoverEngine
 * @see org.saiko.ai.genetics.tsp.TSPEngine
 */
public class SimpleUnisexMutatorEngine<T extends Node> implements TSPEngine<T> {

    /**
     * Population of the chromosomes
     */
    protected List<TSPChromosome<T>> population = Collections.synchronizedList(new ArrayList<>());
    /**
     * Current population size - the population may grow
     */
    protected int populationSize;
    /**
     * Mutation ratio of population (exactly percentage of mutation =
     * 1/mutationRatio)
     */
    protected int mutationRatio;
    /**
     * configuration paramteres of application
     *
     * @see TSPConfiguration
     */
    protected TSPConfiguration configuration;
    /**
     * Return to start city at the end
     */
    protected boolean loop;

    /**
     * @see org.saiko.ai.genetics.tsp.TSPEngine#initialize
     */
    @Override
    public void initialize(TSPConfiguration appConfiguration, final T cities[], final boolean loop) {
        this.configuration = appConfiguration;
        this.loop = loop;

        //clear the population if the engine is re-initialized
        population.clear();

        populationSize = configuration.getInitialPopulationSize();

        final List<Thread> runningThreads = Collections.synchronizedList(new ArrayList<>());
        //this has to be computed again as availableProcessors() can change in the time
        int maxThreadCount = Runtime.getRuntime().availableProcessors() * 2;
        //Adjust maxThreadCount to never be higher than settings allow
        if (maxThreadCount > configuration.getThreadMaxCount() && configuration.getThreadMaxCount() > 0) {
            maxThreadCount = configuration.getThreadMaxCount();
        }

        for (int i = 0; i < maxThreadCount; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (population.size() < populationSize) {
                        TSPChromosome<T> chromosome = new TSPChromosome<>(cities, loop);
                        randomize(chromosome.getCities());
                        chromosome.computeCost();
                        population.add(chromosome);
                    }
                }
            });
            runningThreads.add(thread);
        }
        for (Thread thread : runningThreads) {
            thread.start();
        }
        for (Thread thread : runningThreads) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                //No problem
            }
        }

        orderPopulation();
        //recompute utation ratio so we can use if(rnd.nextInt(mutationRatio)==0)
        mutationRatio = (int) (1 / configuration.getMutationRatio());
    }

    /**
     * Randomizes cities in chromosome
     *
     * @param <T>
     * @param cities
     */
    public static <T extends Node> void randomize(final T[] cities) {

        final int length = cities.length;

        //make sure that each city is swapped at leas once
        //else there could be created lots of similar chromosomes
        for (int i = 0; i < length; i++) {
            int i1 = i;
            int i2 = Manager.getRandom(length);
            if (i2 == i1) {
                if (i2 > 0) {
                    i2--;
                } else {
                    i2++;
                }
            }
            T swap = cities[i1];
            cities[i1] = cities[i2];
            cities[i2] = swap;
        }

        //randomize all the set more
        int randomizerSteps = 10 * length;

        //do the randomization of cities
        for (int n = 0; n < randomizerSteps; n++) {
            int i1 = Manager.getRandom(length);
            int i2 = Manager.getRandom(length);
            if (i1 != i2) {
                T swap = cities[i1];
                cities[i1] = cities[i2];
                cities[i2] = swap;
            }
        }
    }

    /**
     * @see org.saiko.ai.genetics.tsp.TSPEngine#getPopulationSize
     */
    @Override
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * @see org.saiko.ai.genetics.tsp.TSPEngine#getBestChromosome
     */
    @Override
    public TSPChromosome getBestChromosome() {
        return population.get(0);
    }

    /**
     * Orders population of chromosomes according to the costs of chromosomes in
     * ascending order
     */
    public void orderPopulation() {
        Collections.sort(population, new Comparator<TSPChromosome<T>>() {
            @Override
            public int compare(TSPChromosome<T> o1, TSPChromosome<T> o2) {
                double cost1 = o1.getTotalCost();
                double cost2 = o2.getTotalCost();
                return (cost1 < cost2 ? -1 : (cost1 > cost2 ? 1 : 0));
            }
        });
    }

    /**
     * @see org.saiko.ai.genetics.tsp.TSPEngine#nextGeneration
     */
    @Override
    public void nextGeneration() {
        //the best is the first half of population 
        final int bestCount = (int) (populationSize * 0.5);

        //leave only the best part of population
        int size = population.size();
        while (size > bestCount) {
            population.remove(size - 1);
            size--;
        }

        //mutate from the first half of population
        final List<Thread> runningThreads = Collections.synchronizedList(new ArrayList<>());

        //this has to be computed again as availableProcessors() can change in the time
        int maxThreadCount = Runtime.getRuntime().availableProcessors() * 2;
        //Adjust maxThreadCount to never be higher than settings allow
        if (maxThreadCount > configuration.getThreadMaxCount() && configuration.getThreadMaxCount() > 0) {
            maxThreadCount = configuration.getThreadMaxCount();
        }
        //it does not matter f countPerThread will not be exact
        final int countPerThread = bestCount / maxThreadCount;
        //create threads
        for (int i = 0; i < maxThreadCount; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int index = 0; index < countPerThread; index++) {
                        growPopulation(bestCount);
                    }
                }
            });
            runningThreads.add(thread);
        }
        //start threads
        for (Thread thread : runningThreads) {
            thread.start();
        }
        //wait for threads to fonish
        for (Thread thread : runningThreads) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                //No problem
            }
        }

        //now, order the chromosomes according to the costs,
        orderPopulation();

        //now align the population size
        //remove the last (worst) part of population
        //the mutation can create more elements than there should 
        //remain in the population
        size = population.size();
        while (size > populationSize) {
            population.remove(size - 1);
            size--;
        }

        //if specified by TSP_POPULATION_GROW,
        //grow the population
        populationSize = (int) (populationSize * (1 + configuration.getPopulationGrow()));
    }

    /**
     * Create childs from the bestCount elements of population
     *
     * @param bestCount
     */
    protected void growPopulation(int bestCount) {
        //the child here is created only from unisex adaptation
        getChild(population.get(Manager.getRandom(bestCount)));
    }

    /**
     * Creates new randomly mutated chromosome from its parent. This is the most
     * simple unisex genetic mutation algorithm.
     *
     * @param parent
     */
    protected void getChild(TSPChromosome<T> parent) {

        //clone the cities to new array
        T newCities[] = parent.getCities().clone();

        //aply random swaping to cities
        mutate(newCities);

        //add new chromosome to population
        population.add(new TSPChromosome<>(newCities, loop));
    }

    /**
     * Mutate randomly the cities (Chromosome) this is dependent on mutation
     * ratio set from TSP
     *
     * @param cities
     */
    protected void mutate(T cities[]) {
        if (Manager.getRandom(mutationRatio) == 0) {
            //randomly mutate two items in the chromosome
            int i1 = Manager.getRandom(cities.length);
            int i2 = Manager.getRandom(cities.length);
            if (i1 == i2) {
                if (i2 > 0) {
                    i2--;
                } else {
                    i2++;
                }
            }
            T swap = cities[i1];
            cities[i1] = cities[i2];
            cities[i2] = swap;
        }
    }
}