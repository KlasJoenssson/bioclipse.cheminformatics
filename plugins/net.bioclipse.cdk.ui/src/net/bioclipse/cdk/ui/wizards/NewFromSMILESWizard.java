/*******************************************************************************
 * Copyright (c) 2008-2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/
package net.bioclipse.cdk.ui.wizards;

import net.bioclipse.cdk.business.ICDKManager;
import net.bioclipse.cdk.domain.CDKMolecule;
import net.bioclipse.cdk.domain.ICDKMolecule;
import net.bioclipse.cdk.ui.Activator;
import net.bioclipse.core.util.LogUtils;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.DeduceBondSystemTool;

public class NewFromSMILESWizard extends BasicNewResourceWizard {

    private static final Logger logger =
        Logger.getLogger(NewFromSMILESWizard.class);

    public static final String WIZARD_ID =
        "net.bioclipse.cdk.ui.wizards.NewFromSMILESWizard"; //$NON-NLS-1$
    
    private SMILESInputWizardPage mainPage;
    
    private String smiles = null;
    
    public void setSMILES(String smiles) {
        this.smiles = smiles;
    }

    public String getSMILES() {
        return smiles;
    }

    public boolean canFinish() {
        return getSMILES() != null;
    }
    
    public void addPages() {
        super.addPages();
        mainPage = new SMILESInputWizardPage("newFilePage0", this);//$NON-NLS-1$
        mainPage.setTitle("Open SMILES");
        mainPage.setDescription("Create a new resource from a SMILES"); 
        addPage(mainPage);
    }

    public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
        setWindowTitle("New Molecule From SMILES");
        setNeedsProgressMonitor(true);
    }

    public boolean performFinish() {
        //Open editor with content (String) as content
        ICDKManager cdk = net.bioclipse.cdk.business.Activator.getDefault().getJavaCDKManager();
        try {
            ICDKMolecule mol = cdk.fromSMILES(getSMILES());
            IMoleculeSet containers =
            	ConnectivityChecker.partitionIntoMolecules(mol.getAtomContainer()
            );
            for (IAtomContainer container : containers.molecules()) {
            	// ok, try to do something smart (tm) with SMILES input:
            	//   try to resolve bond orders
            	DeduceBondSystemTool tool = new DeduceBondSystemTool();
            	org.openscience.cdk.interfaces.IMolecule cdkMol =
            		asCDKMolecule(container);
            	cdkMol = tool.fixAromaticBondOrders(cdkMol);
            	mol = new CDKMolecule(cdkMol);

            	mol = cdk.generate2dCoordinates(mol);
            	CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(
            			NoNotificationChemObjectBuilder.getInstance()
            	);
            	IAtomType[] types = matcher.findMatchingAtomType(
            			mol.getAtomContainer()
            	);
            	for (int i=0; i<types.length; i++) {
            		if (types[i] != null) {
            			mol.getAtomContainer().getAtom(i).setAtomTypeName(
            					types[i].getAtomTypeName()
            			);
            		}
            	}
            	net.bioclipse.ui.business.Activator.getDefault().getUIManager()
            		.open(mol, "net.bioclipse.cdk.ui.editors.jchempaint.cml");
            }
        } catch (Exception e) {
            LogUtils.handleException(
                e, logger,
                Activator.PLUGIN_ID
            );
        }
        return true;
    }

    /**
     * Converts (if needed) a CDK {@link IAtomContainer} into a CDK
     * {@link IMolecule}.
     */
	private org.openscience.cdk.interfaces.IMolecule
	    asCDKMolecule(IAtomContainer container) {
		if (container instanceof org.openscience.cdk.interfaces.IMolecule)
			return (org.openscience.cdk.interfaces.IMolecule)container;

		return container.getBuilder().newInstance(
			org.openscience.cdk.interfaces.IMolecule.class, container
		);
	}
    
}
