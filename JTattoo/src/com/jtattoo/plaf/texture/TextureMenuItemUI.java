/*
 * Copyright 2012 MH-Software-Entwicklung. All rights reserved.
 * Use is subject to license terms.
 */
package com.jtattoo.plaf.texture;

import com.jtattoo.plaf.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;

/**
 * @author Michael Hagen
 */
public class TextureMenuItemUI extends BaseMenuItemUI {

    public static ComponentUI createUI(JComponent c) {
        return new TextureMenuItemUI();
    }

    protected void paintBackground(Graphics g, JComponent c, int x, int y, int w, int h) {
        if (!AbstractLookAndFeel.getTheme().isDarkTexture()) {
            super.paintBackground(g, c, x, y, w, h);
            return;
        }
        JMenuItem b = (JMenuItem) c;
        ButtonModel model = b.getModel();
        if (model.isArmed() || (c instanceof JMenu && model.isSelected())) {
            TextureUtils.fillComponent(g, c, TextureUtils.ROLLOVER_TEXTURE_TYPE);
        } else {
            TextureUtils.fillComponent(g, c, TextureUtils.MENUBAR_TEXTURE_TYPE);
        }
    }

    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
        if (!AbstractLookAndFeel.getTheme().isDarkTexture()) {
            super.paintText(g, menuItem, textRect, text);
            return;
        }
	ButtonModel model = menuItem.getModel();
        FontMetrics fm = menuItem.getFontMetrics(menuItem.getFont());
	int mnemIndex = menuItem.getDisplayedMnemonicIndex();
        if (!menuItem.isArmed()) {
            g.setColor(Color.black);
            JTattooUtilities.drawStringUnderlineCharAt(menuItem, g, text, mnemIndex, textRect.x, textRect.y + fm.getAscent() - 1);
        }
	if (!model.isEnabled()) {
	    // *** paint the text disabled
            g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getDisabledForegroundColor(), 40));
 	} else {
	    // *** paint the text normally
            if (menuItem.isArmed()) {
                g.setColor(AbstractLookAndFeel.getMenuSelectionForegroundColor());
            } else {
                g.setColor(AbstractLookAndFeel.getMenuForegroundColor());
            }
	}
        JTattooUtilities.drawStringUnderlineCharAt(menuItem, g,text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
    }
}
