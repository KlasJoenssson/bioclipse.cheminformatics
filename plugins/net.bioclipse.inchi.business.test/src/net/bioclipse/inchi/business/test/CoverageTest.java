/*******************************************************************************
 * Copyright (c) 2008  Egon Willighagen <egonw@users.sf.net>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: Bioclipse Project <http://www.bioclipse.net>
 ******************************************************************************/
package net.bioclipse.inchi.business.test;

import net.bioclipse.core.api.managers.IBioclipseManager;
import net.bioclipse.core.tests.coverage.AbstractCoverageTest;
import net.bioclipse.inchi.business.IInChIManager;
import net.bioclipse.inchi.business.InChIManager;

/**
 * JUnit tests for checking if the tested Manager is properly tested.
 * 
 * @author egonw
 */
public class CoverageTest extends AbstractCoverageTest {
    
    private static InChIManager manager = new InChIManager();

    @Override
    public IBioclipseManager getManager() {
        return manager;
    }

    @Override
    public Class<? extends IBioclipseManager> getManagerInterface() {
        return IInChIManager.class;
    }

}