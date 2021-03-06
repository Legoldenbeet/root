/*
 * Copyright 2005 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.mcwin;

import com.jtattoo.plaf.BaseScrollPaneUI;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;

/**
 * @author Michael Hagen
 */
public class McWinScrollPaneUI extends BaseScrollPaneUI {

    public static ComponentUI createUI(JComponent c) {
        return new McWinScrollPaneUI();
    }

    public void installDefaults(JScrollPane p) {
        super.installDefaults(p);
        p.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
    }
}
