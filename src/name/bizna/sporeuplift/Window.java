/*

This file is part of SporeUplift.

SporeUplift is free software: you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation, either version 3 of the License, or (at your option) any later
version.

SporeUplift is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with
SporeUplift. If not, see <https://www.gnu.org/licenses/>. 

*/

package name.bizna.sporeuplift;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import name.bizna.sporeuplift.Framerates.FrameratePair;

public class Window extends JFrame {
    public Window() {
        super(SporeUplift.titleMessage("main_window"));
        this.setMinimumSize(new Dimension(400, 0));
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel contentPane = (JPanel)this.getContentPane();
            contentPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            BoxLayout boxLayout = new BoxLayout(contentPane, BoxLayout.Y_AXIS);
            contentPane.setLayout(boxLayout);
            // Test the tests and panel the panels.
            JPanel subpanel;
            for(int n = 0; n < Test.TESTS.length; ++n) {
                Test test = Test.TESTS[n];
                if(!test.isApplicable()) continue;
                subpanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
                String text;
                String uri = null;
                try {
                    if(test.test()) {
                        text = test.getPassText();
                        SporeUplift.stderrMessage("happy", text);
                        text = "<html><span style=\"color:#2f1\";font-weight:bold>✔</span> "+text+"</html>";
                    } else {
                        text = test.getFailText();
                        SporeUplift.stderrMessage("sad", text);
                        text = "<html><span style=\"color:#bb0\">⚠</span> "+text+"</html>";
                        uri = test.getFailURI();
                    }
                } catch(Exception e) {
                    SporeUplift.stderrMessage("exception.sniff_test");
                    System.err.println("");
                    e.printStackTrace();
                    text = "<html><span style=\"color:#f10\">✕</span> "
                        + SporeUplift.message("test.exception")
                        + "</html>";
                }
                final String finalUri = uri;
                JLabel label = new JLabel(text);
                if(finalUri != null) {
                    label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    label.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent _e) {
                            try {
                                Desktop.getDesktop().browse(new URI(finalUri));
                            } catch (Exception e) {
                                SporeUplift.uncaught(e);
                            }
                        }
                    });
                }
                subpanel.add(label);
                contentPane.add(subpanel);
            }
            // Four gig?
            if(FourGig.possible || FourGig.isSet) {
                subpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JCheckBox fourGigBox = new JCheckBox(uiText("4gb")) {
                    @Override public JToolTip createToolTip() {
                        return new JMultiLineToolTip();
                    }
                };
                setTooltip(fourGigBox, "4gb");
                if(FourGig.isSet && !FourGig.possible) {
                    fourGigBox.setSelected(true);
                    fourGigBox.setEnabled(false);
                } else {
                    fourGigBox.setSelected(SporeUplift.want4gb);
                    fourGigBox.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent _e) {
                            SporeUplift.want4gb = fourGigBox.isSelected();
                        }
                    });
                }
                subpanel.add(fourGigBox);
                contentPane.add(subpanel);
            } else {
                subpanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
                String text = SporeUplift.message("test.fail.4gb");
                SporeUplift.stderrMessage("sad", text);
                text = "<html><span style=\"color:#bb0\">⚠</span> "+text+"</html>";
                subpanel.add(new JLabel(text));
                contentPane.add(subpanel);
            }
            // Disable validation?
            subpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JCheckBox disableValidationBox = new JCheckBox(uiText("disable_validation")) {
                @Override public JToolTip createToolTip() {
                    return new JMultiLineToolTip();
                }
            };
            disableValidationBox.setSelected(SporeUplift.disableValidation);
            setTooltip(disableValidationBox, "disable_validation");
            disableValidationBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent _e) {
                    SporeUplift.disableValidation = disableValidationBox.isSelected();
                }
            });
            subpanel.add(disableValidationBox);
            contentPane.add(subpanel);
            // framerate selector
            GridBagLayout gridBagLayout = new GridBagLayout();
            GridBagConstraints gbc = new GridBagConstraints();
            if(SporeUplift.rtl) gbc.gridx = 1;
            subpanel = new JPanel(gridBagLayout);
            subpanel.add(new JLabel(uiText("target_framerate")));
            JComboBox<FrameratePair> framerateBox
                = new JComboBox<FrameratePair>(Framerates.FRAMERATES) {
                @Override public JToolTip createToolTip() {
                    return new JMultiLineToolTip();
                }
            };
            setTooltip(framerateBox, "target_framerate");
            framerateBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent _e) {
                    SporeUplift.frameLimitMS = ((FrameratePair)framerateBox.getSelectedItem()).delay;
                }
            });
            for(int n = 0; n < Framerates.FRAMERATES.length; ++n) {
                if(Framerates.FRAMERATES[n].delay == SporeUplift.frameLimitMS) {
                    framerateBox.setSelectedIndex(n);
                    break;
                }
            }
            gbc = new GridBagConstraints();
            if(!SporeUplift.rtl) gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            subpanel.add(framerateBox, gbc);
            contentPane.add(subpanel);
            // Skin texture size
            gridBagLayout = new GridBagLayout();
            subpanel = new JPanel(gridBagLayout);
            subpanel.add(new JLabel(uiText("skin_texture_size")));
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JLabel textureSizeLabel = new JLabel("<html><span style=\"color:#bb0\">⚠</span> 4096x4096</html>", SwingConstants.RIGHT) {
                @Override public JToolTip createToolTip() {
                    return new JMultiLineToolTip();
                }
            };
            subpanel.add(textureSizeLabel, gbc);
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JSlider textureSizeSlider = new JSlider(SporeUplift.MIN_SKINPAINT_TEXTURE_SIZE_BITS, SporeUplift.MAX_SKINPAINT_TEXTURE_SIZE_BITS) {
                @Override public JToolTip createToolTip() {
                    return new JMultiLineToolTip();
                }
            };
            for(int n = SporeUplift.MIN_SKINPAINT_TEXTURE_SIZE_BITS; n <= SporeUplift.MAX_SKINPAINT_TEXTURE_SIZE_BITS; ++n) {
                if(SporeUplift.skinpaintTextureSize <= 1<<n) {
                    textureSizeSlider.setValue(n);
                    break;
                }
            }
            setTooltip(textureSizeSlider, "texture_size");
            textureSizeSlider.setMajorTickSpacing(1);
            textureSizeSlider.setPaintLabels(false);
            textureSizeSlider.setPaintTicks(true);
            ChangeListener textureSizeChangeListener = new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent _e) {
                    int bits = textureSizeSlider.getValue();
                    int size = 1 << bits;
                    SporeUplift.skinpaintTextureSize = size;
                    String labelText = uiText("skinsize."+size);
                    if(bits >= SporeUplift.DANGEROUS_SKINPAINT_TEXTURE_SIZE_BITS) {
                        labelText = "<html><span style=\"color:#bb0\">⚠</span> "+labelText+"</html>";
                        setTooltip(textureSizeLabel, "dangerous_size");
                    } else {
                        textureSizeLabel.setToolTipText(null);
                    }
                    textureSizeLabel.setText(labelText);
                }
            };
            textureSizeSlider.addChangeListener(textureSizeChangeListener);
            textureSizeChangeListener.stateChanged(null);
            subpanel.add(textureSizeSlider, gbc);
            contentPane.add(subpanel);
            // Buttons! Buttons at the end!
            subpanel = new JPanel();
            JButton button = new JButton(uiText("button.restore")) {
                @Override public JToolTip createToolTip() {
                    return new JMultiLineToolTip();
                }
            };
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent _e) {
                    SporeUplift.restoreBackups();
                }
            });
            setTooltip(button, "restore");
            subpanel.add(button);
            subpanel.add(Box.createHorizontalStrut(10));
            button = new JButton(uiText("button.save_and_exit")) {
                @Override public JToolTip createToolTip() {
                    return new JMultiLineToolTip();
                }
            };
            setTooltip(button, "save_and_exit");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent _e) {
                    SporeUplift.saveAndExit();
                }
            });
            subpanel.add(button);
            contentPane.add(subpanel);
            // All layout complete
            this.pack();
            this.setLocationByPlatform(true);
            SporeUplift.stderrMessage("showing_ui");
            this.setVisible(true);
    }
    private static void setTooltip(JComponent comp, String key) {
        comp.setToolTipText(SporeUplift.message("ui.tooltip."+key));
    }
    private static String uiText(String key, Object... args) {
        return SporeUplift.message("ui."+key, args);
    }
}
