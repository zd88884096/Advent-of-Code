import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static int[] subtract(int[] arr, int amount){
        for(int i = 0; i < arr.length; ++i){
            arr[i] -= amount;
        }
        return arr;
    }
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        /* part I: 
        //brute force simulate, only have to be careful when
        //  iterating large ranges, we instead iterate the intersection of those 
        //  ranges with [-50, 50] to speed things up
        int lower = -50, upper = 50, len = upper - lower + 1;
        boolean[][][] on = new boolean[len][len][len];
        for(String S : read_all_String()){
            String[] toks = split(S, "[ .=,xyz]");
            int[] range = subtract(stoi(subarray(toks, 1, toks.length)), lower);
            print(range);
            for(int i = Math.max(0, range[0]); i <= Math.min(len - 1, range[1]); ++i){
                for(int j = Math.max(0, range[2]); j <= Math.min(len - 1, range[3]); ++j){
                    for(int k = Math.max(0, range[4]); k <= Math.min(len - 1, range[5]); ++k){
                        on[i][j][k] = toks[0].equals("on");
                    }
                }
            }
            int ct = 0;
            for(int i = 0; i < len; ++i){
                for(int j = 0; j < len; ++j){
                    for(int k = 0; k < len; ++k){
                        ct += on[i][j][k] ? 1 : 0;
                    }
                }
            }
            System.out.println("Task 1: " + ct);
        }*/

        //part II, had a more complex, codeable idea
        //  adopted idea of coordinate compression from Neal Wu's video
        //  https://www.youtube.com/watch?v=YKpViLcTp64
        //  Just do coordinate compression, his video explains it pretty well
        TreeMap<Long, Integer>[] map = new TreeMap[3];
        TreeMap<Integer, Long>[] rev = new TreeMap[3];
        String[] arr = read_all_String();
        for(int i = 0; i < 3; ++i){
            map[i] = new TreeMap<>();
            rev[i] = new TreeMap<>();
        }
        for(String S : arr){
            String[] toks = split(S, "[ .=,xyz]");
            long[] range = stol(subarray(toks, 1, toks.length));
            for(int i = 0; i < 3; ++i){
                map[i].put(range[i * 2], -1);
                map[i].put(range[i * 2 + 1] + 1, -1);
            }
        }
        for(int i = 0; i < 3; ++i){
            int ind = 0;
            for(long ele : map[i].keySet()){
                //put them in order
                map[i].put(ele, ind);
                rev[i].put(ind, ele);
                ++ind;
            }
        }
        boolean[][][] on = new boolean[map[0].size() - 1][map[1].size() - 1][map[2].size() - 1];
        for(String S : arr){
            String[] toks = split(S, "[ .=,xyz]");
            long[] range = stol(subarray(toks, 1, toks.length));
            for(int i = map[0].get(range[0]); i < map[0].get(range[1] + 1); ++i){
                for(int j = map[1].get(range[2]); j < map[1].get(range[3] + 1); ++j){
                    for(int k = map[2].get(range[4]); k < map[2].get(range[5] + 1); ++k){
                        on[i][j][k] = toks[0].equals("on");
                    }
                }
            }
        }
        long ct = 0L;
        for(int i = 0; i < on.length; ++i){
            for(int j = 0; j < on[0].length; ++j){
                for(int k = 0; k < on[0][0].length; ++k){
                    ct += on[i][j][k] ? (rev[0].get(i + 1) - rev[0].get(i)) * (rev[1].get(j + 1) - rev[1].get(j)) * (rev[2].get(k + 1) - rev[2].get(k)) : 0L;
                }
            }
        }
        System.out.println(ct);
    }
    

    //template
    public static Scanner sc;
    static long MOD = 1000000007L;
    //(x + dir_card[i], y + dir_card[i + 1]) are 4 cardinal directions (for i in [0..3])
    public static int[] dir_card = {1, 0, -1, 0, 1};
    //same thing, 8 directions, for i in [0..7]
    public static int[] dir_all = {1, 0, -1, 0, 1, -1, -1, 1, 1};

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