/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.scala.platform.wizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemListener;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.scala.platform.InstallerRegistry;
import org.netbeans.spi.scala.platform.CustomPlatformInstall;
import org.netbeans.spi.scala.platform.GeneralPlatformInstall;
import org.openide.WizardDescriptor;
import org.openide.loaders.TemplateWizard;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author  Tomas Zezula
 */
class SelectorPanel extends javax.swing.JPanel implements ItemListener {
        
    private Map<ButtonModel,GeneralPlatformInstall> installersByButtonModels = new IdentityHashMap<ButtonModel,GeneralPlatformInstall>();
    private ButtonGroup group;
    private SelectorPanel.Panel firer;
    
    /** Creates new form SelectorPanel */
    public SelectorPanel(SelectorPanel.Panel firer) {
        this.firer = firer;
        initComponents();
        postInitComponents ();
        this.setName (NbBundle.getMessage(SelectorPanel.class,"TXT_SelectPlatformTypeTitle"));
        this.getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(SelectorPanel.class,"AD_SelectPlatformType"));
    }
    
    private void postInitComponents () {
        InstallerRegistry regs = InstallerRegistry.getDefault();
        List<GeneralPlatformInstall> installers = regs.getAllInstallers();
        this.group = new ButtonGroup ();        
        JLabel label = new JLabel (NbBundle.getMessage(SelectorPanel.class,"TXT_SelectPlatform"));
        label.setDisplayedMnemonic(NbBundle.getMessage(SelectorPanel.class,"AD_SelectPlatform").charAt(0));        
        GridBagConstraints c = new GridBagConstraints ();
        c.gridx = c.gridy = GridBagConstraints.RELATIVE;
        c.gridheight = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;
        c.insets = new Insets (12, 12, 6, 12);
        ((GridBagLayout)this.getLayout()).setConstraints(label,c);
        this.add (label);
        Iterator<GeneralPlatformInstall> it = installers.iterator();
        for (int i=0; it.hasNext(); i++) {
            GeneralPlatformInstall pi = it.next ();            
            JRadioButton button = new JRadioButton (pi.getDisplayName());
            if (i==0) {
                label.setLabelFor(button);
            }
            button.addItemListener(this);
            this.installersByButtonModels.put (button.getModel(), pi);
            this.group.add(button);
            c = new GridBagConstraints ();
            c.gridx = c.gridy = GridBagConstraints.RELATIVE;
            c.gridheight = 1;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.NORTHWEST;
            c.weightx = 1.0;
            c.insets = new Insets (6, 18, it.hasNext()? 0 : 12, 12);
            ((GridBagLayout)this.getLayout()).setConstraints(button,c);
            this.add (button);
        }
        JPanel pad = new JPanel ();
        c = new GridBagConstraints ();
        c.gridx = c.gridy = GridBagConstraints.RELATIVE;
        c.gridheight = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets (12,0,0,12);
        ((GridBagLayout)this.getLayout()).setConstraints(pad,c);
        this.add (pad);
    }
    
    private void readSettings () {
        if (this.group.getSelection()==null) {
            java.util.Enumeration buttonEnum = this.group.getElements();
            assert buttonEnum.hasMoreElements();
            ((JRadioButton)buttonEnum.nextElement()).setSelected(true);
        }
    }

    public void itemStateChanged(java.awt.event.ItemEvent e) {
        this.firer.cs.fireChange();
    }
    
    
    /**
     * Used by unit tests
     * Select the GeneralPlatformInstall to allow step over this panel
     */
    boolean selectInstaller (GeneralPlatformInstall install) {
        assert install != null;
        for (Map.Entry<ButtonModel,GeneralPlatformInstall> entry : installersByButtonModels.entrySet()) {
            if (entry.getValue().equals(install)) {
                ButtonModel model = entry.getKey();
                model.setSelected(true);
                return true;
            }
        }
        return false;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.GridBagLayout());

    }
    // </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
    
    public static class Panel implements WizardDescriptor.Panel<WizardDescriptor> {
        
        private final ChangeSupport cs = new ChangeSupport(this);
        private SelectorPanel component;
        
        public synchronized void removeChangeListener(ChangeListener l) {
            cs.removeChangeListener(l);
        }

        public synchronized void addChangeListener(ChangeListener l) {
            cs.addChangeListener(l);
        }                

        public void readSettings(WizardDescriptor wiz) {
            getComponent().readSettings();
        }

        public void storeSettings(WizardDescriptor wiz) {
        }

        public HelpCtx getHelp() {
            return new HelpCtx (SelectorPanel.class);
        }

        public boolean isValid() {
            return this.component != null;
        }

        public SelectorPanel getComponent() {
            if (this.component == null) {
                this.component = new SelectorPanel (this);
            }
            return this.component;
        }
        
        public GeneralPlatformInstall getInstaller () {
            SelectorPanel c = getComponent ();
            ButtonModel bm = c.group.getSelection();
            if (bm != null) {            
                return c.installersByButtonModels.get(bm);
            }
            return null;
        }
        
        public TemplateWizard.InstantiatingIterator getInstallerIterator () {
            GeneralPlatformInstall platformInstall = getInstaller ();
            if (platformInstall instanceof CustomPlatformInstall) {
                return ((CustomPlatformInstall)platformInstall).createIterator();
            }
            return null;
        }
        
    }
}
