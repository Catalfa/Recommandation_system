
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
   static Neo4J connection = new Neo4J();
   public static void main(String[] args) {
      //showCategories();
      selectSuggestionMethod();
   }

   //Main menu
   private static void selectSuggestionMethod()
   {
      // Creazione della finestra principale
      final JFrame frame = new JFrame("Homepage");
      frame.setSize(400, 200);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // Creazione del layout
      BorderLayout layout = new BorderLayout();
      frame.setLayout(layout);

      // Creazione del pannello di benvenuto
      JPanel welcomePanel = new JPanel();
      welcomePanel.setBackground(Color.LIGHT_GRAY);
      JLabel welcomeLabel = new JLabel("¿ Que Película ?");
      welcomeLabel.setFont(new Font("Arial", Font.BOLD, 35));
  
      welcomePanel.add(welcomeLabel);
      frame.add(welcomePanel, BorderLayout.NORTH);

      // Creazione del pannello del contenuto
      JPanel contentPanel = new JPanel();
      contentPanel.setBackground(Color.WHITE);
      frame.add(contentPanel, BorderLayout.CENTER);

      JButton suggestCategoryButton = new JButton("Suggest by Category");
      suggestCategoryButton.setSize(25, 10);
      suggestCategoryButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            frame.setVisible(false);
            showCategories();
         }
      });
      contentPanel.add(suggestCategoryButton);
      //panel.add(Box.createHorizontalGlue());

      JButton suggestMovieButton = new JButton("Suggest by movie");
      suggestMovieButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            frame.setVisible(false);
            showMovies();
         }
      });
      contentPanel.add(suggestMovieButton);

      contentPanel.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();

      c.weightx = 0;
      c.weighty = 0;
      c.anchor = GridBagConstraints.CENTER;

      c.gridx = -1;
      c.gridy = 1;
      contentPanel.add(suggestCategoryButton, c);

      c.gridx = 1;
      c.gridy = 1;
      contentPanel.add(suggestMovieButton, c);

      // Creazione del pannello del footer
      JPanel footerPanel = new JPanel();
      footerPanel.setBackground(Color.LIGHT_GRAY);
      JLabel footerLabel = new JLabel("Copyright © 2023 - Catalfamo Rosario");
      footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
      footerPanel.add(footerLabel);
      frame.add(footerPanel, BorderLayout.SOUTH);


      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setLocationRelativeTo(null);
      // Visualizzazione della finestra
      frame.setVisible(true);
   }

   //Suggestion selecting a category
   private static void showCategories()
   {
      final JFrame frame = new JFrame("Movie Suggestion");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(400, 600);
      frame.setLocationRelativeTo(null);
      frame.setBackground(Color.gray);

      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      panel.setBackground(Color.gray);

      List < String > categories = collectCategories();
      DefaultListModel < String > categoriesListModel = new DefaultListModel < > ();
      for (String category: categories) {
         categoriesListModel.addElement(category);
      }
      final JList < String > categoriesList = new JList < > (categoriesListModel);
      JScrollPane scrollPane = new JScrollPane(categoriesList);
      panel.add(scrollPane);

      //Suggestion button
      JButton suggestButton = new JButton("Suggest");
      suggestButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            String selectedCategory = categoriesList.getSelectedValue();
            final List < String > movies = requestSuggestion(selectedCategory);
            frame.setTitle(selectedCategory + " Movies");
            panel.removeAll();

            //create a new JList with the updated list of strings
            DefaultListModel < String > newStringListModel = new DefaultListModel < > ();
            for (String movie: movies) {
               newStringListModel.addElement(movie);
            }
            final JList < String > suggestedMovies = new JList < > (newStringListModel);
            JScrollPane scrollPane = new JScrollPane(suggestedMovies);
            

            final JTextField searchBar = new JTextField();
            searchBar.setMaximumSize(new Dimension(250, 25));
            
            panel.add(searchBar);
            panel.add(scrollPane);
            searchBar.getDocument().addDocumentListener(new DocumentListener() {
               public void changedUpdate(DocumentEvent e) {
                  filterList();
               }
               public void removeUpdate(DocumentEvent e) {
                  filterList();
               }
               public void insertUpdate(DocumentEvent e) {
                  filterList();
               }

               public void filterList() {
                  String filterText = searchBar.getText();
                  List<String> filteredMovies = new ArrayList<>();
                  for (String movie : movies) {
                     if (movie.toLowerCase().contains(filterText.toLowerCase())) {
                        filteredMovies.add(movie);
                     }
                  }
                  DefaultListModel<String> newMoviesListModel = new DefaultListModel<>();
                  for (String movie : filteredMovies) {
                     newMoviesListModel.addElement(movie);
                  }
                  suggestedMovies.setModel(newMoviesListModel);
                  panel.revalidate();
                  panel.repaint();
               }
            });

            JButton seeMoreButton = new JButton("See More");
            seeMoreButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  String selectedMovie = suggestedMovies.getSelectedValue();
                  String descriptionRetrieved = retrieveDescription(selectedMovie);
                  frame.setVisible(false);
                  showMovieDescription(descriptionRetrieved);
               }
            });
            panel.add(seeMoreButton);

            JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  frame.setVisible(false);
                  showCategories();
               }
            });
            panel.add(backButton);

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  Neo4J.terminate();
               }
            });
            panel.add(exitButton);

            //refresh the JPanel
            panel.revalidate();
            panel.repaint();
         }
      });
      panel.add(suggestButton);
      panel.add(Box.createHorizontalGlue());

      //Back button
      JButton backButton = new JButton("Back");
      backButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            frame.setVisible(false);
            selectSuggestionMethod();
         }
      });
      panel.add(backButton);

      //Exit button
      JButton exitButton = new JButton("Exit");
      exitButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            Neo4J.terminate();

         }
      });
      panel.add(exitButton);

      frame.add(panel);
      frame.setVisible(true);
   }

   //Suggestion selecting a movie
   private static void showMovies()
   {
      final JFrame frame = new JFrame("Movie Suggestion");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(400, 600);
      frame.setLocationRelativeTo(null);
      frame.setBackground(Color.gray);

      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      panel.setBackground(Color.gray);

      final List < String > movies = collectMovies();
      DefaultListModel < String > moviesListModel = new DefaultListModel < > ();
      for (String movie: movies) {
         moviesListModel.addElement(movie);
      }
      final JList < String > moviesList = new JList < > (moviesListModel);
      JScrollPane scrollPane = new JScrollPane(moviesList);
      

      final JTextField searchBar = new JTextField();
      searchBar.setMaximumSize(new Dimension(200, 25));
      
      panel.add(searchBar);
      panel.add(scrollPane);
      
      searchBar.getDocument().addDocumentListener(new DocumentListener() {
         public void changedUpdate(DocumentEvent e) {
            filterList();
         }
         public void removeUpdate(DocumentEvent e) {
            filterList();
         }
         public void insertUpdate(DocumentEvent e) {
            filterList();
         }

         public void filterList() {
            String filterText = searchBar.getText();
            List<String> filteredMovies = new ArrayList<>();
            for (String movie : movies) {
               if (movie.toLowerCase().contains(filterText.toLowerCase())) {
                  filteredMovies.add(movie);
               }
            }
            DefaultListModel<String> newMoviesListModel = new DefaultListModel<>();
            for (String movie : filteredMovies) {
               newMoviesListModel.addElement(movie);
            }
            moviesList.setModel(newMoviesListModel);
            panel.revalidate();
            panel.repaint();
         }
      });

      //Suggestion button
      JButton suggestButton = new JButton("Suggest");
      suggestButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            String selectedMovie = moviesList.getSelectedValue();
            String movieCategory = getMovieCategory(selectedMovie);
            String movieDirector = getMovieDirector(selectedMovie);
            final List < String > moviesRetrieved = requestSuggestion(movieCategory,movieDirector);
            frame.setTitle("Suggestion: "+selectedMovie);
            panel.removeAll();

            //create a new JList with the updated list of strings
            DefaultListModel < String > newStringListModel = new DefaultListModel < > ();
            for (String movie: moviesRetrieved) {
               newStringListModel.addElement(movie);
            }
            final JList < String > suggestedMovies = new JList < > (newStringListModel);
            JScrollPane scrollPane = new JScrollPane(suggestedMovies);
            
            final JTextField searchBar = new JTextField();
            searchBar.setMaximumSize(new Dimension(250, 25));

            panel.add(searchBar);
            panel.add(scrollPane);
            searchBar.getDocument().addDocumentListener(new DocumentListener() {
               public void changedUpdate(DocumentEvent e) {
                  filterList();
               }
               public void removeUpdate(DocumentEvent e) {
                  filterList();
               }
               public void insertUpdate(DocumentEvent e) {
                  filterList();
               }

               public void filterList() {
                  String filterText = searchBar.getText();
                  List<String> filteredMovies = new ArrayList<>();
                  for (String movie : moviesRetrieved) {
                     if (movie.toLowerCase().contains(filterText.toLowerCase())) {
                        filteredMovies.add(movie);
                     }
                  }
                  DefaultListModel<String> newMoviesListModel = new DefaultListModel<>();
                  for (String movie : filteredMovies) {
                     newMoviesListModel.addElement(movie);
                  }
                  suggestedMovies.setModel(newMoviesListModel);
                  panel.revalidate();
                  panel.repaint();
               }
            });

            JButton seeMoreButton = new JButton("See More");
            seeMoreButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  String selectedMovie = moviesList.getSelectedValue();
                  String descriptionRetrieved = retrieveDescription(selectedMovie);

                  //frame.setVisible(false);
                  showMovieDescription(descriptionRetrieved);
               }
            });
            panel.add(seeMoreButton);

            JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  
                  frame.setVisible(false);
                  showMovies();
               }
            });
            panel.add(backButton);

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  Neo4J.terminate();
               }
            });
            panel.add(exitButton);

            //refresh the JPanel
            panel.revalidate();
            panel.repaint();
         }
      });
      panel.add(suggestButton);
      panel.add(Box.createHorizontalGlue());

      //Back button
      JButton backButton = new JButton("Back");
      backButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            frame.setVisible(false);
            selectSuggestionMethod();
         }
      });
      panel.add(backButton);

      //Exit button
      JButton exitButton = new JButton("Exit");
      exitButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            Neo4J.terminate();
         }
      });
      panel.add(exitButton);

      frame.add(panel);
      frame.setVisible(true);
   }

   //Movie description
   private static void showMovieDescription(String descriptionRetrieved) {
      final JFrame frame = new JFrame("Movie Suggestion");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(400, 600);
      frame.setLocationRelativeTo(null);
      frame.setBackground(Color.gray);

      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      panel.setBackground(Color.gray);

      JLabel label = new JLabel(descriptionRetrieved);
      StringBuilder strBuilder = new StringBuilder();
      strBuilder.append(descriptionRetrieved + "<br>");
      label.setText("<html>" + strBuilder.toString() + "</html>");
      panel.add(label);

      JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  showMovies();
                  frame.setVisible(false);
                  
                  
               }
            });
            panel.add(backButton);

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  Neo4J.terminate();
               }
            });
            panel.add(exitButton);

      frame.add(panel);
      frame.setVisible(true);
   }

   //GET description
   private static String retrieveDescription(String selectedMoviek)
   {
      return connection.retrieveDescription(selectedMoviek);
   }

   //GET categories
   private static List < String > collectCategories() {
      return connection.retrieveCategories();
   }

   //GET movies
   private static List < String > collectMovies() {
      return connection.retrieveMovies();
   }

   //GET category starting from movie
   private static String getMovieCategory(String movieTitle) {
      return connection.getMovieCategory(movieTitle);
   }

   //GET director starting from movie
   private static String getMovieDirector(String movieTitle) {
      return connection.getMovieDirector(movieTitle);
   }

   //GET reccomendation
   private static List < String > requestSuggestion(String category) {
      return connection.suggestMovie(category);
   }

   //GET reccomendation
   private static List < String > requestSuggestion(String category, String director) {
      return connection.suggestMovie(category);
   }
}