package org.geppetto.model.sph.services;

import org.geppetto.core.model.state.AspectNode;
import org.geppetto.core.model.state.StateVariableNode;
import org.geppetto.core.model.state.visitors.DefaultStateVisitor;

public class PopulateModelTreeVisitor extends DefaultStateVisitor{

	private AspectNode _aspectNode;

	public PopulateModelTreeVisitor(AspectNode aspectNode, String id) {
		super();
		this._aspectNode = aspectNode;
	}
	
	/* (non-Javadoc)
	 * @see org.geppetto.core.model.state.visitors.DefaultStateVisitor#inCompositeStateNode(org.geppetto.core.model.state.CompositeStateNode)
	 */
	@Override
	public boolean inAspectNode(AspectNode node)
	{
		return super.inAspectNode(node);
	}

	/* (non-Javadoc)
	 * @see org.geppetto.core.model.state.visitors.DefaultStateVisitor#outCompositeStateNode(org.geppetto.core.model.state.CompositeStateNode)
	 */
	@Override
	public boolean outAspectNode(AspectNode node)
	{
		return super.outAspectNode(node);
	}

	/* (non-Javadoc)
	 * @see org.geppetto.core.model.state.visitors.DefaultStateVisitor#visitSimpleStateNode(org.geppetto.core.model.state.SimpleStateNode)
	 */
	@Override
	public boolean visitStateVariableNode(StateVariableNode node)
	{
		return super.visitStateVariableNode(node);
	}


}
