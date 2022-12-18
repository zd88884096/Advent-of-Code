import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    //Idea: Initial thought: linked list, but I kinda expected that Part II we would need to simulate many more iterations
    //  and it would be too long to do the computation if we use a linked list to brute force simulate
    //So instead we use a HashMap "map" to store (pair, number of times this pair appears in the actual string), 
    //  since there are at most 26 * 26 such pairs (2 letter, each has 26 choices), 
    //  in each iteration, say there are currently 100 "NN" pairs (stored as ("NN", 100) in "map") and we have "NN -> B", 
    //  then this essentially has the effect of removing 100 "NN" pairs but adding 100 "BN" and 100 "NB" pairs.
    //  So for each iteration we record all those changes in a HashMap "change" (useful since all changes in an iteration happen at one)
    //  if we just apply changes one by one, it will mess up the simulation (say we also have "NB -> C", if we simulate iteratively "NN -> B" then "NB -> C",
    //  in the end all "NB" pairs will be removed, but in reality we want to keep the "NB" pairs produced by "NB -> C").
    //  So we use "change" to record all the changes and in the end of the iteration apply them at once
    //
    //  Time Complexity: O(26 * 26 * 40), runs in 0.02 second.
    public static long MAX = (long)(1e8 + 0.1), startTime, endTime;
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        String[] input = read_all_String();
        String ori = input[0];
        char st = ori.charAt(0), end = ori.charAt(ori.length() - 1);
        HashMap<String, Long> map = new HashMap<>();
        for(char a = 'A'; a <= 'Z'; ++a){
            for(char b = 'A'; b <= 'Z'; ++b){
                String S = "";
                S += a;
                S += b;
                map.put(S, 0L);
            }
        }
        for(int i = 0; i < ori.length() - 1; ++i){
            map.put(ori.substring(i, i + 2), map.get(ori.substring(i, i + 2)) + 1);
        }

        //Part I
        //int epoch = 10;

        //Part II
        startTime = System.nanoTime();
        int epoch = 40;
        for(int _e = 0; _e < epoch; ++_e){
            HashMap<String, Long> change = new HashMap<>();
            for(int i = 2; i < input.length; ++i){
                String[] toks = split(input[i], "[ >-]");
                //print(toks);
                long num = map.get(toks[0]);
                String S1 = "", S2 = "";
                S1 += toks[0].charAt(0);
                S1 += toks[1].charAt(0);
                S2 += toks[1].charAt(0);
                S2 += toks[0].charAt(1);
                change.put(toks[0], change.getOrDefault(toks[0], 0L) - num);
                change.put(S1, change.getOrDefault(S1, 0L) + num);
                change.put(S2, change.getOrDefault(S2, 0L) + num);
            }
            for(String ele : change.keySet()){
                map.put(ele, map.get(ele) + change.get(ele));
            }
            long sum = 0L;
            for(String ele : map.keySet()){
                sum += map.get(ele);
            }
            //System.out.println(sum);
        }
        HashMap<Character, Long> freq = new HashMap<>();
        for(String S : map.keySet()){
            char a = S.charAt(0), b = S.charAt(1);
            freq.put(a, freq.getOrDefault(a, 0L) + map.get(S));
            freq.put(b, freq.getOrDefault(b, 0L) + map.get(S));
        }
        freq.put(st, freq.getOrDefault(st, 0L) + 1);
        freq.put(end, freq.getOrDefault(end, 0L) + 1);
        List<Long> l = new ArrayList<>();
        for(char ele : freq.keySet()){
            if(freq.get(ele) > 0)
                l.add(freq.get(ele));
        }
        Collections.sort(l);
        System.out.println("Task 2: " + ((l.get(l.size() - 1) - l.get(0)) / 2));
        endTime = System.nanoTime();
        print_time();
    }
    

    //template
    public static Scanner sc;
    static long MOD = 1000000007L;
    //(x + dir_card[i], y + dir_card[i + 1]) are 4 cardinal directions (for i in [0..3])
    public static int[] dir_card = {1, 0, -1, 0, 1};
    //same thing, 8 directions, for i in [0..7]
    public static int[] dir_all = {1, 0, -1, 0, 1, -1, -1, 1, 1};

    //print timer result in the form of a formmated String
    public static void print_time(){
        System.out.println("Time: " + String.format("%.3f", (double)(endTime - startTime) / (double)(1e9)) + " sec");
    }

    //pad character c onto String S until it reaches target_len
    //pad on left if side == 0, right otherwise
    public static String pad(String S, char c, int target_len, int side){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < target_len - S.length(); ++i){
            sb.append(c);
        }
        return side == 0 ? (sb.toString() + S) : (S + sb.toString());
    }

    public static int[][] rotate_90(int[][] arr){
        int row = arr.length, col = arr[0].length;
        int[][] res = new int[col][row];
        for(int i = 0; i < row; ++i){
            for(int j = 0; j < col; ++j){
                res[j][row - 1 - i] = arr[i][j];
            }
        }
        return res;
    }
    public static int[][] transpose(int[][] arr){
        int row = arr.length, col = arr[0].length;
        int[][] res = new int[col][row];
        for(int i = 0; i < row; ++i){
            for(int j = 0; j < col; ++j){
                res[j][i] = arr[i][j];
            }
        }
        return res;
    }
    //add all characters of a String into a HashSet
    public static HashSet<Character> setify(String S){
        HashSet<Character> set = new HashSet<>();
        char[] a = S.toCharArray();
        for(int i = 0; i < a.length; ++i){
            set.add(a[i]);
        }
        return set;
    }
    public static HashSet<Character> setify(char[] a){
        HashSet<Character> set = new HashSet<>();
        for(int i = 0; i < a.length; ++i){
            set.add(a[i]);
        }
        return set;
    }
    @SuppressWarnings("unchecked")
    public static <T> T[] subarray(T[] arr, int st, int end){
        assert(end > st);
        T[] res = (T[]) new Object[end - st];
        for(int i = st; i < end; ++i){
            res[i - st] = arr[i];
        }
        return res;
    }
    public static String[] subarray(String[] arr, int st, int end){
        assert(end > st);
        String[] res = new String[end - st];
        for(int i = st; i < end; ++i){
            String S = "";
            S += arr[i];
            res[i - st] = S;
        }
        return res;
    }
    //split with multiple delimiters and clearing empty String
    public static String[] split(String S, String delim){
        String[] toks = S.split(delim);
        List<String> l = new ArrayList<>();
        for(String cur : toks){
            if(!cur.equals("")){
                l.add(cur);
            }
        }
        String[] res = new String[l.size()];
        for(int i = 0; i < res.length; ++i){
            res[i] = l.get(i);
        }
        return res;
    }

    //parse a String[] into an int[]
    public static int[] stoi(String[] toks){
        int[] arr = new int[toks.length];
        for(int i = 0; i < toks.length; ++i){
            arr[i] = Integer.parseInt(toks[i]);
        }
        return arr;
    }

    public static long[] stol(String[] toks){
        long[] arr = new long[toks.length];
        for(int i = 0; i < toks.length; ++i){
            arr[i] = Long.parseLong(toks[i]);
        }
        return arr;
    }

    //read all input in the form of a List of String
    public static List<String> read_input(){
        try{
            sc = new Scanner(new File("input.txt"));
        }
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");        
        }
        List<String> l = new ArrayList<>();
        while(sc.hasNextLine()){
            String S = read();
            l.add(S);
        }
        return l;
    }

    //read all input into a 2d char array
    public static char[][] read_all_char_arr(){
        List<String> l = read_input();
        char[][] arr = new char[l.size()][l.get(0).length()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = l.get(i).toCharArray();
        }
        return arr;
    }
    //read all input into an int array
    public static int[] read_all_int(){
        List<String> l = read_input();
        int[] arr = new int[l.size()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = Integer.parseInt(l.get(i));
        }
        return arr;
    }
    //read all input into a 2d int array
    public static int[][] read_all_int_arr(){
        char[][] arr = read_all_char_arr();
        int row = arr.length, col = arr[0].length;
        int[][] res = new int[row][col];
        for(int i = 0; i < row; ++i){
            for(int j = 0; j < col; ++j){
                res[i][j] = (int)(arr[i][j] - '0');
            }
        }
        return res;
    }
    //read all input into a String array
    public static String[] read_all_String(){
        List<String> l = read_input();
        String[] arr = new String[l.size()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = l.get(i);
        }
        return arr;
    }

    public static <T> void print(T[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.print(aa[i] + "\t");
        System.out.println();
    }
    public static <T> void print(T[][] aa){
        for(int i = 0; i < aa.length; ++i){
            for(int j = 0; j < aa.length; ++j)
                System.out.print(aa[i][j] + "\t");
            System.out.println();
        }
        System.out.println();
    }
    public static void print(int[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.print(aa[i] + "\t");
        System.out.println();
    }
    public static void print(long[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.print(aa[i] + "\t");
        System.out.println();
    }
    public static void print(char[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.print(aa[i]);
        System.out.println();
    }
    public static void print(int[][] aa){
        for(int i = 0; i < aa.length; ++i){
            for(int j = 0; j < aa[0].length; ++j)
                System.out.print(aa[i][j] + "\t");
            System.out.println();
        }
        System.out.println();
    }
    public static void print(int[][][] aa){
        for(int k = 0; k < aa[0][0].length; ++k){
            for(int i = 0; i < aa.length; ++i){
                for(int j = 0; j < aa[0].length; ++j){
                    System.out.print(aa[i][j][k] + "\t");
                }
                System.out.println();
            }
            System.out.println();
        }
        System.out.println();
    }
    public static void print(char[][] aa){
        for(int i = 0; i < aa.length; ++i){
            for(int j = 0; j < aa[0].length; ++j)
                System.out.print(aa[i][j]);
            System.out.println();
        }
        System.out.println();
    }
    public static <T, U> void print(HashMap<T, U> map){
        for(T key : map.keySet()){
            System.out.println(key + " : " + map.get(key));
        }
        System.out.println();
    }
    public static <T> void print(HashSet<T> set){
        System.out.print("{");
        for(T key : set){
            System.out.print(key + ", ");
        }
        System.out.print("}");
        System.out.println();
    }
    public static <T> void print(List<T> list){
        System.out.print("[");
        for(T ele : list){
            System.out.print(ele + ", ");
        }
        System.out.print("]");
        System.out.println();
    }
    public static class Pair<_U, _T> { 
        _U a;
        _T b;
        public Pair(_U ap, _T bp){
            a = ap;
            b = bp;
        }
    }
    public static class Triple<_U, _T, _F>{
        _U a;
        _T b;
        _F c;
        public Triple(_U ap, _T bp, _F cp){
            a = ap;
            b = bp;
            c = cp;
        }
    }
    public static int[] dir = {1, 0, -1, 0, 1, 1, -1, -1, 1};
    public static class Union{
        int[] p, size;
        public Union(int n){
            p = new int[n];
            size = new int[n];
            Arrays.fill(size, 1);
            for(int i = 0; i < n; ++i){
                p[i] = i;
            }
        }
        public int find(int n){
            if(p[n] != n){
                p[n] = find(p[n]);
            }
            return p[n];
        }
        public int union(int a, int b){
            int fa = find(a), fb = find(b);
            if(fa == fb){
                return -1;
            }
            if(size[fa] > size[fb]){
                p[fb] = fa;
                size[fa] += size[fb];
                return size[fa];
            }
            else{
                p[fa] = fb;
                size[fb] += size[fa];
                return size[fb];
            }
        }
    }
    public static String read(){
        return sc.nextLine();
    }
    public static String[] read_toks(){
        return sc.nextLine().split(" ");
    }
}