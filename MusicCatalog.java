import java.util.*;

// Trie Node
class TrieNode {
    Map<Character, TrieNode> children;
    boolean isEndWord;

    TrieNode() {
        children = new HashMap<>();
        isEndWord = false;
    }
}

// Trie
class Trie {
    private TrieNode root;

    Trie() {
        root = new TrieNode();
    }

    void insert(String songTitle) {
        TrieNode node = root;
        for (char c : songTitle.toCharArray()) {
            if (!node.children.containsKey(c)) {
                node.children.put(c, new TrieNode());
            }
            node = node.children.get(c);
        }
        node.isEndWord = true;
    }

    boolean search(String songTitle) {
        TrieNode node = root;
        for (char c : songTitle.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return false;
            }
            node = node.children.get(c);
        }
        return node.isEndWord;
    }
}

// Song
class Song {
    String title;
    String artist;
    String album;
    String genre;

    Song(String t, String a, String al, String g) {
        title = t;
        artist = a;
        album = al;
        genre = g;
    }
}

// Graph
class Graph {
    private Map<String, List<String>> adjacencyList;

    Graph() {
        adjacencyList = new HashMap<>();
    }

    void addVertex(String vertex) {
        adjacencyList.put(vertex, new ArrayList<>());
    }

    void addEdge(String vertex1, String vertex2) {
        adjacencyList.get(vertex1).add(vertex2);
        adjacencyList.get(vertex2).add(vertex1);
    }

    void breadthFirstSearch(String startVertex) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        visited.add(startVertex);
        queue.add(startVertex);

        while (!queue.isEmpty()) {
            String currentVertex = queue.poll();

            for (String neighbor : adjacencyList.get(currentVertex)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    System.out.print(neighbor + " ");
                }
            }
        }
    }

    // Method to remove a vertex and associated edges
    void removeVertex(String vertex) {
        // Remove vertex from adjacency list
        adjacencyList.remove(vertex);

        // Remove references to the vertex from other vertices' adjacency lists
        for (List<String> edges : adjacencyList.values()) {
            edges.remove(vertex);
        }
    }
}

// Music Catalog
class MusicCatalog {
    private Trie songTrie;
    private Graph catalogGraph;
    private Map<String, Song> songMetadata;
    private Map<String, Set<String>> artistMetadata;
    private Map<String, Set<String>> albumMetadata;
    private Map<String, Set<String>> genreMetadata;

    MusicCatalog() {
        songTrie = new Trie();
        catalogGraph = new Graph();
        songMetadata = new HashMap<>();
        artistMetadata = new HashMap<>();
        albumMetadata = new HashMap<>();
        genreMetadata = new HashMap<>();
    }

    void addSong(String title, String artist, String album, String genre) {
        Song song = new Song(title, artist, album, genre);
        songTrie.insert(title);
        songMetadata.put(title, song);

        catalogGraph.addVertex(title);
        catalogGraph.addVertex(artist);
        catalogGraph.addVertex(album);
        catalogGraph.addVertex(genre);

        catalogGraph.addEdge(title, artist);
        catalogGraph.addEdge(title, album);
        catalogGraph.addEdge(title, genre);

        artistMetadata.computeIfAbsent(artist, k -> new HashSet<>()).add(title);
        albumMetadata.computeIfAbsent(album, k -> new HashSet<>()).add(title);
        genreMetadata.computeIfAbsent(genre, k -> new HashSet<>()).add(title);
    }

    boolean searchSong(String title) {
        return songMetadata.containsKey(title);
    }

    List<String> getSongsByArtist(String artist) {
        return new ArrayList<>(artistMetadata.getOrDefault(artist, new HashSet<>()));
    }

    List<String> getSongsByAlbum(String album) {
        return new ArrayList<>(albumMetadata.getOrDefault(album, new HashSet<>()));
    }

    List<String> getSongsByGenre(String genre) {
        return new ArrayList<>(genreMetadata.getOrDefault(genre, new HashSet<>()));
    }

    void updateSongMetadata(String title, String newArtist, String newAlbum, String newGenre) {
        if (songMetadata.containsKey(title)) {
            Song song = songMetadata.get(title);

            // Remove song from old metadata entries
            artistMetadata.get(song.artist).remove(title);
            albumMetadata.get(song.album).remove(title);
            genreMetadata.get(song.genre).remove(title);

            // Update song metadata
            song.artist = newArtist;
            song.album = newAlbum;
            song.genre = newGenre;

            // Update metadata maps with new metadata entries
            artistMetadata.computeIfAbsent(newArtist, k -> new HashSet<>()).add(title);
            albumMetadata.computeIfAbsent(newAlbum, k -> new HashSet<>()).add(title);
            genreMetadata.computeIfAbsent(newGenre, k -> new HashSet<>()).add(title);
        }
    }

