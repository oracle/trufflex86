package org.graalvm.vm.x86.trcview.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
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

import org.graalvm.vm.x86.node.debug.trace.ExecutionTraceReader;
import org.graalvm.vm.x86.trcview.io.BlockNode;

import com.everyware.util.log.Trace;
import com.everyware.util.ui.MessageBox;

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
        try (InputStream in = new FileInputStream(file)) {
            setStatus("Loading " + file);
            ExecutionTraceReader reader = new ExecutionTraceReader(in);
            BlockNode root = BlockNode.read(reader);
            if (root == null || root.getFirstStep() == null) {
                setStatus("Loading failed");
                return;
            }
            setStatus("Trace loaded");
            EventQueue.invokeLater(() -> view.setRoot(root));
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
