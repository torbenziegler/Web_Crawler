package hyperskill;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class WebCrawler extends JFrame {

    private final JPanel panel;
    private JTextField urlTextInput;
    private JLabel titleLabel;
    private DefaultTableModel titlesTableModel;


    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(550, 600));
        setTitle("Web Crawler");

        panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 500));

        setContentPane(panel);

        buildComponents();

        pack();
        setVisible(true);
    }

    private void buildComponents() {
        urlTextInput = new JTextField();
        urlTextInput.setName("UrlTextField");
        urlTextInput.setPreferredSize(new Dimension(420, 20));


        // RunButton
        JButton runButton = new JButton();
        runButton.setName("RunButton");
        runButton.setText("Run");
        runButton.setPreferredSize(new Dimension(80, 20));

        runButton.addActionListener(e -> {
            clearTitlesTable();
            try {
                final URL url = new URL(urlTextInput.getText());

                HTMLParser pageParser = new HTMLParser(url);
                pageParser.loadContent();

                titlesTableModel.addRow(
                        new String[]{url.toString(), pageParser.getTitle()}
                );


                titleLabel.setText(pageParser.getTitle());
                Map<String, String> links = pageParser.findLinks();

                for (String anUrl : links.keySet()) {
                    titlesTableModel.addRow(
                            new String[]{anUrl, links.get(anUrl)}
                    );
                }

            } catch (MalformedURLException exception) {
                System.out.println(exception.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        titleLabel = new JLabel("-", JLabel.LEFT);
        titleLabel.setName("TitleLabel");
        titleLabel.setPreferredSize(new Dimension(490, 20));

        titlesTableModel = new DefaultTableModel();
        titlesTableModel.addColumn("URL");
        titlesTableModel.addColumn("Title");

        JTable titlesTable = new JTable(titlesTableModel);
        titlesTable.setEnabled(false);
        titlesTable.setName("TitlesTable");
        titlesTable.setPreferredSize(new Dimension(490, 420));

        JScrollPane titlesTablePane = new JScrollPane(titlesTable);
        titlesTablePane.setPreferredSize(new Dimension(490, 420));


        // ExportLabel
        JLabel exportLabel = new JLabel("Export: ");
        exportLabel.setName("ExportLabel");
        exportLabel.setPreferredSize(new Dimension(490, 20));

        // ExportUrlTextField
        JTextField exportUrlTextField = new JTextField();
        exportUrlTextField.setName("ExportUrlTextField");
        exportUrlTextField.setPreferredSize(new Dimension(420, 20));

        // ExportButton
        JButton exportButton = new JButton("Save");
        exportButton.setName("ExportButton");
        exportButton.setPreferredSize(new Dimension(80, 20));

        exportButton.addActionListener(e -> {
            try {
                File file = new File(exportUrlTextField.getText());
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                PrintWriter fileWriter = new PrintWriter(bufferedWriter);

                //  fileWriter.println("");
                for (int i = 0; i < titlesTable.getRowCount(); ++i) {
                    for (int j = 0; j < titlesTableModel.getColumnCount(); ++j) {
                        String s = titlesTableModel.getValueAt(i, j).toString();
                        fileWriter.print(s);
                        fileWriter.println("");
                    }

                }
                fileWriter.close();
                JOptionPane.showMessageDialog(null, "File saved");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Failure");
            }
        });


        panel.add(titlesTablePane, FlowLayout.LEFT);
        panel.add(titleLabel, FlowLayout.LEFT);
        panel.add(runButton, FlowLayout.LEFT);
        panel.add(urlTextInput, FlowLayout.LEFT);

        add(exportLabel);
        add(exportUrlTextField);
        add(exportButton);
    }

    private void clearTitlesTable() {
        while (0 < titlesTableModel.getRowCount()) {
            titlesTableModel.removeRow(0);
        }
    }


}
