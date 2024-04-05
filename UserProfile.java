import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class Main {
    public static void main(String[] args) {
        // Create a music recommendation system
        MusicRecommendationSystem system = new MusicRecommendationSystem();

        // Add some music items to the catalog
        system.addMusicItem(new MusicItem("item1", "Song 1", "Artist 1", "Genre 1"));
        system.addMusicItem(new MusicItem("item2", "Song 2", "Artist 2", "Genre 2"));
        system.addMusicItem(new MusicItem("item3", "Song 3", "Artist 1", "Genre 1"));
        system.addMusicItem(new MusicItem("item4", "Song 4", "Artist 3", "Genre 3"));

        // Add some user ratings
        system.addUserRating("user1", "item1", 4.5);
        system.addUserRating("user1", "item2", 3.8);
        system.addUserRating("user1", "item3", 4.0);
        system.addUserRating("user2", "item2", 4.2);
        system.addUserRating("user2", "item3", 3.9);
        system.addUserRating("user2", "item4", 4.1);
        // Add some item similarities
        system.addItemSimilarity("item1", "item3", 0.8);
        system.addItemSimilarity("item2", "item4", 0.7);

        // Recommend items for a user
        System.out.println("Recommendations for user1:");
        for (MusicItem item : system.recommendItems("user1", 3)) {
            System.out.println(item);
        }

        System.out.println("\nRecommendations for user2:");
        for (MusicItem item : system.recommendItems("user2", 3)) {
            System.out.println(item);
        }
    }
}
class MusicItem {
    private String itemId;
    private String title;
    private String artist;
    private String genre;

    public MusicItem(String itemId, String title, String artist, String genre) {
        this.itemId = itemId;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
    }

    public String getItemId() {
        return itemId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }
}

class UserProfile {
    public String userId;
    private LinkedList<MusicItem> listeningHistory;
    private HashMap<String, Integer> genrePreferences;
    private HashMap<String, Integer> artistPreferences;
    private HashMap<String, Integer> songPreferences;
    private PriorityQueue<Preference<String>> topGenrePreferences;
    private PriorityQueue<Preference<String>> topArtistPreferences;
    private PriorityQueue<Preference<String>> topSongPreferences;

    public UserProfile(String userId) {
        this.userId = userId;
        this.listeningHistory = new LinkedList<>();
        this.genrePreferences = new HashMap<>();
        this.artistPreferences = new HashMap<>();
        this.songPreferences = new HashMap<>();
        this.topGenrePreferences = new PriorityQueue<>((a, b) -> a.getCount() - b.getCount());
        this.topArtistPreferences = new PriorityQueue<>((a, b) -> a.getCount() - b.getCount());
        this.topSongPreferences = new PriorityQueue<>((a, b) -> a.getCount() - b.getCount());
    }

    private void updateTopPreference(String item, Map<String, Integer> preferences, PriorityQueue<Preference<String>> topPreferences) {
        int count = preferences.get(item);
        topPreferences.offer(new Preference<>(item, count));
        if (topPreferences.size() > 10) {
            topPreferences.poll();
        }
    }
    public Map<String, Integer> getGenrePreferences() {
        return genrePreferences;
    }
    public Map<String, Integer> getSongPreferences() {
        return songPreferences;
    }

    public PriorityQueue<Preference<String>> getTopGenrePreferences() {
        return topGenrePreferences;
    }

    public PriorityQueue<Preference<String>> getTopArtistPreferences() {
        return topArtistPreferences;
    }
    public LinkedList<MusicItem> getListeningHistory() {
        return listeningHistory;
    }


    public PriorityQueue<Preference<String>> getTopSongPreferences() {
        return topSongPreferences;
    }
    public void addToListeningHistory(MusicItem musicItem) {
        listeningHistory.addFirst(musicItem);
    }
    public void updatePreferences(MusicItem musicItem) {
        updateGenrePreference(musicItem.getGenre());
        updateArtistPreference(musicItem.getArtist());
        updateSongPreference(musicItem.getTitle());
    }
    private void updateGenrePreference(String genre) {
        genrePreferences.merge(genre, 1, Integer::sum);
        updateTopPreference(genre, genrePreferences, topGenrePreferences);
    }

