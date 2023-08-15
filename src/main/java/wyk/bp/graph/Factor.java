package wyk.bp.graph;

import wyk.bp.probtable.ProbabilityTable;

public class Factor implements FactorGraphNode {

    protected final ProbabilityTable table;

    public Factor(final ProbabilityTable table) {
        this.table = table;
    }

}
