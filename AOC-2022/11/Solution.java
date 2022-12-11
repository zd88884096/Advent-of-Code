import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args){
        //idea: part I just simulate using Queue (first in first out)
        //idea: part II, observe when some value increase beyond the product of the x in all (divisible by x),
        //  modding it by the product of x (denoted by px) will not change its behavior in the subsequent rounds
        //  Because for all x, old % x = (old % px) % x (you can try to prove this by writing old as c * x + d, and px as e * c)
        //  And if we write out the transformation each value goes through in any monkey, we get
        //  I: (old * b) % x = ((old % x) * (b % x)) % x = ((old % px) * b) % x, which means we can change any old to old % px and result wouldn't be affected
        //  II: (old + b) % x = ((old % px) + b) % x with the same reason
        //  III: (old * old) % x = ((old % x) * old) % x = ((old % px) * old) % x = (old * (old % px)) % x = ((old % x) * (old % px)) % x = ((old % px) * (old % px)) % x
        //  So we conclude we can change any old to old % px, result wouldn't be affected

        //initialize
        String[] arr = read_all_String();
        int N = arr.length / 7 + 1;
        Queue<Long>[] q = new LinkedList[N];
        Triple<Long, Character, Long>[] ops = new Triple[N];
        long[] test = new long[N], inspected = new long[N];
        int[] true_targ = new int[N], false_targ = new int[N];
        long px = 1L;
        for(int i = 0; i < N; ++i){
            q[i] = new LinkedList<>();
        }
        
        //parse input
        for(int i = 0; i < arr.length; i += 7){
            String[] toks = split(arr[i + 1], "[ ,]");
            for(int item : stoi(subarray(toks, 2, toks.length)))
                q[i / 7].add((long)item);
            toks = split(arr[i + 2], " ");
            ops[i / 7] = new Triple(toks[3].equals("old") ? Long.MIN_VALUE : Long.parseLong(toks[3]), toks[4].charAt(0), toks[5].equals("old") ? Long.MIN_VALUE : Long.parseLong(toks[5]));
            toks = split(arr[i + 3], " ");
            test[i / 7] = Long.parseLong(toks[3]);
            px *= test[i / 7];
            toks = split(arr[i + 4], " ");
            true_targ[i / 7] = Integer.parseInt(toks[5]);
            toks = split(arr[i + 5], " ");
            false_targ[i / 7] = Integer.parseInt(toks[5]);
        }

        //simulate
        int round = 10000;
        for(int _e = 0; _e < round; ++_e){
            for(int i = 0; i < N; ++i){
                inspected[i] += q[i].size();
                while(!q[i].isEmpty()){
                    long cur = q[i].poll(), op1 = ops[i].a == Long.MIN_VALUE ? cur : ops[i].a, op2 = ops[i].c == Long.MIN_VALUE ? cur : ops[i].c, nvt = ops[i].b == '*' ? (op1 * op2) : (op1 + op2), nv = nvt / 3, nv2 = nvt % px;
                    //part I: 
                    //q[nv % test[i] == 0L ? true_targ[i] : false_targ[i]].add(nv);
                    q[nv2 % test[i] == 0L ? true_targ[i] : false_targ[i]].add(nv2);
                }
            }
        }
        Arrays.sort(inspected);
        //part I:
        //System.out.println("Task 1: " + (inspected[N - 1] * inspected[N - 2]));
        System.out.println("Task 2: " + (inspected[N - 1] * inspected[N - 2]));
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