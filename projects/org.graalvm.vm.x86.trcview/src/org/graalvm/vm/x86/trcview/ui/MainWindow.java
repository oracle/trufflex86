/*
 * Copyright (c) 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.vm.x86.trcview.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import org.graalvm.vm.util.log.Trace;
import org.graalvm.vm.util.ui.MessageBox;
import org.graalvm.vm.x86.node.debug.trace.ExecutionTraceReader;
import org.graalvm.vm.x86.trcview.io.BlockNode;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
    private static final Logger log = Trace.create(MainWindow.class);

    public static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    private JLabel status;
    private TraceView view;

    private JMenuItem open;

    public MainWindow() {
        super("TRCView");

        FileDialog load = new FileDialog(this, "Open...", FileDialog.LOAD);

        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, view = new TraceView(this::setStatus));
        add(BorderLayout.SOUTH, status = new JLabel(""));

        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        open = new JMenuItem("Open...");
        open.setMnemonic('O');
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        open.addActionListener(e -> {
            load.setVisible(true);
            if (load.getFile() == null) {
                return;
            }
            String filename = load.getDirectory() + load.getFile();
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        load(new File(filename));
                    } catch (IOException ex) {
                        MessageBox.showError(MainWindow.this, ex);
                    }
                    return null;
                }
            };
            worker.execute();
        });
        JMenuItem exit = new JMenuItem("Exit");
        exit.setMnemonic('x');
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
        exit.addActionListener(e -> exit());
        fileMenu.add(open);
        fileMenu.addSeparator();
        fileMenu.add(exit);
        fileMenu.setMnemonic('F');
        menu.add(fileMenu);
        setJMenuBar(menu);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
    }

    public void setStatus(String text) {
        status.setText(text);
    }

    public void load(File file) throws IOException {
        log.info("Loading file " + file + "...");
        open.setEnabled(false);
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            long size = file.length();
            String text = "Loading " + file;
            setStatus(text);
            ExecutionTraceReader reader = new ExecutionTraceReader(in);
            BlockNode root = BlockNode.read(reader, pos -> setStatus(text + " (" + (pos * 100L / size) + "%)"));
            if (root == null || root.getFirstStep() == null) {
                setStatus("Loading failed");
                return;
            }
            setStatus("Trace loaded");
            EventQueue.invokeLater(() -> view.setRoot(root));
        } catch (Throwable t) {
            log.info("Loading failed: " + t);
            setStatus("Loading failed: " + t);
            throw t;
        } finally {
            open.setEnabled(true);
        }
        log.info("File loaded");
    }

    private void exit() {
        dispose();
        System.exit(0);
    }

    public static void main(String[] args) {
        Trace.setup();
        MainWindow w = new MainWindow();
        w.setVisible(true);
    }
}
