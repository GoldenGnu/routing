/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/engines/crossover/GreedyCrossoverEngine.java,v $
 * $Id: GreedyCrossoverEngine.java,v 1.3 2005/08/23 23:18:05 dsaiko Exp $
 * $Date: 2005/08/23 23:18:05 $
 * $Revision: 1.3 $
 * $Author: dsaiko $
 *
 * Travelingce is released under GNU public licence agreement.
 * dusan@saiko.cz Salesman Problem genetic algorithm.
 * This sour
 * http://www.saiko.cz/ai/tsp/
 * 
 * Change log:
 * $Log: GreedyCrossoverEngine.java,v $
 * Revision 1.3  2005/08/23 23:18:05  dsaiko
 * Finished.
 *
 * Revision 1.2  2005/08/22 22:13:53  dsaiko
 * Packages rearanged
 *
 * Revision 1.1  2005/08/22 22:08:51  dsaiko
 * Created engines with heuristics
 *
 * Revision 1.1  2005/08/13 15:02:09  dsaiko
 * build task
 *
 * Revision 1.1  2005/08/12 23:52:17  dsaiko
 * Initial revision created
 *
 */
package uk.me.candle.eve.routing.engines.crossover;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedList;
import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.engines.Manager;
import uk.me.candle.eve.routing.engines.TSPChromosome;
import uk.me.candle.eve.routing.engines.simpleUnisexMutatorHibrid2Opt.SimpleUnisexMutatorHibrid2OptEngine;

/**
 * @author Dusan Saiko (dusan@saiko.cz) Last change $Date: 2005/08/23 23:18:05 $
 *
 * Extends the SimpleUnisexMutatorEngine by providing new method for getting
 * childs to new population.
 *
 * For the population modification, GreeadyCrossover algorithm is taken and
 * adapted from org.jgap.impl.GreedyCrossover
 * @param <T>
 *
 * @see
 * org.saiko.ai.genetics.tsp.engines.simpleUnisexMutatorHibrid2Opt.SimpleUnisexMutatorHibrid2OptEngine
 * @see org.jgap.impl.GreedyCrossover
 * @see #getChild(TSPChromosome)
 * @see org.saiko.ai.genetics.tsp.TSPEngine
 */
public class GreedyCrossoverEngine<T extends Node> extends SimpleUnisexMutatorHibrid2OptEngine<T> {

    /**
     * Create childs from the bestCount elements of population Creates child
     * from two parent
     */
    @Override
    protected void growPopulation(int bestCount) {
        //randomly find the parent
        int i1 = Manager.getRandom(bestCount);
        int i2 = Manager.getRandom(bestCount);
        if (i1 == i2) {
            if (i2 > 0) {
                i2--;
            } else {
                i2++;
            }
        }
        //get child from parent
        getChild(population.get(i1), population.get(i2));
    }

    /**
     * Creates child from two parents using GreeadyCrossover algorithm. It
     * creates child from parent1+parent2; parent2+parent1; mutated parent1 and
     * mutated parent2
     *
     * @see #getChild(TSPChromosome, TSPChromosome)
     * @see SimpleUnisexMutatorHibrid2OptEngine#mutate(City[])
     * @param parent1
     * @param parent2
     */
    protected void getChild(TSPChromosome<T> parent1, TSPChromosome<T> parent2) {

        T child1[] = parent1.getCities().clone();
        T child2[] = parent2.getCities().clone();
        T child3[] = haveSex(parent1, parent2);
        T child4[] = haveSex(parent2, parent1);
        T child5[] = child3.clone();
        T child6[] = child4.clone();

        mutate(child1);
        mutate(child2);
        //mutate(child3);
        //mutate(child4);
        mutate(child5);
        mutate(child6);

        population.add(new TSPChromosome<>(child1, loop));
        population.add(new TSPChromosome<>(child2, loop));
        population.add(new TSPChromosome<>(child3, loop));
        population.add(new TSPChromosome<>(child4, loop));
        population.add(new TSPChromosome<>(child5, loop));
        population.add(new TSPChromosome<>(child6, loop));
    }

    /**
     * Creates one child from two parents applying the CrossOver algorithm for
     * genetic mating of chromosomes. This code is taken and adjusted from
     * rg.jgap.impl.GreedyCrossover
     *
     * In short, algorithm takes first city from parent1 and looks for the way
     * from this first city in both, parent1 and parent2. then it uses the
     * better next city
     *
     * @see org.jgap.impl.GreedyCrossover
     * @param chromosome1 - first chromosome
     * @param chromosome2 - second chromosome
     * @return newly ordered array of cities (=child =new chromosome)
     */
    protected T[] haveSex(TSPChromosome<T> chromosome1, TSPChromosome<T> chromosome2) {

        T[] c1 = chromosome1.getCities();
        T[] c2 = chromosome2.getCities();

        int n = c1.length;

        LinkedList<T> out = new LinkedList<>();
        LinkedList<T> not_picked = new LinkedList<>();

        out.add(c1[0]);
        for (int j = 1; j < n; j++) { // g[0] picked
            not_picked.add(c1[j]);
        }

        while (not_picked.size() > 1) {
            T last = out.getLast();
            T n1 = findNext(c1, last);
            T n2 = findNext(c2, last);

            T picked, other;

            boolean pick1;

            if (n1 == null) {
                pick1 = false;
            } else if (n2 == null) {
                pick1 = true;
            } else {
                pick1 = Manager.cost(last, n1) < Manager.cost(last, n2);
            }

            if (pick1) {
                picked = n1;
                other = n2;
            } else {
                picked = n2;
                other = n1;
            }

            if (out.contains(picked)) {
                picked = other;
            }
            if (picked == null || out /* still */.contains(picked)) {
                // select a non-selected // it is not random
                picked = not_picked.getFirst();
            }

            out.add(picked);
            not_picked.remove(picked);
        }

        out.add(not_picked.getLast());

        T[] c = (T[]) Array.newInstance(out.get(0).getClass(), n);
        Iterator<T> gi = out.iterator();

        for (int i = 0; i < 0; i++) {
            c[i] = c1[i];
        }

        for (int i = 0; i < c.length; i++) {
            c[i] = gi.next();
        }

        return c;

    }

    /**
     * Helper for GreedyCrossover getChild() algorithm. It finds the next city
     * after city "x" in the chromosome "cities"
     *
     * @param cities - array in which to find the next city after city "x"
     * @param x - city for which we are looking for the next path
     * @return next city to go from the chromosome
     */
    protected T findNext(T[] cities, T x) {
        for (int i = 0; i < cities.length - 1; i++) {
            if (cities[i].equals(x)) {
                return cities[i + 1];
            }
        }
        //from the last city we go to the first one
        return cities[0];
    }
}