    void deleteSong(String title) {
        if (!songMetadata.containsKey(title)) {
            // Song not found, return early
            return;
        }

        Song song = songMetadata.get(title);

        // Remove song from Trie
        songTrie.search(title);
        songMetadata.remove(title);

        // Remove song and associated vertices from the graph
        catalogGraph.removeVertex(title);
        catalogGraph.removeVertex(song.artist);
        catalogGraph.removeVertex(song.album);
        catalogGraph.removeVertex(song.genre);

        // Remove song from metadata maps
        artistMetadata.get(song.artist).remove(title);
        albumMetadata.get(song.album).remove(title);
        genreMetadata.get(song.genre).remove(title);
    }
}

public class MusicCatalogMainFinal {
    public static void main(String[] args) {
        MusicCatalog catalog = new MusicCatalog();

        // Add sample items to the catalog
        catalog.addSong("Song1", "Artist1", "Album1", "Genre1");
        catalog.addSong("Song2", "Artist1", "Album2", "Genre2");
        catalog.addSong("Song3", "Artist2", "Album1", "Genre1");
        // Add more sample items here...
        catalog.addSong("Song4", "Artist3", "Album3", "Genre3");
        catalog.addSong("Song5", "Artist2", "Album2", "Genre1");
        catalog.addSong("Song6", "Artist4", "Album1", "Genre2");
        catalog.addSong("Song7", "Artist5", "Album3", "Genre3");
        catalog.addSong("Song8", "Artist1", "Album1", "Genre2");
        catalog.addSong("Song9", "Artist6", "Album2", "Genre1");
        catalog.addSong("Song10", "Artist3", "Album3", "Genre3");
        catalog.addSong("Song11", "Artist2", "Album1", "Genre1");
        catalog.addSong("Song12", "Artist4", "Album2", "Genre2");
        catalog.addSong("Song13", "Artist5", "Album3", "Genre3");
        catalog.addSong("Song14", "Artist1", "Album1", "Genre2");
        catalog.addSong("Song15", "Artist6", "Album2", "Genre1");

        catalog.deleteSong("Song14");
        catalog.updateSongMetadata("Song12", "Arijit", "Fitoor", "RomCom");

        // Interactive terminal interface
        Scanner scanner = new Scanner(System.in);
        while (true) {
            // System.out.println("Select an option:");
            // System.out.println("1. Search for a song");
            // System.out.println("2. Search for an album");
            // System.out.println("3. Exit");

            // int option = scanner.nextInt();
            int option = 1;

            if (option == 1) {
                System.out.println("Select search criteria:");
                System.out.println("a. Search by title");
                System.out.println("b. Search by genre");
                System.out.println("c. Search by singer");
                System.out.println("d. Exit!");

                char searchOption = scanner.next().charAt(0);

                if (searchOption == 'a') {
                    System.out.print("Enter song title: ");
                    String title = scanner.next();
                    if (catalog.searchSong(title)) {
                        System.out.println("Song found!");
                    } else {
                        System.out.println("Song not found!");
                    }
                } else if (searchOption == 'b') {
                    System.out.print("Enter genre: ");
                    String genre = scanner.next();
                    List<String> songs = new ArrayList<>(catalog.getSongsByGenre(genre));
                    if (!songs.isEmpty()) {
                        System.out.println("Songs in genre '" + genre + "':");
                        songs.forEach(System.out::println);
                    } else {
                        System.out.println("No songs found in genre '" + genre + "'");
                    }
                } else if (searchOption == 'c') {
                    System.out.print("Enter artist: ");
                    String artist = scanner.next();
                    List<String> songs = new ArrayList<>(catalog.getSongsByArtist(artist));
                    if (!songs.isEmpty()) {
                        System.out.println("Songs by artist '" + artist + "':");
                        songs.forEach(System.out::println);
                    } else {
                        System.out.println("No songs found by artist '" + artist + "'");
                    }
                } else if (searchOption == 'd') {
                    break;
                } else {
                    System.out.println("Invalid option");
                }
            } else if (option == 2) {
                System.out.print("Enter album name: ");
                String album = scanner.next();
                List<String> songs = new ArrayList<>(catalog.getSongsByAlbum(album));
                if (!songs.isEmpty()) {
                    System.out.println("Songs in album '" + album + "':");
                    songs.forEach(System.out::println);
                } else {
                    System.out.println("No songs found in album '" + album + "'");
                }
            } else if (option == 3) {
                break; // Exit the program
            } else {
                System.out.println("Invalid option");
            }
        }
    }
}
