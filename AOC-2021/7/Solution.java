import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static void main(String[] args){
        int[] val = stoi(split(read_all_String()[0], ","));

        // part II
        // just line sweep
        Arrays.sort(val);
        HashMap<Integer, Integer> map = new HashMap<>();
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        int n = val.length;
        for(int ele : val){
            min = Math.min(min, ele);
            max = Math.max(max, ele);
            map.put(ele, map.getOrDefault(ele, 0) + 1);
        }
        long cur = 0L;
        //start by computing the score if we start at min - 1 
        //  (for more efficient computation (no overlap with values in arr))

        //cur = current score when moved at position i
        //a1 = the amount of score to subtract (induced by the stuff on the right of i)
        //a2 = the number of stuff on the right of i
        //b1, b2, same thing as a1, a2 for the left
        long a1 = 0L, a2 = (long)n, b1 = 0L, b2 = 0L;
        for(int ele : val){
            cur += (ele - min + 1) * (ele - min + 2) / 2;
            a1 += (ele - min + 1);
        }
        //System.out.println(cur + " " + a1);
        long opt = cur;
        for(int i = min; i <= max; ++i){
            b1 += b2;
            cur = cur - a1 + b1;
            opt = Math.min(cur, opt);
            a1 -= a2;
            if(map.containsKey(i)){
                a2 -= map.get(i);
                b2 += map.get(i);
            }
            //System.out.println(i + " " + cur + " a1: " + a1 + " b1: " + b1);
            int true_val = 0;
            /*for(int ele : val){
                true_val += Math.abs(ele - i) * (Math.abs(ele - i) + 1) / 2;
            }
            System.out.println(true_val);*/
        }
        System.out.println(opt);
        // part I
        /*
        //medium is always the optimal solution
        Arrays.sort(val);
        long sum = 0L;
        for(int ele : val){
            sum += Math.abs(ele - val[val.length / 2]);
        }
        System.out.println(sum);*/

    }

    //template
    public static Scanner sc;
    static long MOD = 1000000007L;

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
    //read all input into a String array
    public static String[] read_all_String(){
        List<String> l = read_input();
        String[] arr = new String[l.size()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = l.get(i);
        }
        return arr;
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
    public static void print(String[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.println(aa[i]);
        System.out.println();
    }
    public static void print_short(int[] aa){
        for(int i = 0; i < aa.length; ++i)
            System.out.print(aa[i]);
        System.out.println();
    }
    public static void print(int[][] aa){
        for(int i = 0; i < aa.length; ++i){
            for(int j = 0; j < aa[0].length; ++j){
                System.out.print(aa[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }
    public static class Pair{
        int a;
        int b;
        public Pair(int ap, int bp){
            a = ap;
            b = bp;
        }
    }
    public static class Triple{
        int a;
        int b;
        int c;
        public Triple(int ap, int bp, int cp){
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