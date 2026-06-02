package secondBrain.mainFunctions;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;

import secondBrain.service.Node;
import secondBrain.services.NodeSearchService;
import secondBrain.services.NodeService;
import secondBrain.services.ProjectService;

public class SearchView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JComboBox<String> comboBox;
	private JButton searchButton;
	private JList<String> resultsList;
	private DefaultListModel<String> listModel;
	private List<Node> searchResults;
	private int projectId;
	private int userId;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					int userId = 1;
					ProjectService projectService = new ProjectService();
					int projectId = projectService.getOrCreateDefaultProject(userId);
					
					if (projectId > 0) {
						SearchView frame = new SearchView(userId, projectId);
						frame.setVisible(true);
					} else {
						System.err.println("Error: Could not get project for user");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	

	public SearchView(int userId, int projectId) {
		this.userId = userId;
		this.projectId = projectId;
		this.searchResults = new ArrayList<>();

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 550);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Search a node");
		lblNewLabel.setForeground(SystemColor.textHighlight);
		lblNewLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));
		lblNewLabel.setBounds(61, 36, 179, 25);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Search a node by text, by tags and files:");
		lblNewLabel_1.setFont(new Font("Segoe UI", Font.ITALIC, 13));
		lblNewLabel_1.setBounds(30, 96, 235, 14);
		contentPane.add(lblNewLabel_1);

		comboBox = new JComboBox<>(new String[] { "By Text", "By Tags", "By Files" });
		comboBox.setBounds(450, 121, 120, 25);
		contentPane.add(comboBox);

		textField = new JTextField();
		textField.setBounds(30, 121, 410, 25);
		contentPane.add(textField);
		textField.setColumns(10);

		searchButton = new JButton("Search");
		searchButton.setBounds(30, 160, 540, 30);
		searchButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		contentPane.add(searchButton);

		JLabel resultsLabel = new JLabel("Results:");
		resultsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		resultsLabel.setBounds(30, 205, 100, 20);
		contentPane.add(resultsLabel);

		listModel = new DefaultListModel<>();
		resultsList = new JList<>(listModel);
		resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultsList.setFont(new Font("Segoe UI", Font.PLAIN, 12));

		JScrollPane scrollPane = new JScrollPane(resultsList);
		scrollPane.setBounds(30, 230, 540, 280);
		contentPane.add(scrollPane);

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				performSearch();
			}
		});
	}



	private void performSearch() {
	    String searchInput = textField.getText().trim();
	    String searchType = (String) comboBox.getSelectedItem();
	 
	    if (searchInput.isEmpty()) {
	        listModel.clear();
	        listModel.addElement("Please enter a search term");
	        return;
	    }
	 
	    try {
	        NodeSearchService nodeSearchService = new NodeSearchService();
	        searchResults.clear();
	        listModel.clear();
	 
	        System.out.println("Searching by: " + searchType);
	        System.out.println("Search input: " + searchInput);
	        System.out.println("Project ID: " + projectId);
	 
	        if ("By Text".equals(searchType)) {
	            searchResults = nodeSearchService.searchByText(searchInput, projectId);
	        } else if ("By Tags".equals(searchType)) {
	            List<String> tags = parseTagsInput(searchInput);
	            searchResults = nodeSearchService.searchByTags(tags, projectId);
	        } else if ("By Files".equals(searchType)) {
	            searchResults = nodeSearchService.searchByFiles(searchInput, projectId);
	        }
	 
	        System.out.println("Results found: " + searchResults.size());
	 
	        if (searchResults.isEmpty()) {
	            listModel.addElement("No results found");
	        } else {
	            for (Node node : searchResults) {
	                String displayText = node.getId() + " - " + node.getTitle();
	                listModel.addElement(displayText);
	            }
	        }
	 
	    } catch (ClassNotFoundException | SQLException e) {
	        System.err.println("Error during search: " + e.getMessage());
	        e.printStackTrace();
	        listModel.clear();
	        listModel.addElement("Error: " + e.getMessage());
	    }
	}
	

	private List<String> parseTagsInput(String input) {
		List<String> tags = new ArrayList<>();
		String[] tagArray = input.split(",");
		for (String tag : tagArray) {
			String trimmed = tag.trim();
			if (!trimmed.isEmpty()) {
				tags.add(trimmed);
			}
		}
		return tags;
	}

	
	public Node getSelectedNode() {
		int selectedIndex = resultsList.getSelectedIndex();
		if (selectedIndex >= 0 && selectedIndex < searchResults.size()) {
			return searchResults.get(selectedIndex);
		}
		return null;
	}
	

}
