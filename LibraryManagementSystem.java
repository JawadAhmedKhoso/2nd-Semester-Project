package LibraryManagementSystem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class LibraryManagementSystem extends JFrame {

    CardLayout cardLayout;
    JPanel mainPanel;

    DefaultTableModel bookModel;
    DefaultTableModel issueModel;

    public LibraryManagementSystem() {
        setTitle("Library Management System");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(33, 37, 41));
        sidebar.setLayout(new GridLayout(6, 1, 10, 10));

        JButton dashboardBtn = createMenuButton("Dashboard");
        JButton addBookBtn = createMenuButton("Add Book");
        JButton issueBookBtn = createMenuButton("Issue Book");
        JButton returnBookBtn = createMenuButton("Return Book");

        sidebar.add(dashboardBtn);
        sidebar.add(addBookBtn);
        sidebar.add(issueBookBtn);
        sidebar.add(returnBookBtn);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createDashboardPanel(), "dashboard");
        mainPanel.add(createAddBookPanel(), "addBook");
        mainPanel.add(createIssuePanel(), "issueBook");
        mainPanel.add(createReturnPanel(), "returnBook");

        dashboardBtn.addActionListener(e -> cardLayout.show(mainPanel, "dashboard"));
        addBookBtn.addActionListener(e -> cardLayout.show(mainPanel, "addBook"));
        issueBookBtn.addActionListener(e -> cardLayout.show(mainPanel, "issueBook"));
        returnBookBtn.addActionListener(e -> cardLayout.show(mainPanel, "returnBook"));

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(100, 100, 100));
        btn.setFocusPainted(false);
        return btn;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Library Dashboard", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(title, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 20, 40));

        JLabel totalBooks = createCard("Total Books: 0", new Color(52, 152, 219));
        JLabel issuedBooks = createCard("Issued Books: 0", new Color(231, 76, 60));
        JLabel students = createCard("Total Students: 0", new Color(46, 204, 113));

        statsPanel.add(totalBooks);
        statsPanel.add(issuedBooks);
        statsPanel.add(students);

        panel.add(statsPanel, BorderLayout.NORTH);

        JTable table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            totalBooks.setText("Total Books: " + (bookModel != null ? bookModel.getRowCount() : 0));
            issuedBooks.setText("Issued Books: " + (issueModel != null ? issueModel.getRowCount() : 0));

            if (issueModel != null) {
                Set<String> uniqueStudents = new HashSet<>();
                for (int i = 0; i < issueModel.getRowCount(); i++) {
                    uniqueStudents.add(issueModel.getValueAt(i, 0).toString());
                }
                students.setText("Total Students: " + uniqueStudents.size());
            }
        });
        timer.start();

        totalBooks.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (bookModel != null) table.setModel(bookModel);
            }
        });

        issuedBooks.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (issueModel != null) table.setModel(issueModel);
            }
        });

        students.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (issueModel != null) {
                    DefaultTableModel studentModel = new DefaultTableModel(
                            new String[]{"Student Name"}, 0);

                    Set<String> uniqueStudents = new HashSet<>();
                    for (int i = 0; i < issueModel.getRowCount(); i++) {
                        String name = issueModel.getValueAt(i, 0).toString();
                        if (!uniqueStudents.contains(name)) {
                            uniqueStudents.add(name);
                            studentModel.addRow(new Object[]{name});
                        }
                    }

                    table.setModel(studentModel);
                }
            }
        });

        return panel;
    }

    private JLabel createCard(String text, Color color) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setOpaque(true);
        label.setBackground(color);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return label;
    }

    private JPanel createAddBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        bookModel = new DefaultTableModel(
                new String[]{"Book ID", "Title", "Author"}, 0);

        JTable table = new JTable(bookModel);

        JPanel form = new JPanel();

        JTextField id = new JTextField(10);
        JTextField title = new JTextField(10);
        JTextField author = new JTextField(10);
        JButton add = new JButton("Add Book");

        form.add(new JLabel("ID"));
        form.add(id);
        form.add(new JLabel("Title"));
        form.add(title);
        form.add(new JLabel("Author"));
        form.add(author);
        form.add(add);

        add.addActionListener(e -> {
            String bookId = id.getText().trim();
            String bookTitle = title.getText().trim();
            String bookAuthor = author.getText().trim();

            if (bookId.isEmpty() || bookTitle.isEmpty() || bookAuthor.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "All fields are required!");
                return;
            }

            if (!bookId.matches("\\d+")) {
                JOptionPane.showMessageDialog(panel, "Book ID must be a number!");
                return;
            }

            bookModel.addRow(new Object[]{bookId, bookTitle, bookAuthor});

            id.setText("");
            title.setText("");
            author.setText("");
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createIssuePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        issueModel = new DefaultTableModel(
                new String[]{"Student Name", "CMS", "Department", "Semester", "Book ID", "Issue Date", "Action"}, 0);

        JTable table = new JTable(issueModel);

        JPanel form = new JPanel();

        JTextField name = new JTextField(8);
        JTextField cms = new JTextField(8);
        JTextField dept = new JTextField(8);
        JTextField sem = new JTextField(5);
        JTextField bookId = new JTextField(5);

        JButton issue = new JButton("Issue Book");

        form.add(new JLabel("Name"));
        form.add(name);
        form.add(new JLabel("CMS"));
        form.add(cms);
        form.add(new JLabel("Dept"));
        form.add(dept);
        form.add(new JLabel("Semester"));
        form.add(sem);
        form.add(new JLabel("Book ID"));
        form.add(bookId);
        form.add(issue);

        issue.addActionListener(e -> {

            String n = name.getText().trim();
            String c = cms.getText().trim();
            String d = dept.getText().trim();
            String s = sem.getText().trim();
            String b = bookId.getText().trim();

            if (n.isEmpty() || c.isEmpty() || d.isEmpty() || s.isEmpty() || b.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "All fields are required!");
                return;
            }

            if (!c.matches("\\d+") || !s.matches("\\d+") || !b.matches("\\d+")) {
                JOptionPane.showMessageDialog(panel, "CMS, Semester, and Book ID must be numbers!");
                return;
            }

            // ✅ CHECK BOOK EXISTS
            boolean bookExists = false;
            for (int i = 0; i < bookModel.getRowCount(); i++) {
                String existingId = bookModel.getValueAt(i, 0).toString();
                if (existingId.equals(b)) {
                    bookExists = true;
                    break;
                }
            }

            if (!bookExists) {
                JOptionPane.showMessageDialog(panel, "Book ID does not exist!");
                return;
            }

            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

            issueModel.addRow(new Object[]{
                    n, c, d, s, b, date, "Return"
            });

            name.setText("");
            cms.setText("");
            dept.setText("");
            sem.setText("");
            bookId.setText("");
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReturnPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();
        JTextField searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search");

        topPanel.add(new JLabel("Search by Book ID: "));
        topPanel.add(searchField);
        topPanel.add(searchBtn);

        JTable table = new JTable(issueModel);

        TableColumn actionColumn = table.getColumn("Action");
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox(), issueModel));

        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().toLowerCase();
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(issueModel);
            table.setRowSorter(sorter);
            sorter.setRowFilter(RowFilter.regexFilter(keyword, 4));
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Return");
        }
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private DefaultTableModel model;
        private int row;

        public ButtonEditor(JCheckBox checkBox, DefaultTableModel model) {
            super(checkBox);
            this.model = model;
            button = new JButton("Return");

            button.addActionListener(e -> {
                model.removeRow(row);
                JOptionPane.showMessageDialog(null, "Book Returned Successfully!");
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            return button;
        }

        public Object getCellEditorValue() {
            return "Return";
        }
    }

    public static void main(String[] args) {
        new LibraryManagementSystem();
    }
}