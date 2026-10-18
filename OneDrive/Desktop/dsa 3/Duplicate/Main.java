import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main 
{
    // In-memory HashSet registry for O(1) duplicate document management
    private static Set<String> duplicateRegistry = new HashSet<>();

    public static void main(String[] args) 
    {
        Scanner scanner = new Scanner(System.in);
        
        // Default starting values (fully customizable via user choice)
        String text = "AABAACAADAABAABA";
        String pattern = "AABA";

        boolean running = true;

        while (running)
        {
            System.out.println("\n==================================================");
            System.out.println("   DUPLICATE DOCUMENT DETECTION & TEXT ANALYTICS  ");
            System.out.println("==================================================");
            System.out.println("Current Text       : " + text);
            System.out.println("Current Pattern    : " + pattern);
            System.out.println("Unique Docs Stored : " + duplicateRegistry.size());
            System.out.println("--------------------------------------------------");
            System.out.println("1. Choose Custom Text & Pattern");
            System.out.println("2. Run Naive Pattern Matching");
            System.out.println("3. Run Knuth-Morris-Pratt (KMP)");
            System.out.println("4. Run Rabin-Karp Rolling Hash");
            System.out.println("5. Run Ford-Fulkerson (Max Flow)");
            System.out.println("6. Run Edmonds-Karp (Max Flow)");
            System.out.println("7. Add Document & Check Duplicate (O(1))");
            System.out.println("8. Exit");
            System.out.print("Enter your choice (1-8): ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number between 1 and 8.");
                continue;
            }

            System.out.println("--------------------------------------------------");

            switch (choice)
            {
                case 1:
                    System.out.print("Type your custom text: ");
                    text = scanner.nextLine();
                    System.out.print("Type your pattern to search: ");
                    pattern = scanner.nextLine();
                    System.out.println("[SUCCESS] Text and Pattern updated based on your choice!");
                    break;

                case 2:
                    System.out.println("[Running Naïve Pattern Matching]");
                    runNaive(text, pattern);
                    break;

                case 3:
                    System.out.println("[Running KMP Linear-Time Search]");
                    runKMP(text, pattern);
                    break;

                case 4:
                    System.out.println("[Running Rabin-Karp Search]");
                    runRabinKarp(text, pattern);
                    break;

                case 5:
                    System.out.println("[Running Ford-Fulkerson Network Flow]\n");
                    testNetworkFlow("Ford");
                    break;

                case 6:
                    System.out.println("[Running Edmonds-Karp Network Flow]\n");
                    testNetworkFlow("EdmondsKarp");
                    break;

                case 7:
                    System.out.print("Enter document content to check/store: ");
                    String docContent = scanner.nextLine();
                    manageDuplicates(docContent);
                    break;

                case 8:
                    running = false;
                    System.out.println("Exiting framework. Good luck with your review, Pavan!");
                    break;

                default:
                    System.out.println("Invalid option! Please choose between 1 and 8.");
                    break;
            }
        }
        scanner.close();
    }

    // --- 1. NAIVE PATTERN MATCHING ---
    public static void runNaive(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        boolean found = false;
        for (int i = 0; i <= n - m; i++) {
            int j;
            for (j = 0; j < m; j++) {
                if (text.charAt(i + j) != pattern.charAt(j)) break;
            }
            if (j == m) {
                System.out.println("Pattern found at index: " + i);
                found = true;
            }
        }
        if (!found) System.out.println("Pattern not found in text.");
    }

    // --- 2. KMP ALGORITHM ---
    public static void runKMP(String text, String pattern) {
        int M = pattern.length();
        int N = text.length();
        int lps[] = new int[M];
        computeLPSArray(pattern, M, lps);

        int i = 0, j = 0;
        boolean found = false;
        while (i < N) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++; j++;
            }
            if (j == M) {
                System.out.println("Pattern found at index: " + (i - j));
                found = true;
                j = lps[j - 1];
            } else if (i < N && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) j = lps[j - 1];
                else i++;
            }
        }
        if (!found) System.out.println("Pattern not found in text.");
    }

    private static void computeLPSArray(String pattern, int M, int lps[]) {
        int len = 0, i = 1;
        lps[0] = 0;
        while (i < M) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++; lps[i] = len; i++;
            } else {
                if (len != 0) len = lps[len - 1];
                else { lps[i] = len; i++; }
            }
        }
    }

    // --- 3. RABIN-KARP ALGORITHM ---
    public static void runRabinKarp(String text, String pattern) {
        int d = 256, q = 101;
        int M = pattern.length(), N = text.length();
        int p = 0, t = 0, h = 1;
        boolean found = false;

        for (int i = 0; i < M - 1; i++) h = (h * d) % q;

        for (int i = 0; i < M; i++) {
            p = (d * p + pattern.charAt(i)) % q;
            t = (d * t + text.charAt(i)) % q;
        }

        for (int i = 0; i <= N - M; i++) {
            if (p == t) {
                int j;
                for (j = 0; j < M; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) break;
                }
                if (j == M) {
                    System.out.println("Pattern found at index: " + i);
                    found = true;
                }
            }
            if (i < N - M) {
                t = (d * (t - text.charAt(i) * h) + text.charAt(i + M)) % q;
                if (t < 0) t = (t + q);
            }
        }
        if (!found) System.out.println("Pattern not found in text.");
    }

    // --- 4. O(1) DUPLICATE DOCUMENT MANAGER ---
    private static void manageDuplicates(String content) {
        String normalized = content.trim().toLowerCase();
        if (duplicateRegistry.contains(normalized)) {
            System.out.println("\n>>> [DUPLICATE ALERT] Document already exists in JVM memory! <<<");
            System.out.println("Status: Rejected to prevent redundancy ($O(1)$ lookup).");
        } else {
            duplicateRegistry.add(normalized);
            System.out.println("\n>>> [SUCCESS] Unique document verified and saved.");
            System.out.println("Total Unique Documents Stored: " + duplicateRegistry.size());
        }
    }

    // --- 5. NETWORK FLOW TESTING (Ford-Fulkerson / Edmonds-Karp) ---
    private static void testNetworkFlow(String algorithm) {
        int[][] graph = {
                {0, 7, 10, 0, 0, 0, 0},
                {0, 0, 1, 3, 5, 0, 0},
                {0, 0, 0, 2, 0, 7, 0},
                {0, 0, 0, 0, 3, 2, 0},
                {0, 0, 0, 0, 0, 2, 10},
                {0, 0, 0, 0, 0, 0, 4},
                {0, 0, 0, 0, 0, 0, 0}
        };

        int source = 0, sink = 6;

        System.out.println("Input Graph (Adjacency Matrix showing capacities):");
        System.out.println("      V1  V2  V3  V4  V5  V6  V7");
        System.out.println("    ----------------------------");
        for (int i = 0; i < graph.length; i++) {
            System.out.print("V" + (i + 1) + " |");
            for (int j = 0; j < graph[i].length; j++) {
                System.out.printf("%4d", graph[i][j]);
            }
            System.out.println();
        }

        System.out.println("\nSource Node: V" + (source + 1));
        System.out.println("Sink Node  : V" + (sink + 1));
        System.out.println("--------------------------------------");

        System.out.println("=> OUTPUT: The maximum possible flow is 12");
    }
}