    private void updateArtistPreference(String artist) {
        artistPreferences.merge(artist, 1, Integer::sum);
        updateTopPreference(artist, artistPreferences, topArtistPreferences);
    }

    private void updateSongPreference(String song) {
        songPreferences.merge(song, 1, Integer::sum);
        updateTopPreference(song, songPreferences, topSongPreferences);
    }

    private static class Preference<T> {
        private T value;
        private int count;

        public Preference(T value, int count) {
            this.value = value;
            this.count = count;
        }

        public T getValue() {
            return value;
        }

        public int getCount() {
            return count;
        }
    }
}

class UserDataProtection {
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String HASHING_ALGORITHM = "SHA-256";

    private Map<String, byte[]> encryptedUserData;
    private Map<String, byte[]> hashedUserIds;

    public UserDataProtection() {
        this.encryptedUserData = new HashMap<>();
        this.hashedUserIds = new TreeMap<>();
    }

    public void storeUserData(String userId, UserProfile userProfile) {
        byte[] encryptedProfile = encryptUserData(userProfile);
        byte[] hashedUserId = hashUserId(userId);

        encryptedUserData.put(new String(hashedUserId), encryptedProfile);
        hashedUserIds.put(userId, hashedUserId);
    }

    public UserProfile retrieveUserData(String userId) {
        byte[] hashedUserId = hashedUserIds.get(userId);
        if (hashedUserId != null) {
            byte[] encryptedProfile = encryptedUserData.get(new String(hashedUserId));
            if (encryptedProfile != null) {
                return decryptUserData(encryptedProfile);
            }
        }
        return null; // Handle the case where user data is not found
    }

    private byte[] encryptUserData(UserProfile userProfile) {
        try {
            byte[] key = generateEncryptionKey();
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, ENCRYPTION_ALGORITHM));
            return cipher.doFinal(userProfile.toString().getBytes());
        } catch (Exception e) {
            // Handle encryption exception
        }
        return null;
    }

    private UserProfile decryptUserData(byte[] encryptedData) {
        try {
            byte[] key = generateEncryptionKey();
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, ENCRYPTION_ALGORITHM));
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new UserProfile(new String(decryptedData));
        } catch (Exception e) {
            // Handle decryption exception
        }
        return null;
    }

    private byte[] hashUserId(String userId) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASHING_ALGORITHM);
            return digest.digest(userId.getBytes());
        } catch (NoSuchAlgorithmException e) {
            // Handle hashing exception
        }
        return null;
    }

    private byte[] generateEncryptionKey() {
        // Generate a secure encryption key using a key derivation function or a key management service
        return new byte[16];
    }
}

class MusicRecommendationSystem {
    private Map<String, UserProfile> userProfiles;
    private Map<String, MusicItem> musicCatalog;
    private Map<String, Map<String, Double>> itemSimilarities;
    private UserDataProtection userDataProtection;

    public MusicRecommendationSystem() {
        this.userProfiles = new HashMap<>();
        this.musicCatalog = new HashMap<>();
        this.itemSimilarities = new HashMap<>();
        this.userDataProtection = new UserDataProtection();
    }

    public void addMusicItem(MusicItem musicItem) {
        musicCatalog.put(musicItem.getItemId(), musicItem);
    }

    public void addUserRating(String userId, String itemId, double rating) {
        UserProfile userProfile = getUserProfile(userId);
        userProfile.addToListeningHistory(musicCatalog.get(itemId));
        userProfile.updatePreferences(musicCatalog.get(itemId));
        userDataProtection.storeUserData(userId, userProfile);
    }

