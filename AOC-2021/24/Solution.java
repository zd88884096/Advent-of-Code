import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static void main(String[] args){
        String[] arr = read_all_String();
        Stack<Pair> stack = new Stack<>();
        HashMap<Integer, Pair> relation = new HashMap<>();
        for(int i = 0; i < 14; ++i){
            int a = Integer.parseInt(arr[i * 18 + 4].split(" ")[2]), b = Integer.parseInt(arr[i * 18 + 5].split(" ")[2]), c = Integer.parseInt(arr[i * 18 + 15].split(" ")[2]);
            if(a == 1){
                stack.add(new Pair(i, c));
            }
            else{
                Pair p = stack.get(stack.size() - 1);
                stack.pop();
                int d2 = p.a, x = p.b;
                relation.put(i, new Pair(d2, b + x));
                relation.put(d2, new Pair(i, -1 * (b + x)));
            }
        }
        int[] res = new int[14];
        for(int i = 0; i < 14; ++i){
            if(res[i] != 0 || !relation.containsKey(i))
                continue;
            Pair p = relation.get(i);
            int d2 = p.a, x = p.b;
            if(x >= 0 && x <= 9){
                res[i] = 9;
                res[d2] = 9 - x;
            }
            else{
                res[i] = 9 + x;
                res[d2] = 9;
            }
        }
        print_short(res);
        res = new int[14];
        for(int i = 0; i < 14; ++i){
            if(res[i] != 0 || !relation.containsKey(i))
                continue;
            Pair p = relation.get(i);
            int d2 = p.a, x = p.b;
            if(x >= 0 && x <= 8){
                res[i] = 1 + x;
                res[d2] = 1;
            }
            else{
                res[i] = 1;
                res[d2] = 1 - x;
            }
        }
        print_short(res);
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