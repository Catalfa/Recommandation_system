
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Dimension;
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
      final JFrame frame = new JFrame("Movie Suggestion Menu");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(300, 200);
      frame.setLocationRelativeTo(null);

      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

      JButton suggestCategoryButton = new JButton("Suggest by Category");
      suggestCategoryButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            frame.setVisible(false);
            showCategories();
         }
      });
      panel.add(suggestCategoryButton);
      panel.add(Box.createHorizontalGlue());

      JButton suggestMovieButton = new JButton("Suggest by movie");
      suggestMovieButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            frame.setVisible(false);
            showMovies();
         }
      });
      panel.add(suggestMovieButton);
      panel.add(Box.createHorizontalGlue());

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

   //Suggestion selecting a category
   private static void showCategories() {
      final JFrame frame = new JFrame("Movie Suggestion");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(800, 500);
      frame.setLocationRelativeTo(null);

      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      
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
            panel.add(scrollPane);

            final JTextField searchBar = new JTextField();
            searchBar.setMaximumSize(new Dimension(1300, 25));
            
            panel.add(searchBar);
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
   private static void showMovies() {
      final JFrame frame = new JFrame("Movie Suggestion");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(800, 500);
      frame.setLocationRelativeTo(null);

      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      
      final List < String > movies = collectMovies();
      DefaultListModel < String > moviesListModel = new DefaultListModel < > ();
      for (String movie: movies) {
         moviesListModel.addElement(movie);
      }
      final JList < String > moviesList = new JList < > (moviesListModel);
      JScrollPane scrollPane = new JScrollPane(moviesList);
      

      final JTextField searchBar = new JTextField();
      searchBar.setMaximumSize(new Dimension(1300, 25));
      
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
            final List < String > moviesRetrieved = requestSuggestion(movieCategory);
            frame.setTitle(movieCategory + " Movies");
            panel.removeAll();

            //create a new JList with the updated list of strings
            DefaultListModel < String > newStringListModel = new DefaultListModel < > ();
            for (String movie: moviesRetrieved) {
               newStringListModel.addElement(movie);
            }
            final JList < String > suggestedMovies = new JList < > (newStringListModel);
            JScrollPane scrollPane = new JScrollPane(suggestedMovies);
            panel.add(scrollPane);

            final JTextField searchBar = new JTextField();
            searchBar.setMaximumSize(new Dimension(1300, 25));

            panel.add(searchBar);
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
      frame.setSize(800, 500);
      frame.setLocationRelativeTo(null);

      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

      JLabel label = new JLabel(descriptionRetrieved);
      StringBuilder strBuilder = new StringBuilder();
      strBuilder.append(descriptionRetrieved + "<br>");
      label.setText("<html>" + strBuilder.toString() + "</html>");
      panel.add(label);

      JButton backButton = new JButton("Back");
            backButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent e) {
                  
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

   //GET reccomendation
   private static List < String > requestSuggestion(String category) {
      return connection.suggestMovie(category);
   }
}