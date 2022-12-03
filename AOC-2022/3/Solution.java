import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static HashSet<Character> setify(String S){
        HashSet<Character> set = new HashSet<>();
        char[] a = S.toCharArray();
        for(int i = 0; i < a.length; ++i){
            set.add(a[i]);
        }
        return set;
    }
    public static void main(String[] args){
        String[] arr = read_all_String();
        int ct = 0;
        HashMap<Character, Integer> map = new HashMap<>();
        for(int i = 0; i < 26; ++i){
            map.put((char)(i + (int)'a'), i + 1);
        }
        for(int i = 0; i < 26; ++i){
            map.put((char)(i + (int)'A'), i + 27);
        }
        loop: for(int i = 0; i < arr.length / 3; ++i){
            HashSet<Character> s1 = setify(arr[i * 3]), s2 = setify(arr[i * 3 + 1]), s3 = setify(arr[i * 3 + 2]);
            for(char c : s1){
                if(s2.contains(c) && s3.contains(c)){
                    ct += map.get(c);
                    continue loop;
                }
            }
        }
        System.out.println("Task 2: " + ct);
        //part 1
        ct = 0;
        loop: for(String S : arr){
            char[] b = S.substring(S.length() / 2, S.length()).toCharArray();
            HashSet<Character> set = setify(S.substring(0, S.length() / 2));
            for(int i = 0; i < b.length; ++i){
                if(set.contains(b[i])){
                    ct += map.get(b[i]);
                    continue loop;
                }
            }
        }
        System.out.println("Task 1: " + ct);
    }

    //template
    public static Scanner sc;
    static long MOD = 1000000007L;

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
    public static char[][] read_all_char_arr(){
        List<String> l = read_input();
        char[][] arr = new char[l.size()][l.get(0).length()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = l.get(i).toCharArray();
        }
        return arr;
    }
    public static int[] read_all_int(){
        List<String> l = read_input();
        int[] arr = new int[l.size()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = Integer.parseInt(l.get(i));
        }
        return arr;
    }
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