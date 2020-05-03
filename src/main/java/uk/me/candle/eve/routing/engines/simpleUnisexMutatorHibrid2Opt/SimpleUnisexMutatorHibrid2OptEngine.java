/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/engines/simpleUnisexMutatorHibrid2Opt/SimpleUnisexMutatorHibrid2OptEngine.java,v $
 * $Id: SimpleUnisexMutatorHibrid2OptEngine.java,v 1.2 2005/08/23 23:18:05 dsaiko Exp $
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
 * $Log: SimpleUnisexMutatorHibrid2OptEngine.java,v $
 * Revision 1.2  2005/08/23 23:18:05  dsaiko
 * Finished.
 *
 * Revision 1.1  2005/08/22 22:13:53  dsaiko
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
package uk.me.candle.eve.routing.engines.simpleUnisexMutatorHibrid2Opt;

import uk.me.candle.eve.graph.Node;
import uk.me.candle.eve.routing.engines.TSPChromosome;
import uk.me.candle.eve.routing.engines.crossoverHibrid2opt.GreedyCrossoverHibrid2OptEngine;
import uk.me.candle.eve.routing.engines.simpleUnisexMutator.SimpleUnisexMutatorEngine;

/**
 * @author Dusan Saiko (dusan@saiko.cz) Last change $Date: 2005/08/23 23:18:05 $
 *
 * Class witch extends SimpleUnisexMutatorEngine and adds hibrid heuristics to
 * solve the Traveling Salesman Problem
 *
 * For heuristics, the 2opt mutation is used, as described at
 * http://www.gcd.org/sengoku/docs/arob98.pdf
 * @param <T>
 *
 * @see #getChild(TSPChromosome)
 * @see org.saiko.ai.genetics.tsp.engines.crossover.GreedyCrossoverEngine
 * @see org.saiko.ai.genetics.tsp.TSPEngine
 * @see org.saiko.ai.genetics.tsp.engines.crossover.GreedyCrossoverEngine
 */
public class SimpleUnisexMutatorHibrid2OptEngine<T extends Node> extends SimpleUnisexMutatorEngine<T> {

    /**
     * Creates new randomly mutated chromosome from its parent. This is the most
     * simple unisex genetic mutation algorithm, but this algorithm is combined
     * with 2opt heuristics
     *
     * @param parent
     */
    @Override
    protected void getChild(TSPChromosome<T> parent) {

        //clone the cities to new array
        T child1[] = parent.getCities().clone();

        //aply random swaping to cities
        mutate(child1);

        //addon
        GreedyCrossoverHibrid2OptEngine.heuristics2opt(child1);
        
        //add new chromosome to population
        population.add(new TSPChromosome<>(child1, loop));
    }
}