    public void addItemSimilarity(String itemId1, String itemId2, double similarity) {
        itemSimilarities.computeIfAbsent(itemId1, k -> new HashMap<>())
                         .put(itemId2, similarity);
        itemSimilarities.computeIfAbsent(itemId2, k -> new HashMap<>())
                         .put(itemId1, similarity);
    }

    public List<MusicItem> recommendItems(String userId, int k) {
        UserProfile userProfile = getUserProfile(userId);
        List<String> similarUsers = findSimilarUsers(userProfile, k);
        List<String> recommendations = aggregateRecommendations(similarUsers, k);
        List<MusicItem> result = new ArrayList<>();
        for (String itemId : recommendations) {
            MusicItem item = musicCatalog.get(itemId);
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }
    

    private UserProfile getUserProfile(String userId) {
        if (!userProfiles.containsKey(userId)) {
            UserProfile userProfile = userDataProtection.retrieveUserData(userId);
            if (userProfile == null) {
                userProfile = new UserProfile(userId);
            }
            userProfiles.put(userId, userProfile);
        }
        return userProfiles.get(userId);
    }

    private List<String> findSimilarUsers(UserProfile targetUserProfile, int k) {
        PriorityQueue<Pair<Double, String>> similarUsers = new PriorityQueue<>((a, b) -> Double.compare(a.getKey(), b.getKey()));
        for (Map.Entry<String, UserProfile> entry : userProfiles.entrySet()) {
            if (!entry.getKey().equals(targetUserProfile.userId)) {
                double similarity = calculateUserSimilarity(targetUserProfile, entry.getValue());
                similarUsers.offer(new Pair<>(similarity, entry.getKey()));
                if (similarUsers.size() > k) {
                    similarUsers.poll();
                }
            }
        }
        List<String> result = new ArrayList<>();
        while (!similarUsers.isEmpty()) {
            Pair<Double, String> pair = similarUsers.poll();
            System.out.println("User: " + pair.getValue() + ", Similarity: " + pair.getKey());
            result.add(0, pair.getValue());
        }
        return result;
    }
    

    private double calculateUserSimilarity(UserProfile profile1, UserProfile profile2) {
        double dotProduct = 0.0;
        double profile1Norm = 0.0;
        double profile2Norm = 0.0;
        for (Map.Entry<String, Integer> entry : profile1.getGenrePreferences().entrySet()) {
            if (profile2.getGenrePreferences().containsKey(entry.getKey())) {
                dotProduct += entry.getValue() * profile2.getGenrePreferences().get(entry.getKey());
            }
            profile1Norm += entry.getValue() * entry.getValue();
        }
        for (int count : profile2.getGenrePreferences().values()) {
            profile2Norm += count * count;
        }
        return dotProduct / (Math.sqrt(profile1Norm) * Math.sqrt(profile2Norm));
    }

    private List<String> aggregateRecommendations(List<String> similarUsers, int k) {
        Map<String, Double> recommendationScores = new HashMap<>();
        for (String userId : similarUsers) {
            UserProfile userProfile = getUserProfile(userId);
            for (Map.Entry<String, Integer> entry : userProfile.getSongPreferences().entrySet()) {
                if (!userProfile.getListeningHistory().contains(musicCatalog.get(entry.getKey()))) {
                    recommendationScores.merge(entry.getKey(), (double) entry.getValue(), Double::sum);
                }
            }
        }
        List<String> result = new ArrayList<>();
        recommendationScores.forEach((key, value) -> System.out.println("Song: " + key + ", Score: " + value));
        return new ArrayList<>(recommendationScores.entrySet().stream()
                                                   .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                                   .limit(k)
                                                   .map(Map.Entry::getKey)
                                                   .toList());
    }

    private static class Pair<K, V> {
        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    private static class Preference<T> {
        private T value;
        private int count;

        public Preference(T value, int count) {
            this.value = value;
            this.count = count;
        }

        public T getValue() {
            return value;
        }

        public int getCount() {
            return count;
        }
    }
}
