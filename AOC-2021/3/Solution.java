import java.util.*;
import java.io.*;
import java.lang.Math;

public class Solution {
    public static Scanner sc;
    public static void main(String[] args){
        /* P1
        char[][] arr = list_to_char_arr(read_input());
        int row = arr.length, col = arr[0].length;
        long a = 0L, b = 0L;
        for(int i = 0; i < col; ++i){
            int ct = 0;
            for(int j = 0; j < row; ++j){
                if(arr[j][i] == '1'){
                    ++ct;
                }
            }
            a *= 2;
            b *= 2;
            if(ct > row - ct){
                ++a;
            }
            else{
                ++b;
            }
        }
        System.out.println(a + " " + b + " " + (a * b));
        */
        //p2
        char[][] arr = list_to_char_arr(read_input());
        int row = arr.length, col = arr[0].length;
        TreeSet<Integer> s1 = new TreeSet<>(), s2 = new TreeSet<>();
        for(int i = 0; i < row; ++i){
            s1.add(i);
            s2.add(i);
        }
        for(int j = 0; j < col; ++j){
            int ct = 0;
            for(int i : s1){
                //System.out.print(i + " ");
                if(arr[i][j] == '1'){
                    ++ct;
                }
            }
            //System.out.println();
            if(ct >= s1.size() - ct){
                //System.out.println("case 1:");
                for(int i = 0; i < row; ++i){
                    if(s1.size() > 1 && s1.contains(i) && arr[i][j] == '0'){
                        //System.out.println("removing: " + i);
                        s1.remove(i);
                    }
                }
            }
            else{
                //System.out.println("case 2:");
                for(int i = 0; i < row; ++i){
                    if(s1.size() > 1 && s1.contains(i) && arr[i][j] == '1'){
                        s1.remove(i);
                    }
                }
            }
        }
        for(int j = 0; j < col; ++j){
            int ct = 0;
            for(int i : s2){
                if(arr[i][j] == '1'){
                    ++ct;
                }
            }
            if(ct >= s2.size() - ct){
                for(int i = 0; i < row; ++i){
                    if(s2.size() > 1 && s2.contains(i) && arr[i][j] == '1'){
                        //System.out.println("removing: " + i);
                        s2.remove(i);
                    }
                }
            }
            else{
                for(int i = 0; i < row; ++i){
                    if(s2.size() > 1 && s2.contains(i) && arr[i][j] == '0'){
                        s2.remove(i);
                    }
                }
            }
        }
        int r1 = s1.first(), r2 = s2.first(), a1 = 0, a2 = 0;
        for(int j = 0; j < col; ++j){
            a1 *= 2;
            a2 *= 2;
            if(arr[r1][j] == '1'){
                ++a1;
            }
            if(arr[r2][j] == '1'){
                ++a2;
            }
        }
        System.out.println(a1 + " " + a2 + " " + (a1 * a2));
    }

    //template
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
    public static char[][] list_to_char_arr(List<String> l){
        char[][] arr = new char[l.size()][l.get(0).length()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = l.get(i).toCharArray();
        }
        return arr;
    }
    public static int[] list_to_int_arr(List<String> l){
        int[] arr = new int[l.size()];
        for(int i = 0; i < l.size(); ++i){
            arr[i] = Integer.parseInt(l.get(i));
        }
        return arr;
    }
    public static String[] list_to_String_arr(List<String> l){
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